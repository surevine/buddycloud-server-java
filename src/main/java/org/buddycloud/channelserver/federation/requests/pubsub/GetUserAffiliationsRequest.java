package org.buddycloud.channelserver.federation.requests.pubsub;

import java.util.Collection;
import java.util.Collections;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.federation.AbstractProcessor;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.Buddycloud;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class GetUserAffiliationsRequest extends AbstractProcessor<Collection<NodeAffiliation>> {
	private final XMPPConnection connection;
	
	private final JID user;

	public GetUserAffiliationsRequest(final XMPPConnection connection, final ServiceDiscoveryRegistry discovery, final JID user) {
		super(discovery);
		this.user = user;
		this.connection = connection;
	}
	
	@Override
	protected JID getToJid() {
		return user;
	}

	@Override
	protected void sendRequest(final JID remoteServer, final ResultHandler<Collection<NodeAffiliation>> handler) {
		IQ iq = new IQ(IQ.Type.get);
		
		iq.setTo(remoteServer);
		
		Element pubsub = iq.setChildElement("pubsub", JabberPubsub.NAMESPACE_URI);
		
		Element affiliations = pubsub.addElement("affiliations");
	    affiliations.addAttribute("jid", user.toBareJID());
		Element actor = pubsub.addElement("actor");
		actor.addAttribute("jid", user.toBareJID());
		actor.addNamespace("", Buddycloud.NAMESPACE);				

		connection.sendIQ(iq, new XMPPConnection.IQHandler() {
			
			@Override
			public void onResult(IQ iq) {
				handler.onSuccess(fromIQ(iq));
			}
			
			@Override
			public void onError(IQ iq) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private Collection<NodeAffiliation> fromIQ(final IQ iq) {
		// Do stuff
		return Collections.emptyList();
	}
}