package org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.set;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import com.surevine.spiffing.Label;
import org.apache.log4j.Logger;
import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.ValidatePayload;
import org.buddycloud.channelserver.channel.validate.PayloadValidator;
import org.buddycloud.channelserver.channel.validate.UnknownContentTypeException;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.PubSubElementProcessorAbstract;
import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeMembership;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.buddycloud.channelserver.pubsub.model.impl.NodeItemImpl;
import org.buddycloud.channelserver.pubsub.subscription.Subscriptions;
import org.buddycloud.channelserver.utils.XMLConstants;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.resultsetmanagement.ResultSet;

public class Publish extends PubSubElementProcessorAbstract {

    private static final Logger LOGGER = Logger.getLogger(Publish.class);

    private Element entry;
    private Label label;
    private JID publishersJID;
    private Element item;

    private PayloadValidator validator;


    public Publish(BlockingQueue<Packet> outQueue, ChannelManager channelManager) {
        this.outQueue = outQueue;
        this.channelManager = channelManager;

        acceptedElementName = XMLConstants.PUBLISH_ELEM;
    }

    @Override
    public void process(Element elm, JID actorJID, IQ reqIQ, Element rsm) throws Exception {

        request = reqIQ;
        response = IQ.createResultIQ(reqIQ);
        publishersJID = request.getFrom();

        node = request.getChildElement().element(XMLConstants.PUBLISH_ELEM).attributeValue(XMLConstants.NODE_ATTR);

        if (!checkNode()) {
            return;
        }

        boolean isLocalSubscriber = false;

        if (actorJID != null) {
            publishersJID = actorJID;
        } else {

            isLocalSubscriber = Configuration.getInstance().isLocalJID(publishersJID);

            // Check that user is registered.
            if (!isLocalSubscriber) {

                // If the packet did not have actor, and the sender is not a
                // local user.
                // publishing is not allowed.

                /*
                 * <iq type='error' from='pubsub.shakespeare.lit' to='hamlet@denmark.lit/elsinore'
                 * id='create1'> <error type='auth'> <registration-required
                 * xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/> </error> </iq>
                 */
                response.setType(Type.error);
                PacketError pe = new PacketError(org.xmpp.packet.PacketError.Condition.registration_required, org.xmpp.packet.PacketError.Type.auth);
                response.setError(pe);
                outQueue.put(response);
                return;
            }
        }

        try {
            if (!nodeExists()) {
                return;
            }
            if (!userCanPost()) {
                return;
            }
            if (!isRequestValid()) {
                return;
            }
            saveNodeItem();
            sendResponseStanza();
            sendNotifications();

        } catch (NodeStoreException e) {
            LOGGER.error("Problem with node store", e);
            setErrorCondition(PacketError.Type.wait, PacketError.Condition.internal_server_error);
            outQueue.put(response);
        } catch (UnknownContentTypeException e) {
            LOGGER.error(e);
            createExtendedErrorReply(PacketError.Type.modify, PacketError.Condition.not_acceptable, ValidatePayload.UNSUPPORTED_CONTENT_TYPE);
        }

    }

    private void saveNodeItem() throws NodeStoreException {
        // Let's store the new item.
        entry = validator.getPayload();
        label = validator.getLabel();
        String labelstr = null;
        if (label != null) {
            labelstr = label.toESSBase64();
        }
        channelManager.addNodeItem(new NodeItemImpl(node, this.validator.getLocalItemId(), new Date(), entry.asXML(),
                this.validator.getInReplyTo(), labelstr));
    }

    public void setEntryValidator(PayloadValidator validator) {
        this.validator = validator;
    }

    private PayloadValidator getPayloadValidator() throws Exception {
        if (null == this.validator) {
            this.validator = new ValidatePayload(channelManager, node).getValidator();
        }
        return this.validator;
    }

    private void sendInvalidPayloadResponse() throws InterruptedException {
        LOGGER.info("Payload is not valid: '" + validator.getErrorMessage() + "'.");
        createExtendedErrorReply(PacketError.Type.modify, PacketError.Condition.bad_request, validator.getErrorMessage());
        outQueue.put(response);
    }

    private boolean isRequestValid() throws Exception {
        item = request.getChildElement().element(acceptedElementName).element(XMLConstants.ITEM_ELEM);
        if (null == item) {
            createExtendedErrorReply(PacketError.Type.modify, PacketError.Condition.bad_request, XMLConstants.ITEM_REQUIRED_ELEM);
            outQueue.put(response);
            return false;
        }

        validator = getPayloadValidator();
        validator.setPayload(item);
        validator.setUser(publishersJID);
        validator.setTo(request.getTo().toBareJID());
        validator.setNode(node);
        validator.setChannelManager(channelManager);

        if (!validator.isValid()) {
            sendInvalidPayloadResponse();
            return false;
        }
        return true;
    }

    private boolean userCanPost() throws NodeStoreException, InterruptedException {

        NodeMembership membership = channelManager.getNodeMembership(node, publishersJID);

        if ((!membership.getSubscription().equals(Subscriptions.subscribed))
                || (!membership.getAffiliation().in(Affiliations.moderator, Affiliations.owner, Affiliations.publisher))) {
            response.setType(Type.error);
            PacketError error = new PacketError(PacketError.Condition.forbidden, PacketError.Type.auth);
            response.setError(error);
            outQueue.put(response);
            return false;
        }
        return true;
    }

    private void sendResponseStanza() throws InterruptedException {
        /*
         * Success, let's response as defined in
         * http://xmpp.org/extensions/xep-0060.html#publisher-publish - 7.1.2 Success Case
         */
        Element pubsub = new DOMElement(XMLConstants.PUBSUB_ELEM, new org.dom4j.Namespace("", JabberPubsub.NAMESPACE_URI));

        Element publish = pubsub.addElement(XMLConstants.PUBLISH_ELEM);
        publish.addAttribute(XMLConstants.NODE_ATTR, node);

        Element newItem = publish.addElement(XMLConstants.ITEM_ELEM);
        newItem.addAttribute(XMLConstants.ID_ATTR, validator.getGlobalItemId());

        response.setChildElement(pubsub);
        outQueue.put(response);
    }

    private boolean nodeExists() throws Exception {
        if (true == channelManager.nodeExists(node)) {
            return true;
        }
        response.setType(Type.error);
        PacketError error = new PacketError(PacketError.Condition.item_not_found, PacketError.Type.cancel);
        response.setError(error);
        outQueue.put(response);
        return false;
    }

    private boolean checkNode() throws InterruptedException, NodeStoreException {
        if (node == null || "".equals(node)) {
            response.setType(Type.error);

            Element badRequest = new DOMElement(PacketError.Condition.bad_request.toXMPP(), new org.dom4j.Namespace("", JabberPubsub.NS_XMPP_STANZAS));

            Element nodeIdRequired = new DOMElement(XMLConstants.NODE_ID_REQUIRED, new org.dom4j.Namespace("", JabberPubsub.NS_PUBSUB_ERROR));

            Element error = new DOMElement(XMLConstants.ERROR_ELEM);
            error.addAttribute(XMLConstants.TYPE_ATTR, PacketError.Type.modify.toXMPP());
            error.add(badRequest);
            error.add(nodeIdRequired);

            response.setChildElement(error);

            outQueue.put(response);
            return false;
        }
        boolean isLocalNode = false;
        try {
            isLocalNode = Configuration.getInstance().isLocalNode(node);
        } catch (IllegalArgumentException e) {
            response.setType(Type.error);
            PacketError pe = new PacketError(PacketError.Condition.bad_request, PacketError.Type.modify);
            response.setError(pe);
            LOGGER.error(e);
            outQueue.put(response);
            return false;
        }

        if (!isLocalNode) {
            makeRemoteRequest();
            return false;
        }
        return true;
    }

    private void sendNotifications() throws NodeStoreException, InterruptedException {
        // Let's send notifications as defined in 7.1.2.1 Notification With
        // Payload
        Message msg = new Message();
        msg.getElement().addAttribute("remote-server-discover", "false");
        msg.setType(Message.Type.headline);
        msg.setFrom(request.getTo());
        Element event = msg.addChildElement("event", JabberPubsub.NS_PUBSUB_EVENT);
        Element items = event.addElement("items");
        items.addAttribute("node", node);
        Element i = items.addElement("item");
        i.addAttribute("id", validator.getGlobalItemId());
        i.add(entry.createCopy());
        if (label != null) {
            Element seclabel = msg.addChildElement("securitylabel", "urn:xmpp:sec-label:0");
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
            Element ess = seclabel.addElement("esssecuritylabel", "urn:xmpp:sec-label:ess:0");
            ess.setText(label.toESSBase64());
        }

        ResultSet<NodeSubscription> cur = channelManager.getNodeSubscriptionListeners(node);

        for (NodeSubscription ns : cur) {
            JID to = ns.getUser();
            if (ns.getSubscription().equals(Subscriptions.subscribed)) {
                LOGGER.debug("Sending post notification to " + to.toBareJID());
                msg.setTo(ns.getListener());
                outQueue.put(msg.createCopy());
            }
        }

        Collection<JID> admins = getAdminUsers();
        for (JID admin : admins) {
            msg.setTo(admin);
            outQueue.put(msg.createCopy());
        }
    }

}
