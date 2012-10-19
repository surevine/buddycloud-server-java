package org.buddycloud.channelserver.federation.requests.disco;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.buddycloud.channelserver.Configuration;
import org.buddycloud.channelserver.connection.XMPPConnection;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.buddycloud.channelserver.federation.AsyncCall;
import org.buddycloud.channelserver.packetprocessor.iq.namespace.discoinfo.JabberDiscoInfo;
import org.buddycloud.channelserver.utils.request.Parameters;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

public class DiscoInfo implements AsyncCall<Collection<DiscoInfo.Identity>> {
	private final XMPPConnection connection;
	private final Configuration configuration;

	public class Identity {
		private String category;
		private String type;

		public Identity(String category, String type) {
			this.category = category;
			this.type = type;
		}

		public String getCategory() {
			return category;
		}

		public String getType() {
			return type;
		}
	}
	
	private JID jid;
	
	public DiscoInfo(final XMPPConnection connection, final Configuration configuration, final JID jid) {
		this.jid = jid;
		this.connection = connection;
		this.configuration = configuration;
	}

	public Collection<Identity> fromIQ(IQ response) {
		Element queryEl = response.getChildElement();
		
		@SuppressWarnings("unchecked")
		List<Element> itemEls = queryEl.elements(QName.get("identity", JabberDiscoInfo.NAMESPACE));
		
		ArrayList<Identity> items = new ArrayList<Identity>(itemEls.size());
		
		for(Element itemEl : itemEls) {
			items.add(new Identity(itemEl.attributeValue("category"), itemEl.attributeValue("type")));
		}
		
		return items;
	}

	@Override
	public void call(final ResultHandler<Collection<Identity>> handler) {
		IQ iq = new IQ();
		
		iq.setType(IQ.Type.get);
		iq.setTo(jid);
		iq.setFrom(configuration.getProperty(Configuration.CONFIGURATION_SERVER_CHANNELS_DOMAIN));
		iq.setChildElement("query",
				JabberDiscoInfo.NAMESPACE_URI);
		
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
