package org.buddycloud.channelserver.federation.requests.pubsub;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.channel.connection.MockConnection;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.CloseableIterator;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.federation.AsyncCall.ResultHandler;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry.JIDDiscoveryHandler;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmpp.packet.JID;

public class GetNodeItemsTest {

	private String node = "/user/romeo@shakespeare.lit/posts";
	private ServiceDiscoveryRegistry registry;
	private MockConnection connection;
	private Parameters requestParameters;
	private GetNodeItems processor;
	
	/**
	 * Result and error holders
	 */
	final ArrayList<CloseableIterator<NodeItem>> result = new ArrayList<CloseableIterator<NodeItem>>(
			1);
	final ArrayList<Throwable> error = new ArrayList<Throwable>(1);
	
	@Before
	public void setUp() throws Exception {
		
		requestParameters = new Parameters();
		requestParameters.setChannelsDomain("channels.shakespeare.lit");
		requestParameters.setTopicsDomain("topics.shakespeare.lit");
		requestParameters.setRequester(new JID("romeo@shakespeare.lit"));
		
		ChannelManager channelManager = Mockito.mock(ChannelManager.class);
		Mockito.when(channelManager.getRequestParameters()).thenReturn(requestParameters);
		Mockito.when(channelManager.getNodeItems(Mockito.anyString())).thenReturn(null);
		
		registry = Mockito.mock(ServiceDiscoveryRegistry.class);
		
		connection = new MockConnection();
		
		processor = new GetNodeItems(registry, connection, node, channelManager);
	}

	@Test
	public void testSomething() {
		
		//Mockito.when(registry.discoverChannelServerJID(Mockito.any(JID.class), Mockito.any(JIDDiscoveryHandler.class));
		processor.call(new ResultHandler<CloseableIterator<NodeItem>>() {
			@Override
			public void onSuccess(CloseableIterator<NodeItem> items) {
				result.set(0, items);
			}
			@Override
			public void onError(Throwable t) {
				error.set(0, t);
			}
		});
		System.out.println(result.get(0));
		System.out.println(connection.getLastPacket().toXML());
		System.out.println(result.get(0));
	}
}