<%@ page import="java.util.List" %>
<%@ page import="com.mvc.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<% 
    List<User> customers = (List<User>) request.getAttribute("customers");
%>

<html>
    <head>
        <title>Staff Dashboard</title>
        <style>
            .container {
                width: 90%;
                margin: 0 auto;
                padding: 20px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }
            th {
                background-color: #f2f2f2;
            }
            .logout-btn {
                float: right;
                padding: 5px 10px;
                background-color: #f44336;
                color: white;
                border: none;
                cursor: pointer;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2>Staff Dashboard</h2>
            <button class="logout-btn" onclick="window.location.href = 'logout'">Logout</button>
            <h3>Customer Management</h3>

            <% if (customers == null) { %>
            <p style="color:red;">Error: Customer data is NULL. Make sure to access the servlet first.</p>
            <% } else { %>
            <p>Total Customers: <%= customers.size() %></p>
            <% } %>

            <table>
                <tr>
                    <th>ID</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Address</th>
                </tr>
                <% 
                    if (customers != null && !customers.isEmpty()) {
                        for (User customer : customers) {
                %>
                <tr>
                    <td><%= customer.getUserId() %></td>
                    <td><%= customer.getFirstName() %></td>
                    <td><%= customer.getLastName() %></td>
                    <td><%= customer.getEmail() %></td>
                    <td><%= customer.getPhone() %></td>
                    <td><%= customer.getAddress() %></td>
                </tr>
                <% 
                        }
                    } else {
                %>
                <tr><td colspan="6" style="color:red; text-align:center;">No customers found</td></tr>
                <% 
                    }
                %>
            </table>
        </div>
    </body>
</html>
