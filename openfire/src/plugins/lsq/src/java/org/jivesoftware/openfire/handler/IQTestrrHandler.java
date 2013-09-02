package org.jivesoftware.openfire.handler;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.SharedGroupException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.stringprep.IDNAException;
import org.jivesoftware.util.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

public class IQTestrrHandler extends IQHandler{

	private static final Logger Log = LoggerFactory
			.getLogger(IQTestrrHandler.class);
	private IQHandlerInfo info;
	private Element testReponse;

	public IQTestrrHandler() {
		super("XMPP User Crowd List Handler");
		info = new IQHandlerInfo("query", "jabber:iq:testrr");
		testReponse = DocumentHelper.createElement(QName.get("query",
				"jabber:iq:testrr"));
		testReponse.addElement("username");
		testReponse.addElement("fullname");
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		System.out.println("IQLoadUserCrowdListHandler - handleIQ - begin");
		try {
			IQ returnPacket = null;
			System.out.println("IQLoadUserCrowdListHandler - handleIQ - 1");
			System.out
					.println("IQLoadUserCrowdListHandler - handleIQ - packet to xml = "
							+ packet.toXML());
			JID recipientJID = packet.getTo();
			if (recipientJID == null
					|| recipientJID.getNode() == null
					|| !UserManager.getInstance().isRegisteredUser(
							recipientJID.getNode())) {
				returnPacket = manageRoster(packet);
			}
			return returnPacket;
		} catch (SharedGroupException e) {
			IQ result = IQ.createResultIQ(packet);
			result.setChildElement(packet.getChildElement().createCopy());
			result.setError(PacketError.Condition.not_acceptable);
			return result;
		} catch (Exception e) {
			if (e.getCause() instanceof IDNAException) {
				Log.warn(LocaleUtils.getLocalizedString("admin.error"), e);
				IQ result = IQ.createResultIQ(packet);
				result.setChildElement(packet.getChildElement().createCopy());
				result.setError(PacketError.Condition.jid_malformed);
				return result;
			} else {
				Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
				IQ result = IQ.createResultIQ(packet);
				result.setChildElement(packet.getChildElement().createCopy());
				result.setError(PacketError.Condition.internal_server_error);
				return result;
			}
		}
	}

	private IQ manageRoster(IQ packet) throws UnauthorizedException,
			UserAlreadyExistsException, SharedGroupException {
		IQ returnPacket = null;
		JID sender = packet.getFrom();
		System.out.println("IQLoadUserCrowdListHandler - manageRoster - begin");
		IQ.Type type = packet.getType();
		try {
			System.out.println("IQLoadUserCrowdListHandler - manageRoster - 1");
//			Element iq = packet.getElement();
//			Element query = iq.element("query");
			Element queryResponse = testReponse.createCopy();
			if (IQ.Type.get == type) {
				User user = XMPPServer.getInstance().getUserManager().getUser("wzhu");
				returnPacket = IQ.createResultIQ(packet);
				queryResponse.element("username").setText(user.getUsername());
				queryResponse.element("fullname").setText(user.getName());
				returnPacket.setChildElement(queryResponse);
				returnPacket.setType(IQ.Type.result);
				returnPacket.setTo(sender);
				returnPacket.setID(packet.getID());
				System.out
						.println("IQLoadUserCrowdListHandler - manageRoster - returnPacket xml = "
								+ returnPacket.toXML());
				deliverer.deliver(returnPacket);
			}
		} catch (Exception e) {
			throw new UnauthorizedException(e);
		}
		return returnPacket;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}
