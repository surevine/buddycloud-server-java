package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.get;

import java.io.StringReader;
import java.util.concurrent.BlockingQueue;

import com.surevine.spiffing.*;
import org.apache.log4j.Logger;
import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.node.configuration.field.AccessModel;
import org.buddycloud.channelserver.db.CloseableIterator;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessorAbstract;
import org.buddycloud.channelserver.pubsub.accessmodel.AccessModels;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.utils.XMLConstants;
import org.buddycloud.channelserver.utils.node.item.payload.Buddycloud;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

public class ThreadGet extends PubSubElementProcessorAbstract {

    private Element pubsub;
    private SAXReader xmlReader;

    // RSM details
    private String firstItemId = null;
    private String lastItemId = null;
    private String afterItemId = null;
    private int maxResults = -1;

    private static final Logger LOGGER = Logger.getLogger(RecentItemsGet.class);

    public ThreadGet(BlockingQueue<Packet> outQueue, ChannelManager channelManager) {
        setChannelManager(channelManager);
        setOutQueue(outQueue);

        xmlReader = new SAXReader();

        acceptedElementName = XMLConstants.THREAD_ELEM;
    }

    @Override
    public void process(Element elm, JID actorJID, IQ reqIQ, Element rsm) throws Exception {
        response = IQ.createResultIQ(reqIQ);
        request = reqIQ;
        actor = actorJID;
        resultSetManagement = rsm;

        if (null == actor) {
            actor = request.getFrom();
        }

        if (!isValidStanza()) {
            outQueue.put(response);
            return;
        }

        try {
            if (!Configuration.getInstance().isLocalJID(request.getFrom())) {
                response.getElement().addAttribute(XMLConstants.REMOTE_SERVER_DISCOVER_ATTR, Boolean.FALSE.toString());
            }
            pubsub = response.getElement().addElement(XMLConstants.PUBSUB_ELEM, JabberPubsub.NAMESPACE_URI);
            if ((!userCanViewNode()) || (!itemExists())) {

                outQueue.put(response);
                return;
            }
            parseRsmElement();
            addItems();
            addRsmElement();

        } catch (NodeStoreException e) {
            LOGGER.error(e);
            response.getElement().remove(pubsub);
            setErrorCondition(PacketError.Type.wait, PacketError.Condition.internal_server_error);
        }

        outQueue.put(response);
    }

    private boolean itemExists() throws NodeStoreException {
        if (null != channelManager.getNodeItem(node, parentId)) {
            return true;
        }
        createExtendedErrorReply(PacketError.Type.cancel, PacketError.Condition.item_not_found, "parent-item-not-found", Buddycloud.NS_ERROR);
        return false;
    }

    protected boolean parseRsmElement() {
        Element rsmElement = request.getChildElement().element(XMLConstants.SET_ELEM);
        if (null == rsmElement) {
            return true;
        }
        Element max;
        Element after;
        if (null != (max = rsmElement.element("max"))) {
            maxResults = Integer.parseInt(max.getTextTrim());
        }
        if (null != (after = rsmElement.element("after"))) {
            afterItemId = after.getTextTrim();
        }

        return true;
    }

    protected void addRsmElement() throws NodeStoreException {
        if (null == firstItemId) {
            return;
        }
        Element rsm = pubsub.addElement("set");
        rsm.addNamespace("", NS_RSM);
        rsm.addElement("first").setText(firstItemId);
        rsm.addElement("last").setText(lastItemId);
        rsm.addElement("count").setText(String.valueOf(channelManager.getCountNodeThread(node, parentId)));
    }

    private void addItems() throws NodeStoreException {

        CloseableIterator<NodeItem> items = channelManager.getNodeItemThread(node, parentId, afterItemId, maxResults);
        NodeItem item;
        Element entry;
        Element itemElement;
        Element itemsElement = pubsub.addElement("items");
        itemsElement.addAttribute("node", node);

        while (items.hasNext()) {
            item = items.next();

            try {
                entry = xmlReader.read(new StringReader(item.getPayload())).getRootElement();
                itemElement = itemsElement.addElement("item");
                itemElement.addAttribute("id", item.getId());
                if (null == firstItemId) {
                    firstItemId = item.getId();
                }
                lastItemId = item.getId();
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
            } catch (DocumentException e) {
                LOGGER.error("Error parsing a node entry, ignoring. " + item.getId());
            } catch (SIOException e) {
                LOGGER.error("Error handling item label, " + item.getId() + "discarding.", e);
            }
        }
    }

    public AccessModels getNodeAccessModel() {
        if (!nodeConfiguration.containsKey(AccessModel.FIELD_NAME)) {
            return AccessModels.authorize;
        }
        return AccessModels.createFromString(nodeConfiguration.get(AccessModel.FIELD_NAME));
    }
}
