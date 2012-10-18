package org.buddycloud.channelserver.channel;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.federation.requests.pubsub.GetNodeItems;
import org.buddycloud.channelserver.utils.request.Parameters;

public class OperationsFactory {
	private final ServiceDiscoveryRegistry discovery;
	private final XMPPConnection connection;
	private final Parameters requestParameters;
	
	public OperationsFactory(ServiceDiscoveryRegistry discovery,
			XMPPConnection connection, Parameters requestParameters) {
		this.discovery = discovery;
		this.connection = connection;
		this.requestParameters = requestParameters;
	}

	public GetNodeItems getNodeItems(final String nodeId) {
		return new GetNodeItems(discovery, connection, nodeId, requestParameters);
	}
}
