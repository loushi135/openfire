<%@page import="com.lsq.openfire.chat.logs.entity.ChatLogs"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="org.jivesoftware.util.ParamUtils"%>
<%@page import="org.jivesoftware.openfire.XMPPServer"%>
<%@page import="com.lsq.openfire.chat.logs.DbChatLogsManager"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<title><fmt:message key="chatLogs.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="pageID" content="chatLogs-service" />
</head>
<%
	String sender = ParamUtils.getParameter(request, "sender");
	String receiver = ParamUtils.getParameter(request, "receiver");
	String content = ParamUtils.getParameter(request, "content");
	String createDate = ParamUtils.getParameter(request, "createDate");
	ChatLogs entity = new ChatLogs();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	try {
		entity.setCreateDate(new Timestamp(df.parse(createDate)
				.getTime()));
	} catch (Exception e) {
	}
	request.setAttribute("sender",sender);
	request.setAttribute("receiver",receiver);
	request.setAttribute("content",content);
	request.setAttribute("createDate",createDate);
	entity.setContent(content);
	entity.setReceiver(receiver);
	entity.setSender(sender);
	DbChatLogsManager logsManager = DbChatLogsManager.getInstance();
	List<ChatLogs> logs = logsManager.query(entity);
%>
<body>
	<div class="jive-contentBoxHeader"><fmt:message key="chatLogs.allContactors" /></div>
	<div class="jive-contentBox">
		<a href="chatlogs?action=all!contact">detail</a>
	</div>
	
	<div class="jive-contentBoxHeader">search</div>
	<div class="jive-contentBox">
		<form action="chatLogs-service.jsp">
			<fmt:message key="chatLogs.sender" />: <input type="text" name="sender" value="<%= sender==null?"":sender %>">
			<fmt:message key="chatLogs.receiver" />: <input type="text" name="receiver" value="<%= receiver==null?"":receiver %>">
			<fmt:message key="chatLogs.content" />: <input type="text" name="content" value="<%= content==null?"":content %>">
			<fmt:message key="chatLogs.sendDate" />: <input type="text" name="createDate"
				value="<%= createDate==null?"":createDate %>"> <input type="submit">
			<input type="reset">
		</form>
	</div>

	<div class="jive-table">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<thead>
				<tr>
					<th><fmt:message key="chatLogs.sender" /></th>
					<th><fmt:message key="chatLogs.receiver" /></th>
					<th><fmt:message key="chatLogs.content" /></th>
					<th><fmt:message key="chatLogs.sendDate" /></th>
					<th><fmt:message key="chatLogs.delete" /></th>
				</tr>
			</thead>
			<tbody>
				<%
					for (int i = 0, len = logs.size(); i < len; i++) {

						ChatLogs log = logs.get(i);
				%>

				<tr class="jive-<%=i % 2 == 0 ? "even" : "odd"%>">

					<td><%=log.getSender()%></td>

					<td><%=log.getReceiver()%></td>

					<td><%=log.getContent()%></td>

					<td><%=log.getCreateDate()%></td>

					<td>
						<a href="chatlogs?action=remove!contact&messageId=<%=log.getMessageId() %>"
							 title="<fmt:message key='chatLogs.onclickDelete' />"
							 ><img src="images/delete-16x16.gif" width="16" height="16" border="0" >
						</a>
					</td>
				</tr>
				<%
					}
				%>
			</tbody>
		</table>
	</div>

</body>

</html>

