<%@ page import="java.util.*,org.jivesoftware.openfire.XMPPServer" %>
<html>
<head>
<link rel="stylesheet" type="text/css" href="/style/global.css">
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<title><fmt:message key="msger.sendToUser.title"/></title>
<meta name="pageID" content="msger-send"/>
</head>
<jsp:useBean id="webManager" class="org.jivesoftware.util.WebManager" />
<body>
<%@ page import="org.jivesoftware.openfire.PresenceManager,
                 org.jivesoftware.openfire.group.Group,
                 org.jivesoftware.openfire.group.GroupManager,
                 org.jivesoftware.openfire.user.User,
                 org.jivesoftware.openfire.user.UserManager,
                 org.jivesoftware.openfire.user.UserNotFoundException,
                 org.jivesoftware.util.ParamUtils,
                 org.jivesoftware.openfire.SessionManager,
                 org.xmpp.packet.JID,
                 org.xmpp.packet.Message,
                 java.util.Collection"
    errorPage="error.jsp"
%>
<%    
    boolean send = ParamUtils.getBooleanParameter(request,"send"); 
    String toUser = ParamUtils.getParameter(request,"toUser");
    String fromUser = ParamUtils.getParameter(request,"fromUser");
    String message = ParamUtils.getParameter(request,"message");
    
String success=null;

    if (send) {
   //send to user
   SessionManager sessionManager = webManager.getSessionManager();
   PresenceManager presenceManager = webManager.getPresenceManager();
   UserManager userManager = webManager.getUserManager();
   String serverDomainName = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
   GroupManager groupManager = webManager.getGroupManager();
   User user=null;
   JID toUserJid= null;
   JID fromUserJid= null;
  
   if(fromUser!=null)
    fromUserJid=new JID(fromUser+"@"+serverDomainName); 
   else
    fromUserJid=new JID("");   
  
   if(toUser!=null&&message!=null&&userManager.isRegisteredUser(toUser)==true){
        
         try {
           	 user = userManager.getUser(toUser);
	         if (presenceManager.isAvailable(user)) {
		         // online messages
		      toUserJid = presenceManager.getPresence(user).getFrom();
		      sessionManager.sendServerMessage(toUserJid , null, message);     
		      success="true";
		     }else{
		     // offline message
		      toUserJid =new JID(toUser+"@"+serverDomainName);    
		      if(toUserJid!=null){
		       Message msg = new Message();
		       msg.setBody(message);
		       msg.setTo(toUserJid);
		       msg.setFrom(fromUserJid);
		       XMPPServer.getInstance().getOfflineMessageStrategy().storeOffline(msg);
		      }
		      success="true";
		     }
         }
         catch (UserNotFoundException e) {
	         success="fail";
	         e.printStackTrace();
         }
    }else{
        success="fail";
    }
}
       

%>
<% if (success=="true") { %>
    <div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr><td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0" alt=""></td>
        <td class="jive-icon-label">
        <fmt:message key="msger.sendReault.success" />
        </td></tr>
    </tbody>
    </table>
    </div><br>
<% }if(success=="fail"){ %>
<div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr><td class="jive-icon"><img src="images/error-16x16.gif" width="16" height="16" border="0" alt=""></td>
        <td class="jive-icon-label">
        <fmt:message key="msger.sendReault.fail" />! <fmt:message key="msger.sendReault.fail.nouser" /> , <fmt:message key="msger.sendReault.fail.noContent" />
        </td></tr>
    </tbody>
    </table>
    </div><br>
<%}%>
<table cellpadding="3"> 
<form action="msger-send.jsp" id="sendMsg" method="post">
<tr>
   <td><fmt:message key="msger.sendToUser"/></td>
      <td><input type="text" name="toUser" id="toUser"></td> 
</tr>

   <tr>
    <td> <fmt:message key="msger.sendContent"/></td>
    <td><textarea rows="10" name="message" cols="60"></textarea></td> 
   <tr>
   <td></td>
       <td>
    <input type="submit" value="<fmt:message key="msger.send.submit"/>"><input type="hidden" name="send" value="true">
       </td>
</tr>
</form>
</table>
  
</body>
</html>