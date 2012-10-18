package org.buddycloud.channelserver.federation.requests.pubsub;

import static org.junit.Assert.*;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class GetNodeItemsTest {

	private String node = "romeo@shakespeare.lit";
	private ServiceDiscoveryRegister register;
	private XMPPConnection connection;
	private GetNodeItems processor;
	
	@Before
	public void setUp() throws Exception {
		
		registry = Mockito.mock(ServiceDiscoveryRegistry.class);
		connection = Mockito.mock(XMPPConnection.class);
		processor = new GetNodeItems(register, connection, node);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
