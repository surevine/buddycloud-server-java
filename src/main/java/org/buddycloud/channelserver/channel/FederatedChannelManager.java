package org.buddycloud.channelserver.channel;

import java.util.Collection;
import java.util.Map;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.CloseableIterator;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.AsyncCall.ResultHandler;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.federation.requests.pubsub.GetNodeItems;
import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.pubsub.model.NodeSubscription;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.xmpp.packet.JID;

public class FederatedChannelManager implements ChannelManager {

	private final ChannelManager delegate;
	private final XMPPConnection xmppConnection;
	private final ServiceDiscoveryRegistry discoveryRegistry;
	private final OperationsFactory operations;
	private Parameters requestParameters;

	public FederatedChannelManager(final ChannelManager delgate,
			final XMPPConnection xmppConnection,
			final ServiceDiscoveryRegistry discoveryRegistry, final OperationsFactory operationsFactory) {
		this.delegate = delgate;
		this.xmppConnection = xmppConnection;
		this.discoveryRegistry = discoveryRegistry;
		this.operations = operationsFactory;
	}

	@Override
	public void createNode(JID owner, String nodeId,
			Map<String, String> nodeConf) throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNodeConfValue(String nodeId, String key, String value)
			throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNodeConf(String nodeId, Map<String, String> conf)
			throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNodeConfValue(String nodeId, String key)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getNodeConf(String nodeId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean nodeExists(String nodeId) throws NodeStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUserAffiliation(String nodeId, JID user,
			Affiliations affiliation) throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUserSubscription(NodeSubscription subscription)
			throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public NodeAffiliation getUserAffiliation(String nodeId, JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<NodeAffiliation> getUserAffiliations(JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<NodeAffiliation> getNodeAffiliations(String nodeId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<NodeSubscription> getUserSubscriptions(JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<NodeSubscription> getNodeSubscriptions(String nodeId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSubscription getUserSubscription(String nodeId, JID user)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CloseableIterator<NodeItem> getNodeItems(String nodeId,
			String afterItemId, int count) throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CloseableIterator<NodeItem> getNodeItems(String nodeId)
			throws NodeStoreException {
		final ObjectHolder<CloseableIterator<NodeItem>> result = new ObjectHolder<CloseableIterator<NodeItem>>();
		final ObjectHolder<Throwable> error = new ObjectHolder<Throwable>();

		GetNodeItems getNodeItems = operations.getNodeItems(nodeId);
				
				new GetNodeItems(discoveryRegistry,
				xmppConnection, nodeId, requestParameters);

		final Thread thread = Thread.currentThread();

		getNodeItems.call(new ResultHandler<CloseableIterator<NodeItem>>() {

			@Override
			public void onSuccess(CloseableIterator<NodeItem> items) {
				result.set(items);
				thread.interrupt();
			}

			@Override
			public void onError(Throwable t) {
				error.set(t);
				thread.interrupt();
			}
		});

		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			if (result.get() != null) {
				return result.get();
			}

			if (error.get() instanceof NodeStoreException) {
				throw (NodeStoreException) error.get();
			} else {
				throw new NodeStoreException("Unexpected error caught",
						error.get());
			}
		}

		throw new NodeStoreException("Timed out");
	}

	@Override
	public int countNodeItems(String nodeId) throws NodeStoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NodeItem getNodeItem(String nodeId, String nodeItemId)
			throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addNodeItem(NodeItem item) throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNodeItem(NodeItem item) throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteNodeItemById(String nodeId, String nodeItemId)
			throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public Transaction beginTransaction() throws NodeStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createPersonalChannel(JID ownerJID) throws NodeStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLocalNode(String nodeId) throws NodeStoreException {
		return delegate.isLocalNode(nodeId);
	}

	@Override
	public boolean isLocalJID(JID jid) throws NodeStoreException {
		return delegate.isLocalJID(jid);
	}

	@Override
	public void setRequestParameters(Parameters requestParameters) {
		this.requestParameters = requestParameters;
	}
	
	/**
	 * Holds a reference to an object. This is used to pass objects from an inner class method to the outer class.
	 * @param <T> the type of the object to hold.
	 */
	private class ObjectHolder<T> {
		private T obj;
		
		public T get() {
			return obj;
		}
		
		public void set(final T obj) {
			this.obj = obj;
		}
	}
}
