<%@ page import="java.util.*"
    errorPage="error.jsp"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<html>
   <head>
       <title>My Plugin Page</title>

       <meta name="pageID" content="lsq"/>
   </head>
   <body>
        Body here!
        <fmt:message key="projectName" />
   </body>
   </html>