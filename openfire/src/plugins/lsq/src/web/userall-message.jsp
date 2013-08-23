<%@ page import="org.jivesoftware.util.ParamUtils,
                 org.jivesoftware.openfire.SessionManager,
                 org.jivesoftware.openfire.session.ClientSession,
                 org.jivesoftware.openfire.user.UserManager,
                 org.jivesoftware.openfire.PresenceManager,
                 org.jivesoftware.openfire.user.User,
                 org.jivesoftware.openfire.XMPPServer,
                 org.xmpp.packet.JID,
                 org.xmpp.packet.Message,
                 java.net.URLEncoder,
                 java.util.Collection,
                 java.util.HashMap"
    errorPage="error.jsp"
%>
<%@ page import="java.util.Map" %>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%  // Get parameters
    String username = ParamUtils.getParameter(request,"username");
    boolean send = ParamUtils.getBooleanParameter(request,"send");
    boolean success = ParamUtils.getBooleanParameter(request,"success");
    boolean sendToAll = ParamUtils.getBooleanParameter(request,"sendToAll");
    String jid = ParamUtils.getParameter(request,"jid");
    String[] jids = ParamUtils.getParameters(request,"jid");
    String message = ParamUtils.getParameter(request,"message");
%>

<jsp:useBean id="webManager" class="org.jivesoftware.util.WebManager"  />
<% webManager.init(pageContext); %>

<%
    // Handle a cancel
    if (request.getParameter("cancel") != null) {
            response.sendRedirect("userall-message.jsp");
    }

    // Get the user - a user might not be passed in if this is a system-wide message
    User user = null;
    if (username != null) {
        user = webManager.getUserManager().getUser(username);
    }

    // Get the session manager
    SessionManager sessionManager = webManager.getSessionManager();
    PresenceManager presenceManager = webManager.getPresenceManager();
    UserManager userManager = webManager.getUserManager();
    String serverDomainName = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
    JID serverJID = new JID(serverDomainName);
    // Handle the request to send a message:
    Map<String,String> errors = new HashMap<String,String>();
    if (send) {
        // Validate the message and jid
        if (jid == null && !sendToAll && user != null) {
            errors.put("jid","jid");
        }
        if (message == null) {
            errors.put("message","message");
        }
        if (errors.size() == 0) {
            // no errors, so continue
            if (user == null) {
                // system-wide message:
                //sessionManager.sendServerMessage(null,message);
            	Collection<User> users = userManager.getUsers();
            	for(User toUser:users){
            		if(presenceManager.isAvailable(toUser)){
            		  JID toUserJid = presenceManager.getPresence(toUser).getFrom();
          		      sessionManager.sendServerMessage(toUserJid , null, message);     
            		}else{// offline message
            		  JID toUserJid =new JID(toUser+"@"+serverDomainName);    
          		      if(toUserJid!=null){
          		       Message msg = new Message();
          		       msg.setBody(message);
          		       msg.setTo(toUserJid);
          		       msg.setFrom(serverJID);
          		       XMPPServer.getInstance().getOfflineMessageStrategy().storeOffline(msg);
          		      }
            		}
            	}
            	success=true;
            }
            else {
                if (sendToAll) {
                    // loop through all sessions based on the user assoc with the JID, send
                    // message to all
                    for (String jid1 : jids) {
                        JID address = new JID(jid1);
                        // TODO: Do we really need this?
                        sessionManager.getSession(address);
                        sessionManager.sendServerMessage(address, null, message);
                        // Log the event
                        webManager.logEvent("send server message", "jid = all active\nmessage = "+message);
                    }
                }
                else {
                    sessionManager.sendServerMessage(new JID(jid),null,message);
                    // Log the event
                    webManager.logEvent("send server message", "jid = "+jid+"\nmessage = "+message);
                }
            }
            if (username != null){
                response.sendRedirect("userall-message.jsp?success=true&username=" +
                        URLEncoder.encode(username, "UTF-8"));
            }
            else {
                response.sendRedirect("userall-message.jsp?success=true");
            }
            return;
        }
    }

    // Get all sessions associated with this user:
    int numSessions = -1;
    ClientSession sess = null;
    Collection<ClientSession> sessions = null;
    if (user != null) {
        numSessions = sessionManager.getSessionCount(user.getUsername());
        sessions = sessionManager.getSessions(user.getUsername());
        if (numSessions == 1) {
            sess = sessions.iterator().next();
        }
    }
%>


<html>
<head>
<title><fmt:message key="userall.message.title"/></title>
<meta name="pageID" content="userall-message"/>
</head>
<body>

<%  if (success) { %>

    <div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr><td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0" alt=""></td>
        <td class="jive-icon-label">
        <fmt:message key="userall.message.send" />
        </td></tr>
    </tbody>
    </table>
    </div><br>

<%  } %>

<script language="JavaScript" type="text/javascript">
function updateSelect(el) {
    if (el.checked) {
        for (var e=0; e<el.form.jid.length; e++) {
            el.form.jid[e].selected = true;
        }
    }
    else {
        for (var e=0; e<el.form.jid.length; e++) {
            el.form.jid[e].selected = false;
        }
    }
    el.form.message.focus();
}
</script>

<form action="userall-message.jsp" method="post" name="f">
<% if(username != null){ %>
<input type="hidden" name="username" value="<%= username %>">
<% } %>
<input type="hidden" name="send" value="true">
<%  if (sess != null) { %>

    <input type="hidden" name="sessionID" value="<%= sess.getAddress().toString() %>">

<%  } %>

	<!-- BEGIN send message block -->
	<!--<div class="jive-contentBoxHeader">
		<fmt:message key="user.message.send_admin_msg" />
	</div>-->
	<div class="jive-contentBox" style="-moz-border-radius: 3px;">
		<table cellpadding="3" cellspacing="1" border="0" width="600">

		<tr><td colspan=3 class="text" style="padding-bottom: 10px;">
		<%   if (user == null) { %>

			<p><fmt:message key="userall.message.info" /></p>

		<%  } else { %>

			<p><fmt:message key="userall.message.specified_user_info" /></p>

		<%  } %>
		</td></tr>
		<tr>
			<td class="jive-label">
				<fmt:message key="userall.message.to" />:
			</td>
			<td>
				<%  if (user == null) { %>

					<fmt:message key="userall.message.all_online_user" />

				<%  } else { %>

					<%  if (sess != null && numSessions == 1) { %>

						<%= sess.getAddress().toString() %>
						<input type="hidden" name="jid" value="<%= sess.getAddress().toString() %>">

					<%  } else { %>

						<select size="2" name="jid" multiple>

						<%
                            for (ClientSession clisess : sessions) {
                        %>
                            <option value="<%= clisess.getAddress().toString() %>"><%= clisess.getAddress().toString() %>
                            </option>

                            <% } %>

						</select>

						<input type="checkbox" name="sendToAll" value="true" id="cb01"
						 onfocus="updateSelect(this);" onclick="updateSelect(this);">
						<label for="cb01"><fmt:message key="userall.message.send_session" /></label>

					<%  } %>

					<%  if (errors.get("jid") != null) { %>

						<br>
						<span class="jive-error-text">
						<fmt:message key="userall.message.valid_address" />
						</span>

					<%  } %>

				<%  } %>
			</td>
		</tr>
		<tr valign="top">
			<td class="jive-label">
				<fmt:message key="userall.message.message" />:
			</td>
			<td>
				<%  if (errors.get("message") != null) { %>

					<span class="jive-error-text">
					<fmt:message key="userall.message.valid_message" />
					</span>
					<br>

				<%  } %>
				<textarea name="message" cols="55" rows="5" wrap="virtual"></textarea>
			</td>
		</tr>
		</table>
	</div>
	<!-- END send message block -->

<input type="submit" value="<fmt:message key="userall.message.send_message" />">
<input type="submit" name="cancel" value="<fmt:message key="global.cancel" />">

</form>

<script language="JavaScript" type="text/javascript">
document.f.message.focus();
</script>


</body>
</html>