package org.buddycloud.channelserver.channel.connection;

import org.buddycloud.channelserver.connection.XMPPConnection;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;

public class MockConnection implements XMPPConnection {

	private Packet packet;
	private IQ iq;

	public void send(Packet p) {
		packet = p;
	}

	public void sendIQ(IQ iq, IQHandler handler) {
		this.iq = iq;
		handler.onResult(iq);
	}

	public Packet getLastPacket() {
		return packet;
	}
	
	public IQ getLastIQ() {
		return iq;
	}
}