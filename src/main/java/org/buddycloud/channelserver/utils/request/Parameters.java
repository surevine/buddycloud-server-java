package org.buddycloud.channelserver.utils.request;

import org.xmpp.packet.JID;

public class Parameters {

	private JID requester;
	private String channelsDomain;
	private String topicsDomain;
	
	public JID getRequester() {
		return requester;
	}
	public void setRequester(JID requester) {
		this.requester = requester;
	}
	public String getChannelsDomain() {
		return channelsDomain;
	}
	public void setChannelsDomain(String serverDomain) {
		this.channelsDomain = serverDomain;
	}
	public String getTopicsDomain() {
		return topicsDomain;
	}
	public void setTopicsDomain(String topicsDomain) {
		this.topicsDomain = topicsDomain;
	}
}