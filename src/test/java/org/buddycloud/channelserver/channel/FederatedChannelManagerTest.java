package org.buddycloud.channelserver.channel;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class FederatedChannelManagerTest {

	private static final String TEST_NODE_ID = "users/user@server.com/posts";
	
	@Mock
	ChannelManager channelManager;
	
	@Mock
	XMPPConnection xmppConnection;
	
	@Mock
	ServiceDiscoveryRegistry discoveryRegistry;
	
	/**
	 * Class under test
	 */
	FederatedChannelManager federatedChannelManager;
	
	@Before
	public void setUp() throws Exception {
		federatedChannelManager = new FederatedChannelManager(channelManager, xmppConnection, discoveryRegistry);
	}

	@Test
	public void testGetNodeItemsString() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					federatedChannelManager.getNodeItems(TEST_NODE_ID);
				} catch (NodeStoreException e) {
					e.printStackTrace();
					fail("getNodeItems threw an exception");
				}
			}
			
		};
		
		Thread t = new Thread(r);
		t.start();
		
		
	}

}
