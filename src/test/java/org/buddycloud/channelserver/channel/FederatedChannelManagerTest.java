package org.buddycloud.channelserver.channel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.omg.CORBA.portable.ResponseHandler;
import org.xmpp.packet.IQ;

public class FederatedChannelManagerTest {

	private static final String TEST_LOCAL_NODE_ID = "users/user@server.com/posts";
	private static final String TEST_REMOTE_NODE_ID = "users/user@otherserver.com/posts";
	
	@Mock
	ChannelManager channelManager;
	
	@Mock
	XMPPConnection xmppConnection;
	
	@Mock
	ServiceDiscoveryRegistry discoveryRegistry;
	
	@Mock
	OperationsFactory operations;
	
	/**
	 * Class under test
	 */
	FederatedChannelManager federatedChannelManager;
	
	@Before
	public void setUp() throws Exception {
		federatedChannelManager = new FederatedChannelManager(channelManager, xmppConnection, discoveryRegistry, operations);
	}

	@Test
	public void testGetNodeItemsStringForRemoteNode() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					federatedChannelManager.getNodeItems(TEST_REMOTE_NODE_ID);
				} catch (NodeStoreException e) {
					e.printStackTrace();
					fail("getNodeItems threw an exception");
				}
			}
			
		};
		
		
		
//		verify(xmppConnection.sendIQ(any(IQ.class), any(XMPPConnection.IQHandler.class)));
		
//		Thread t = new Thread(r);
//		t.start();
	}

}
