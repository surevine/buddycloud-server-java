package org.buddycloud.channelserver.channel.node.configuration.field;

import com.surevine.spiffing.Label;
import com.surevine.spiffing.SIOException;

/**
 * Created by dwd on 09/11/15.
 */
public class SIOLabel extends Field {
    public static final String FIELD_NAME = "buddycloud#sio_label";
    public static final String DEFAULT_VALUE = "Channel Security Label";

    public SIOLabel() {
        name = FIELD_NAME;
    }


    public String getValue() {
        return this.value;
    }

    public boolean isValid() {
        Label label = null;
        boolean valid = false;
        try {
            label = new Label(this.value);
            valid = label.valid();
        } catch (SIOException e) {
            valid = false;
        } finally {
            if (label != null) {
                label.dispose();
            }
        }
        return valid;
    }
}
