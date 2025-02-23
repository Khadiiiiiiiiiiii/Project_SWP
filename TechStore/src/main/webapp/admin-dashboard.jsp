<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Admin".equals(user.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
    <head>
        <title>Admin Dashboard</title>
    </head>
    <body>
        <h2>Welcome Admin</h2>
        <p>Xin chào, <%= user.getEmail() %></p>
        <a href="logout">Logout</a>
    </body>
</html>
