package org.buddycloud.channelserver.federation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.AsyncCall.ResultHandler;
import org.buddycloud.channelserver.federation.requests.disco.DiscoInfo;
import org.buddycloud.channelserver.federation.requests.disco.DiscoInfo.Identity;
import org.buddycloud.channelserver.federation.requests.disco.DiscoItems;
import org.xmpp.packet.JID;

public class ServiceDiscoveryRegistry {
	public interface JIDDiscoveryHandler {
		void onSuccess(JID jid);
		void onError(Throwable t);
	}
	
	private static Logger LOGGER = Logger.getLogger(ServiceDiscoveryRegistry.class);
	
	private final XMPPConnection connection;
	private final Configuration configuration;
	
	private final Map<String,JID> channelServers;
	
	public ServiceDiscoveryRegistry(final XMPPConnection connection, final Configuration configuration) {
		this.connection = connection;
		this.configuration = configuration;
		channelServers = new HashMap<String,JID>();
	}
	
	public void discoverChannelServerJIDFromNodeId(final String nodeID, final JIDDiscoveryHandler handler) {
	}
	
	/**
	 * Discovers the relevant channel server JID for a remote JID
	 * @param handler the response handler which will be called 
	 * @param remoteJID
	 */
	public void discoverChannelServerJID(final JID remoteJID, final JIDDiscoveryHandler handler) {
		// TODO Ensure that we don't send the same request twice at the same time
		
		// We first discover the items on the JID's domain
		final String remoteDomain = remoteJID.getDomain();
		
		final JID cachedJID = channelServers.get(remoteDomain);
		
		if(cachedJID != null) {
			handler.onSuccess(cachedJID);
			return;
		}
		
		DiscoItems itemsRequest = new DiscoItems(connection, configuration, new JID(remoteDomain));
		
		itemsRequest.call(new AsyncCall.ResultHandler<Collection<JID>>() {
			@Override
			public void onSuccess(final Collection<JID> result) {
				// Then for each item we do an info query until we find the appropriate identity
				for(final JID jid : result) {
					// We will recheck the map each iteration in case another thread has added it a result in the meantime.
					// This is less expensive than potentially sending more disco#info requests than we strictly need to.
					final JID cachedJID = channelServers.get(remoteDomain);
					
					if(cachedJID != null) {
						handler.onSuccess(cachedJID);
						return;
					}

					DiscoInfo infoRequest = new DiscoInfo(connection, configuration, jid);
					
					infoRequest.call(new ResultHandler<Collection<Identity>>() {
						@Override
						public void onSuccess(final Collection<Identity> result) {
							for(Identity identity : result) {
								if(identity.getCategory().equals("pubsub") && identity.getCategory().equals("channels")) {
									channelServers.put(remoteDomain, jid);
									handler.onSuccess(jid);
									return;
								}
							}
						}

						@Override
						public void onError(Throwable t) {
							LOGGER.info("Error returned from disco#items for " + jid, t);
						}
					});
					
					// TODO
					handler.onError(new NodeStoreException());
				}
			}

			@Override
			public void onError(Throwable t) {
				handler.onError(t);
			}
		});
	}
}
