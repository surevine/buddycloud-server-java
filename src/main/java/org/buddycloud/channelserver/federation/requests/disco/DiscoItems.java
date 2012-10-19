package org.buddycloud.channelserver.federation.requests.disco;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.AsyncCall;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.discoitems.JabberDiscoItems;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class DiscoItems implements AsyncCall<Collection<JID>> {
	private final XMPPConnection connection;
	private final Configuration configuration;
	
	private JID jid;
	
	public DiscoItems(final XMPPConnection connection, final Configuration configuration, final JID jid) {
		this.jid = jid;
		this.connection = connection;
		this.configuration = configuration;
	}

	public Collection<JID> fromIQ(IQ response) {
		ArrayList<JID> items = new ArrayList<JID>();
		
		Element queryEl = response.getChildElement();
		
		@SuppressWarnings("unchecked")
		List<Element> itemEls = queryEl.elements(QName.get("item", JabberDiscoItems.NAMESPACE));
		
		for(Element itemEl : itemEls) {
			items.add(new JID(itemEl.attributeValue("jid")));
		}
		
		return items;
	}

	@Override
	public void call(final ResultHandler<Collection<JID>> handler) {
		IQ iq = new IQ();
		
		iq.setType(IQ.Type.get);
		iq.setTo(jid);
		iq.setFrom(configuration.getProperty(Configuration.CONFIGURATION_SERVER_CHANNELS_DOMAIN));
		iq.setChildElement("query",
				JabberDiscoItems.NAMESPACE_URI);
		
		connection.sendIQ(iq, new XMPPConnection.IQHandler() {
			
			@Override
			public void onResult(IQ iq) {
				handler.onSuccess(fromIQ(iq));
			}
			
			@Override
			public void onError(IQ iq) {
				// TODO
				handler.onError(new NodeStoreException());
			}
		});
	}
}