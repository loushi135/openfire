<%@ page import="java.util.*"
    errorPage="error.jsp"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<html>
   <head>
       <title>My Plugin Page</title>

       <meta name="pageID" content="test1"/>
   </head>
   <body>
        Body here! test1!!!!!!!!!
        <fmt:message key="projectName" />
   </body>
   </html>