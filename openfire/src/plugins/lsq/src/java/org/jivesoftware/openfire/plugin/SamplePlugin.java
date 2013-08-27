package org.jivesoftware.openfire.plugin;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.lsq.openfire.chat.logs.DbChatLogsManager;
import com.lsq.openfire.chat.logs.entity.ChatLogs;
 
public class SamplePlugin implements Plugin,PacketInterceptor {
	private static final Logger log = LoggerFactory.getLogger(SamplePlugin.class);
    private XMPPServer server;
    private static PluginManager pluginManager;
    private static DbChatLogsManager logsManager;
    private InterceptorManager interceptorManager;
    
    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        server = XMPPServer.getInstance();
        pluginManager = manager;
        interceptorManager = InterceptorManager.getInstance();
        logsManager = DbChatLogsManager.getInstance();
        interceptorManager.addInterceptor(this);
        System.out.println("test");
//        org.jivesoftware.util.LocaleUtils.getLocalizedString("projectName", "[myPlugin]");
        debug("init plugin start up");
        System.out.println(server.getServerInfo());
    }
 
    @Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
//        if (session != null) {
//            debug(packet, incoming, processed, session);
//        }
//        JID recipient = packet.getTo();
//        if (recipient != null) {
//            String username = recipient.getNode();
//            //un register
//            if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {
//                return;
//            } else if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
//                // not this xmppserver
//                return;
//            } else if ("".equals(recipient.getResource())) {
//            }
//        }
//        this.doAction(packet, incoming, processed, session);
		
	}

    private void doAction(Packet packet, boolean incoming, boolean processed, Session session) {

        Packet copyPacket = packet.createCopy();
        if (packet instanceof Message) {
            Message message = (Message) copyPacket;
            //one to one single talk
            if (message.getType() == Message.Type.chat) {
                log.info("single talk:{}", message.toXML());
                debug("single talk:" + message.toXML());
                // processed is false and incoming is true.  processed true means send is over, incoming true means self send the message
                if (processed || !incoming) {
                    return;
                }
                logsManager.add(this.get(packet, incoming, session));
            // group talk,multiPeople
            } else if (message.getType() ==  Message.Type.groupchat) {
                List<?> els = message.getElement().elements("x");
                if (els != null && !els.isEmpty()) {
                    log.info("group info:{}", message.toXML());
                    debug("group info:" + message.toXML());
                } else {
                    log.info("group info:{}", message.toXML());
                    debug("group info:" + message.toXML());
                }
            } else {
                log.info("other info:{}", message.toXML());
                debug("other info:" + message.toXML());
            }
        } else if (packet instanceof IQ) {
            IQ iq = (IQ) copyPacket;
            if (iq.getType() == IQ.Type.set && iq.getChildElement() != null && "session".equals(iq.getChildElement().getName())) {
                log.info("user login success:{}", iq.toXML());
                debug("user login success: " + iq.toXML());
            }
        } else if (packet instanceof Presence) {
            Presence presence = (Presence) copyPacket;
            if (presence.getType() == Presence.Type.unavailable) {
                log.info("user loginout success:{}", presence.toXML());
                debug("user loginout success: " + presence.toXML());
            }
        } 

    }
    private void debug(Packet packet, boolean incoming, boolean processed, Session session) {
        String info = "[ packetID: " + packet.getID() + ", to: " + packet.getTo() + ", from: " + packet.getFrom() + ", incoming: " + incoming + ", processed: " + processed + " ]";
        long timed = System.currentTimeMillis();
        debug("################### start ###################" + timed);
        debug("id:" + session.getStreamID() + ", address: " + session.getAddress());
        debug("info: " + info);
        debug("xml: " + packet.toXML());
        debug("################### end #####################" + timed);
        log.info("id:" + session.getStreamID() + ", address: " + session.getAddress());
        log.info("info: {}", info);
        log.info("plugin Name: " + pluginManager.getName(this) + ", xml: " + packet.toXML());
    }

    private ChatLogs get(Packet packet, boolean incoming, Session session) {
        Message message = (Message) packet;
        ChatLogs logs = new ChatLogs();
        JID jid = session.getAddress();
        if (incoming) {        //sender
            logs.setSender(jid.getNode());
            JID recipient = message.getTo();
            logs.setReceiver(recipient.getNode());
        } 
        logs.setContent(message.getBody());
        logs.setCreateDate(new Timestamp(new Date().getTime()));
        logs.setDetail(message.toXML());
        logs.setLength(logs.getContent().length());
        logs.setState(0);
        logs.setSessionJID(jid.toString());
        return logs;
    }
    
    private void debug(Object message) {
        if (true) {
            System.out.println(message);
        }
    }
    
	@Override
    public void destroyPlugin() {
		interceptorManager.removeInterceptor(this);
		debug("destroy plugin success");
    }
}