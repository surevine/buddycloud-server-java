package org.buddycloud.channelserver.federation;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.channel.ChannelManagerImpl;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public abstract class ChannelServerRequestAbstract<T> implements AsyncCall<T> {
	protected final ServiceDiscoveryRegistry discovery;
	protected final XMPPConnection connection;
	protected final ChannelManager channelManager;

	public ChannelServerRequestAbstract(
			final ServiceDiscoveryRegistry discovery,
			final XMPPConnection connection, ChannelManager channelManager) {
		this.discovery = discovery;
		this.connection = connection;
		this.channelManager = channelManager;
	}

	@Override
	public void call(final ResultHandler<T> handler) {
		discovery.discoverChannelServerJID(getToJid(),
				new ServiceDiscoveryRegistry.JIDDiscoveryHandler() {

					@Override
					public void onSuccess(JID jid) {
						sendRequest(jid, handler);
					}

					@Override
					public void onError(Throwable t) {
						handler.onError(t);
					}
				});
	}

	protected void sendIq(IQ iq, final ResultHandler<T> handler) {

		connection.sendIQ(iq, new XMPPConnection.IQHandler() {

			@Override
			public void onResult(IQ iq) {
				handler.onSuccess(fromIq(iq));
			}

			@Override
			public void onError(IQ iq) {
				handler.onError(new NodeStoreException(iq.getError().toString()));
			}
		});
	}

	protected abstract T fromIq(IQ iq);

	protected abstract JID getToJid();

	protected abstract void sendRequest(JID channelServer,
			ResultHandler<T> handler);
}
