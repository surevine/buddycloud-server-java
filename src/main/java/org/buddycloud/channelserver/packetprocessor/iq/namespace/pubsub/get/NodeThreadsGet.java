package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.surevine.spiffing.Label;
import org.apache.log4j.Logger;
import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.Conf;
import org.buddycloud.channelserver.channel.node.configuration.field.AccessModel;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessorAbstract;
import org.buddycloud.channelserver.pubsub.accessmodel.AccessModels;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.pubsub.model.NodeThread;
import org.buddycloud.channelserver.utils.XMLConstants;
import org.buddycloud.channelserver.utils.node.NodeAclRefuseReason;
import org.buddycloud.channelserver.utils.node.NodeViewAcl;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.resultsetmanagement.ResultSet;

public class NodeThreadsGet extends PubSubElementProcessorAbstract {

    private static final Logger LOGGER = Logger.getLogger(NodeThreadsGet.class);

    private static final int MAX_THREADS_TO_RETURN = 15;
    private int max;
    private String afterId;

    public NodeThreadsGet(BlockingQueue<Packet> outQueue, ChannelManager channelManager) {
        setChannelManager(channelManager);
        setOutQueue(outQueue);

        acceptedElementName = XMLConstants.THREADS_ELEM;
    }

    @Override
    public void process(Element elm, JID actorJID, IQ reqIQ, Element rsm) throws Exception {
        this.request = reqIQ;
        this.response = IQ.createResultIQ(request);
        this.actor = actorJID;
        this.resultSetManagement = rsm;
        this.max = MAX_THREADS_TO_RETURN;

        if (actor == null) {
            actor = request.getFrom();
        }

        if (!Configuration.getInstance().isLocalJID(request.getFrom())) {
            response.getElement().addAttribute(XMLConstants.REMOTE_SERVER_DISCOVER_ATTR, Boolean.FALSE.toString());
        }

        if (!isValidStanza()) {
            outQueue.put(response);
            return;
        }

        if (!Configuration.getInstance().isLocalNode(node) && !channelManager.isCachedNode(node)) {
            LOGGER.debug("Node " + node + " is remote and not cached, off to get some data");

            makeRemoteRequest();
            return;
        }

        if (!checkNodeExists() || !userCanViewNode() || !parseRsmElement()) {
            outQueue.put(response);
            return;
        }
        getNodeThreads();
        addRsmElement();
        outQueue.put(response);
    }

    protected void addRsmElement() throws NodeStoreException {
        if (firstItem == null) {
            return;
        }
        Element pubsubEl = response.getElement().element(XMLConstants.PUBSUB_ELEM);
        Element rsm = pubsubEl.addElement(XMLConstants.SET_ELEM, NS_RSM);
        rsm.addElement("first", NS_RSM).setText(firstItem);
        rsm.addElement("last", NS_RSM).setText(lastItem);

        Integer nodeThreadCount = channelManager.countNodeThreads(node);
        rsm.addElement("count", NS_RSM).setText(nodeThreadCount.toString());
    }

    private void getNodeThreads() throws NodeStoreException, DocumentException {
        ResultSet<NodeThread> nodeThreads = channelManager.getNodeThreads(node, afterId, max);
        Element pubsubEl = response.getElement().addElement(XMLConstants.PUBSUB_ELEM, JabberPubsub.NAMESPACE_URI);
        SAXReader xmlReader = new SAXReader();
        for (NodeThread nodeThread : nodeThreads) {
            Element threadEl = pubsubEl.addElement(XMLConstants.THREAD_ELEM);
            threadEl.addAttribute(XMLConstants.NODE_ATTR, node);
            threadEl.addAttribute(XMLConstants.ID_ATTR, nodeThread.getId());
            threadEl.addAttribute(XMLConstants.UPDATED_ATTR, Conf.formatDate(nodeThread.getUpdated()));
            ResultSet<NodeItem> items = nodeThread.getItems();
            for (NodeItem item : items) {
                Element entry = xmlReader.read(new StringReader(item.getPayload())).getRootElement();
                Element itemElement = threadEl.addElement(XMLConstants.ITEM_ELEM);
                itemElement.addAttribute(XMLConstants.ID_ATTR, item.getId());
                itemElement.add(entry);
                // Label
                LOGGER.info("Item has label of " + item.getLabel());
                Label label = new Label(item.getLabel());
                if (label != null) {
                    Element seclabel = itemElement.addElement("securitylabel", "urn:xmpp:sec-label:0");
                    Element marking = seclabel.addElement("displaymarking");
                    marking.setText(label.displayMarking());
                    String fg = label.fgColour();
                    if (fg != null) {
                        marking.addAttribute("fgcolor", fg);
                    }
                    String bg = label.bgColour();
                    if (bg != null) {
                        marking.addAttribute("bgcolor", bg);
                    }
                    Element labelwrap = seclabel.addElement("label");
                    Element ess = labelwrap.addElement("esssecuritylabel", "urnq:xmpp:sec-label:ess:0");
                    ess.setText(label.toESSBase64());
                }
            }
        }
        if (!nodeThreads.isEmpty()) {
            this.firstItem = nodeThreads.getFirst(1).iterator().next().getId();
            this.lastItem = nodeThreads.getLast(1).iterator().next().getId();
        }
    }

    @Override
    protected boolean isValidStanza() {
        try {
            this.node = request.getChildElement().element(acceptedElementName).attributeValue(XMLConstants.NODE_ATTR);
            if (node != null) {
                return true;
            }
        } catch (NullPointerException e) {
            LOGGER.error(e);
        }
        createExtendedErrorReply(PacketError.Type.modify, PacketError.Condition.bad_request, XMLConstants.NODE_ID_REQUIRED);
        return false;
    }


    private AccessModels getNodeAccessModel(Map<String, String> nodeConfiguration) {
        if (!nodeConfiguration.containsKey(AccessModel.FIELD_NAME)) {
            return AccessModels.authorize;
        }
        return AccessModels.createFromString(nodeConfiguration.get(AccessModel.FIELD_NAME));
    }

    protected boolean userCanViewNode() throws NodeStoreException {
        NodeViewAcl nodeViewAcl = new NodeViewAcl();
        Map<String, String> nodeConfiguration = channelManager.getNodeConf(node);

        if (nodeViewAcl.canViewNode(node, channelManager.getNodeMembership(node, actor), getNodeAccessModel(nodeConfiguration), Configuration
                .getInstance().isLocalJID(actor))) {
            return true;
        }

        NodeAclRefuseReason reason = nodeViewAcl.getReason();
        createExtendedErrorReply(reason.getType(), reason.getCondition(), reason.getAdditionalErrorElement());
        return false;
    }

    protected boolean parseRsmElement() throws NodeStoreException {
        if (resultSetManagement == null) {
            return true;
        }
        Element maxEl = resultSetManagement.element("max");
        if (maxEl != null) {
            this.max = Integer.parseInt(maxEl.getTextTrim());
        }
        Element afterEl = resultSetManagement.element("after");
        if (afterEl != null) {
            this.afterId = afterEl.getTextTrim();
            if (channelManager.getNodeItem(node, afterId) == null) {
                setErrorCondition(PacketError.Type.cancel, PacketError.Condition.item_not_found);
                return false;
            }
        }

        return true;
    }

}
