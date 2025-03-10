<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.Connection, java.util.List, com.mvc.model.User, com.mvc.DAO.StaffDAO, com.mvc.dal.DBContext" %>

<%
    List<User> customers = (List<User>) request.getAttribute("customers");

    if (customers == null) {
        Connection connection = DBContext.getConnection(); // Cần import java.sql.Connection
        StaffDAO staffDAO = new StaffDAO(connection);
        customers = staffDAO.getAllCustomers();
        request.setAttribute("customers", customers);
    }
%>


<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Staff Dashboard</title>
        <link rel="stylesheet" href="CSS/style.css">
    </head>
    <body>
        <div class="container">
            <h2>Customer List</h2>
            <table border="1">
                <tr>
                    <th>ID</th>
                    <th>Email</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Phone</th>
                    <th>Address</th>
                </tr>
                <% if (customers != null && !customers.isEmpty()) { %>
                    <% for (User user : customers) { %>
                        <tr>
                            <td><%= user.getUserId() %></td>
                            <td><%= user.getEmail() %></td>
                            <td><%= user.getFirstName() %></td>
                            <td><%= user.getLastName() %></td>
                            <td><%= user.getPhone() %></td>
                            <td><%= user.getAddress() %></td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="6">No customers found</td>
                    </tr>
                <% } %>
            </table>
        </div>
    </body>
</html>
