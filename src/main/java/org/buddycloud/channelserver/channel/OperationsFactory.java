package org.buddycloud.channelserver.channel;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.federation.requests.pubsub.GetNodeItems;
import org.buddycloud.channelserver.federation.requests.pubsub.NodeExists;
import org.buddycloud.channelserver.utils.request.Parameters;

public class OperationsFactory {
	private final ServiceDiscoveryRegistry discovery;
	private final XMPPConnection connection;
	private final ChannelManager channelManager;
	
	public OperationsFactory(ServiceDiscoveryRegistry discovery,
			XMPPConnection connection, ChannelManager channelManager) {
		this.discovery = discovery;
		this.connection = connection;
		this.channelManager = channelManager;
	}

	public GetNodeItems getNodeItems(final String nodeId) {
		return new GetNodeItems(discovery, connection, nodeId, channelManager);
	}
	
	public NodeExists nodeExists(final String nodeId) {
		return new NodeExists(discovery, connection, nodeId, channelManager);
	}
}
