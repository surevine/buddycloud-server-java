package org.buddycloud.channelserver.federation.requests.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.ClosableIteratorImpl;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.ChannelServerRequestAbstract;
import org.buddycloud.channelserver.federation.ServiceDiscoveryRegistry;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.Buddycloud;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.pubsub.JabberPubsub;
import org.buddycloud.channelserver.pubsub.affiliation.Affiliations;
import org.buddycloud.channelserver.pubsub.model.NodeAffiliation;
import org.buddycloud.channelserver.pubsub.model.NodeItem;
import org.buddycloud.channelserver.pubsub.model.impl.NodeAffiliationImpl;
import org.buddycloud.channelserver.pubsub.model.impl.NodeItemImpl;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class GetUserAffiliations extends
		ChannelServerRequestAbstract<Collection<NodeAffiliation>> {

	private final JID user;

	public GetUserAffiliations(final XMPPConnection connection,
			final ServiceDiscoveryRegistry discovery, final JID user,
			ChannelManager channelManager) {
		super(discovery, connection, channelManager);
		this.user = user;
	}

	@Override
	protected JID getToJid() {
		return user;
	}

	@Override
	protected void sendRequest(final JID remoteServer,
			final ResultHandler<Collection<NodeAffiliation>> handler) {
		IQ iq = new IQ(IQ.Type.get);

		iq.setTo(remoteServer);

		Element pubsub = iq.setChildElement("pubsub",
				JabberPubsub.NAMESPACE_URI);

		Element affiliations = pubsub.addElement("affiliations");
		affiliations.addAttribute("jid", user.toBareJID());
		Element actor = pubsub.addElement("actor");
		actor.addAttribute("jid", user.toBareJID());
		actor.addNamespace("", Buddycloud.NAMESPACE);

		sendIq(iq, handler);
	}

	@Override
	protected Collection<NodeAffiliation> fromIq(IQ iq) {
		ArrayList<NodeAffiliation> result = new ArrayList<NodeAffiliation>();
		NodeAffiliation nodeAffiliation;
		String nodeId = iq.getElement().element("affiliations")
				.attributeValue("node");
		JID jid;
		try {
			List<Element> affiliationsList = iq.getElement().elements(
					"affiliation");
			for (Element affiliation : affiliationsList) {
				jid = new JID(affiliation.attributeValue("jid"));
				nodeAffiliation = new NodeAffiliationImpl(nodeId, jid,
						Affiliations.valueOf(affiliation
								.attributeValue("affiliation")));
				result.add(nodeAffiliation);
			}
		} catch (NullPointerException e) {
			// Means no items
		}
		return result;
	}
}