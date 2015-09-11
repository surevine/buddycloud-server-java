package org.buddycloud.channelserver.db.jdbc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.packetHandler.iq.IQTestHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmpp.packet.JID;

public class JDBCNodeStoreRemoteAndLocalNodesTest extends JDBCNodeStoreAbstract {

    public JDBCNodeStoreRemoteAndLocalNodesTest() throws SQLException, IOException,
            ClassNotFoundException {
        dbTester = new DatabaseTester();
        IQTestHandler.readConf();
    }
    
    @Before
    public void setup() throws SQLException, IOException, NodeStoreException {
        dbTester.loadData("node_1");
        dbTester.loadData("node_2");
        dbTester.loadData("advertise_nodes");
        store.addRemoteNode(TEST_SERVER2_NODE1_ID);
    }

    @Test
    public void testGetLocalNodesListAsLocalUser() throws Exception {
        List<String> localNodes = store.getLocalNodesList(new JID("user1@server1"));
        Assert.assertTrue(localNodes.contains(TEST_SERVER1_NODE1_ID));
        Assert.assertTrue(localNodes.contains(TEST_SERVER1_NODE2_ID));
        Assert.assertFalse(localNodes.contains(TEST_SERVER2_NODE1_ID));
        Assert.assertTrue(localNodes.contains("/user/advertised@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/undetermined-advertised@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/not-advertised@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/advertised@server2/posts"));
        Assert.assertFalse(localNodes.contains("/user/undetermined-advertised@server2/posts"));
        Assert.assertFalse(localNodes.contains("/user/not-advertised@server2/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-private-never@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-private-locally@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-private-joinable@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-private-always@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-local-never@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-local-locally@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-local-joinable@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-local-always@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-open-never@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-open-locally@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-open-joinable@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-open-always@server1/posts"));
        Assert.assertEquals(12, localNodes.size());
    }

    @Test
    public void testGetLocalNodesListAsRemoteUser() throws Exception {
        List<String> localNodes = store.getLocalNodesList(new JID("user1@server2"));
        Assert.assertTrue(localNodes.contains(TEST_SERVER1_NODE1_ID));
        Assert.assertTrue(localNodes.contains(TEST_SERVER1_NODE2_ID));
        Assert.assertFalse(localNodes.contains(TEST_SERVER2_NODE1_ID));
        Assert.assertTrue(localNodes.contains("/user/advertised@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/undetermined-advertised@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/not-advertised@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/advertised@server2/posts"));
        Assert.assertFalse(localNodes.contains("/user/undetermined-advertised@server2/posts"));
        Assert.assertFalse(localNodes.contains("/user/not-advertised@server2/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-private-never@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-private-locally@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-private-joinable@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-private-always@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-local-never@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-local-locally@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-local-joinable@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-local-always@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-open-never@server1/posts"));
        Assert.assertFalse(localNodes.contains("/user/zz-open-locally@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-open-joinable@server1/posts"));
        Assert.assertTrue(localNodes.contains("/user/zz-open-always@server1/posts"));
        Assert.assertEquals(8, localNodes.size());
    }

    @Test
    public void testGetRemoteNodesList() throws Exception {
        List<String> remoteNodes = store.getRemoteNodesList();
        Assert.assertFalse(remoteNodes.contains(TEST_SERVER1_NODE1_ID));
        Assert.assertFalse(remoteNodes.contains(TEST_SERVER1_NODE2_ID));
        Assert.assertTrue(remoteNodes.contains(TEST_SERVER2_NODE1_ID));
        Assert.assertFalse(remoteNodes.contains("/user/advertised@server1/posts"));
        Assert.assertFalse(remoteNodes.contains("/user/undetermined-advertised@server1/posts"));
        Assert.assertFalse(remoteNodes.contains("/user/not-advertised@server1/posts"));
        Assert.assertTrue(remoteNodes.contains("/user/advertised@server2/posts"));
        Assert.assertTrue(remoteNodes.contains("/user/undetermined-advertised@server2/posts"));
        Assert.assertFalse(remoteNodes.contains("/user/not-advertised@server2/posts"));
        Assert.assertEquals(3, remoteNodes.size());
    }
}
