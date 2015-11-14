package org.buddycloud.channelserver.channel.validate;

import com.surevine.spiffing.Label;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.Capabilities;

import org.buddycloud.channelserver.channel.ChannelManager;
import org.buddycloud.channelserver.db.exception.NodeStoreException;
import org.dom4j.Element;
import org.xmpp.packet.JID;

public interface PayloadValidator extends Plugin {

    public abstract void setPayload(Element payload);

    public abstract String getErrorMessage();

    public abstract void setChannelManager(ChannelManager channelManager);

    /**
     * @throws InterruptedException
     * @throws NodeStoreException
     */
    public abstract boolean isValid() throws NodeStoreException;

    public abstract Element getPayload();

    public abstract Label getLabel();

    public abstract void setUser(JID jid);

    public abstract void setNode(String node);

    public abstract void setTo(String channelServerDomain);

    public abstract String getLocalItemId();

    public abstract String getGlobalItemId();

    public abstract String getInReplyTo();

    /**
     * Indicate the capabilities of this plugin
     *
     * Should return an array of content types that can be validated by this plugin
     *
     * @return
     */
    @Capabilities
    public abstract String[] capabilities();
}
