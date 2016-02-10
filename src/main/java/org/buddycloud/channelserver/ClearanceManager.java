package org.buddycloud.channelserver;

import com.surevine.spiffing.*;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

/**
 * Created by dwd on 17/12/15.
 */
public class ClearanceManager {
    private static final Logger LOGGER = Logger.getLogger(ClearanceManager.class);

    public static String BASELINE_CLEARANCE = "spiffing.clearance.baseline";
    public static String USER_CLEARANCE = "spiffing.clearance.user.";
    public static String DOMAIN_CLEARANCE = "spiffing.clearance.domain.";
    public static String USER_POLICY = "spiffing.policy_id.user.";
    public static String DOMAIN_POLICY = "spiffing.policy_id.domain.";

    private Configuration configuration;
    private Site site;

    public ClearanceManager(Configuration conf) throws SIOException {
        this.configuration = conf;
        try {
            this.site = new Site();
        } catch (NoClassDefFoundError e) {
            this.site = null;
            return;
        }

        String allspifs = configuration.getProperty(Configuration.SECURITY_POLICY);
        if (allspifs != null) {
            String[] spifs = allspifs.split(";");
            for (String spif : spifs) {
                try {
                    Spif policy = site.load(spif);
                    LOGGER.info("Loaded site security policy: " + policy.name());
                } catch (SIOException e) {
                    LOGGER.warn("Failed to load policy from " + spif, e);
                }
            }
        }
    }

    private Clearance getClearance(JID target) {
        if (this.site == null) {
            return null;
        }
        if (configuration.isLocalJID(target)) {
            String b64 = configuration.getProperty(USER_CLEARANCE + target.getNode());
            if (b64 != null) {
                try {
                    return new Clearance(b64);
                } catch (SIOException e) {
                    LOGGER.warn("Failed to load user clearance");
                }
            }
        } else {
            String b64 = configuration.getProperty(DOMAIN_CLEARANCE + target.getDomain());
            if (b64 != null) {
                try {
                    return new Clearance(b64);
                } catch (SIOException e) {
                    LOGGER.warn("Failed to load domain clearance");
                }
            }
        }
        String b64 = configuration.getProperty(BASELINE_CLEARANCE);
        if (b64 != null) {
            try {
                return new Clearance(b64);
            } catch (SIOException e) {
                LOGGER.warn("Failed to load baseline policy");
            }
        }
        return null;
    }

    private Spif getPolicy(JID target) throws SIOException {
        String pol_id;
        if (configuration.isLocalJID(target)) {
            pol_id = configuration.getProperty(USER_POLICY + target.getNode());
        } else {
            pol_id = configuration.getProperty(DOMAIN_POLICY + target.getDomain());
        }
        if (pol_id == null) {
            return null;
        }
        return this.site.spif(pol_id);
    }

    private static void fillLabel(Element seclabel, Label label) throws SIOException {
        Element marking = seclabel.addElement("displaymarking");
        marking.setText(label.displayMarking());
        String fg = label.fgColour();
        if (fg != null) {
            marking.addAttribute("fgcolor", fg);
        }
        String bg = label.bgColour();
        if (bg != null) {
            marking.addAttribute("bgcolor", bg);
        }
        Element labelwrap = seclabel.addElement("label");
        Element ess = labelwrap.addElement("esssecuritylabel", "urn:xmpp:sec-label:ess:0");
        ess.setText(label.toESSBase64());
    }

    public Label doACDF(Label label, JID whom) throws SIOException {
        if (this.site == null) {
            return null;
        }
        if (label == null) {
            return null;
        }
        Clearance clr = getClearance(whom);
        Label ret = null;
        LOGGER.debug("Label is " + label.displayMarking());
        try {
            ret = label;
            if (clr != null) {
                LOGGER.debug("Got Clearance " + clr.displayMarking() + " for " + whom.toBareJID());
                String clr_id = clr.policy().policy_id();
                String label_id = label.policy().policy_id();
                LOGGER.debug("Label policy " + label_id + ", Clearance policy " + clr_id);
                if (!clr_id.equals(label_id)) {
                    Spif clr_policy = this.site.spif(clr_id);
                    try {
                        ret = label.encrypt(clr_policy);
                        LOGGER.debug("Translated label to " + ret.displayMarking());
                    } finally {
                        label.dispose();
                    }
                }
                if (!clr.dominates(ret)) {
                    throw new SIOException("Clearance does not dominate.");
                }
            }
            Spif policy = getPolicy(whom);
            if (policy != null) {
                if (!ret.policy().policy_id().equals(policy.policy_id())) {
                    Label ret2 = null;
                    try {
                        ret2 = ret.encrypt(policy);
                    } finally {
                        ret.dispose();
                    }
                    return ret2;
                }
            }
            return ret;
        } catch (RuntimeException e) {
            if (ret != null) {
                ret.dispose();
            }
            throw e;
        } finally {
            if (clr != null) {
                clr.dispose();
            }
        }
    }

    public void addLabel(Element itemElement, String itemLabel, JID whom) throws SIOException {
        if (this.site == null) {
            return;
        }
        Label label_in = null;
        Label label = null;
        if (itemLabel == null) {
            return;
        }
        try {
            label_in = new Label(itemLabel);
            label = doACDF(label_in, whom);
            if (label != label_in) {
                label_in.dispose();
                label_in = null;
            }
            if (label != null) {
                Element seclabel = itemElement.addElement("securitylabel", "urn:xmpp:sec-label:0");
                fillLabel(seclabel, label);
            }
        } finally {
            if (label_in != null) {
                label_in.dispose();
            }
            if (label != null) {
                label.dispose();
            }
        }
    }

    public void stampMessage(Message msg, Label label_in, JID whom) throws SIOException {
        if (this.site == null) {
            return;
        }
        Label label = null;
        if (label_in == null) {
            return;
        }
        boolean freeme = true; // Honestly!! Like old-school C, this is.
        try {
            label = doACDF(label_in, whom);
            if (label == label_in) {
                freeme = false; // Kill me. Kill me now. Java, why no RAII?
            }
            if (label != null) {
                Element seclabel = msg.addChildElement("securitylabel", "urn:xmpp:sec-label:0");
                fillLabel(seclabel, label);
            }
        } finally {
            if (freeme && label != null) {
                label.dispose();
            }
        }
    }
}
