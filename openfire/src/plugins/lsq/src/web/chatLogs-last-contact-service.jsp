<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title><fmt:message key="chatLogs.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="subPageID" content="chatLogs-all-contact-service" />
</head>

<body>
	<div class="jive-contentBoxHeader"><fmt:message key="chatLogs.allContactors" /></div>
	<div class="jive-table">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
			<thead>
				<tr>
					<th><fmt:message key="chatLogs.contactJID" /></th>
					<th><fmt:message key="chatLogs.contactName" /></th>
					<th><fmt:message key="chatLogs.contactcontent" /></th>
				</tr>
			</thead>
			<tbody>
				<%
					Object obj = request.getAttribute("lastContact");
					if (obj != null) {
						List<String> allContact = (List<String>) obj;
						for (int i = 0, len = allContact.size(); i < len; i++) {
							String contact = allContact.get(i);
				%>
				<tr class="jive-<%=i % 2 == 0 ? "even" : "odd"%>">
					<td><%=contact%></td>
					<td><a href="chatLogs-service.jsp?sender=${param.sender}&receiver=<%=contact%>">和他/她的聊天记录信息</a></td>
					<td><a href="chatLogs-service.jsp?receiver=<%=contact%>">和他/她的聊天记录信息</a></td>
				</tr>

				<%
					}

					}
				%>

			</tbody>

		</table>

	</div>

</body>

</html>