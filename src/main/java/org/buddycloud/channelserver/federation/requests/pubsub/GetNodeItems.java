package org.buddycloud.channelserver.federation.requests.pubsub;

import java.util.Collection;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.ChannelNodeRef;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.CloseableIterator;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.ChannelServerRequestAbstract;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class GetNodeItems extends ChannelServerRequestAbstract<CloseableIterator<NodeItem>> {

	private XMPPConnection connection;
	private ChannelManager channelManager;
	
	private String nodeId;
	
	public GetNodeItems(final ServiceDiscoveryRegistry discovery, final XMPPConnection connection, final String nodeId) {
		super(discovery);
		
		this.connection = connection;
		this.nodeId = nodeId;
	}

	@Override
	protected JID getToJid() {
		ChannelNodeRef nodeRef = ChannelNodeRef.fromNodeId(nodeId);
		return nodeRef.getJID();
	}

	@Override
	protected void sendRequest(
			JID channelServer, ResultHandler<CloseableIterator<NodeItem>> handler) {
		IQ iq = new IQ(IQ.Type.get);
		
		Element query = iq.setChildElement("query", JabberPubsub.NAMESPACE_URI);
		
		
	}

	@Override
	public void call(ResultHandler<CloseableIterator<NodeItem>> handler) {
		// TODO Auto-generated method stub
		CloseableIterator<NodeItem> items;
		
		try {
			items = channelManager.getNodeItems(nodeId);
		
			if(items.hasNext() || channelManager.isLocalNode(nodeId)) {
				handler.onSuccess(items);
				return;
			}
		} catch (NodeStoreException e) {
			handler.onError(e);
			return;
		}
		
		super.call(handler);
	}

}
