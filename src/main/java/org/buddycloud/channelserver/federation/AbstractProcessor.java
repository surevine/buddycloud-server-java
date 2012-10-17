package org.buddycloud.channelserver.federation;

import org.xmpp.packet.JID;

public abstract class AbstractProcessor<T> implements AsyncCall<T> {
	private final ServiceDiscoveryRegistry discovery;

	public AbstractProcessor(final ServiceDiscoveryRegistry discovery) {
		this.discovery = discovery;
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

	protected abstract JID getToJid();
	
	protected abstract void sendRequest(JID channelServer, ResultHandler<T> handler);
}
