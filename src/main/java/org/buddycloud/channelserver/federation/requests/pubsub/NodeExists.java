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

public class NodeExists extends
		ChannelServerRequestAbstract<Boolean> {

	private String nodeId;

	public NodeExists(final ServiceDiscoveryRegistry discovery,
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
			ResultHandler<Boolean> handler) {

		IQ iq = new IQ(IQ.Type.get);

		Element query = iq.setChildElement("query",
				JabberPubsub.NAMESPACE_URI);

		query.addNamespace("", JabberPubsub.NS_DISCO_INFO);
		query.addAttribute("node", nodeId);
		
		query.addElement("actor");
		
		iq.setFrom(channelManager.getRequestParameters().getChannelsDomain());
		iq.getChildElement().element("actor")
				.addText(channelManager.getRequestParameters().getRequester().toBareJID());

		sendIq(iq, handler);
	}

	@Override
	public void call(ResultHandler<Boolean> handler) {
		
		boolean exists;
		try {
			exists = channelManager.nodeExists(nodeId);

			if ((exists != false) || (true == channelManager.isLocalNode(nodeId))) {
			    handler.onSuccess(exists);
				return;
			}
		} catch (NodeStoreException e) {
			handler.onError(e);
			return;
		}
		super.call(handler);
	}

	@Override
	protected Boolean fromIq(IQ iq) {
		// TODO Auto-generated method stub
		try {
			return iq.getType().equals("result");
		} catch (NullPointerException e) {
			return false;
		}
	}
}