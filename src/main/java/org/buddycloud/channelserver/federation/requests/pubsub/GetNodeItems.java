package org.buddycloud.channelserver.federation.requests.pubsub;

import java.util.Collection;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.ChannelNodeRef;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.CloseableIterator;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.ChannelServerRequestAbstract;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.Buddycloud;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class GetNodeItems extends
		ChannelServerRequestAbstract<CloseableIterator<NodeItem>> {

	private String nodeId;

	public GetNodeItems(final ServiceDiscoveryRegistry discovery,
			final XMPPConnection connection, final String nodeId,
			ChannelManager channelManager) {
		super(discovery, connection, channelManager);
		this.nodeId = nodeId;
	}

	@Override
	protected JID getToJid() {
		ChannelNodeRef nodeRef = ChannelNodeRef.fromNodeId(nodeId);
		return nodeRef.getJID();
	}

	@Override
	protected void sendRequest(JID channelServer,
			ResultHandler<CloseableIterator<NodeItem>> handler) {

		IQ iq = new IQ(IQ.Type.get);

		Element pubsub = iq.setChildElement("pubsub",
				JabberPubsub.NAMESPACE_URI);

		Element items = pubsub.addElement("items");
		items.addAttribute("node", nodeId);
		
		iq.setFrom(channelManager.getRequestParameters().getChannelsDomain());
		iq.getChildElement().element("actor")
				.addText(channelManager.getRequestParameters().getRequester().toBareJID());
		//iq.setTo(discovery.discoverChannelServerJIDFromNodeId(nodeId, handler));

		sendIq(iq, handler);
	}

	@Override
	public void call(ResultHandler<CloseableIterator<NodeItem>> handler) {
		
		CloseableIterator<NodeItem> items;
		try {
			items = channelManager.getNodeItems(nodeId);

			if ((items != null) && (items.hasNext() || channelManager.isLocalNode(nodeId))) {
				handler.onSuccess(items);
				return;
			}
		} catch (NodeStoreException e) {
			handler.onError(e);
			return;
		}
		super.call(handler);
	}

	@Override
	protected CloseableIterator<NodeItem> fromIq(IQ iq) {
		// TODO Auto-generated method stub
		return null;
	}
}
