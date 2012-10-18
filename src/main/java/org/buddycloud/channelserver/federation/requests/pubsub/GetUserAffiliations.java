package org.buddycloud.channelserver.federation.requests.pubsub;

import java.util.Collection;
import java.util.Collections;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.ChannelServerRequestAbstract;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.Buddycloud;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class GetUserAffiliations extends ChannelServerRequestAbstract<Collection<NodeAffiliation>> {

	private final JID user;

	public GetUserAffiliations(final XMPPConnection connection, final ServiceDiscoveryRegistry discovery, final JID user, Parameters requestParameters) {
		super(discovery, connection, requestParameters);
		this.user = user;
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

		sendIq(iq, handler);
	}

	@Override
	protected Collection<NodeAffiliation> fromIq(IQ iq) {
		// TODO Auto-generated method stub
		return null;
	}
}