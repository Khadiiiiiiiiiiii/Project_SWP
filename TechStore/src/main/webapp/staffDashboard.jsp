<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.Connection, java.util.List, com.mvc.model.Customer, com.mvc.DAO.StaffDAO, com.mvc.dal.DBContext" %>

<%
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");

    if (customers == null) {
        Connection connection = DBContext.getConnection(); 
        StaffDAO staffDAO = new StaffDAO(connection);
        customers = staffDAO.getAllCustomers();
        request.setAttribute("customers", customers);
    }
%>

<% 
    String error = request.getParameter("error");
    String success = request.getParameter("success");
%>

<% if (error != null) { %>
<div id="errorPopup" class="popup error">
    <div class="popup-content">
        <% if ("missing_id".equals(error)) { %>Error: User ID is missing!<% } %>
        <% if ("delete_failed".equals(error)) { %>Error: Failed to delete customer.<% } %>
        <% if ("invalid_id".equals(error)) { %>Error: Invalid User ID!<% } %>
        <% if ("exception".equals(error)) { %>Error: System error. Please contact support.<% } %>
        <% if ("update_failed".equals(error)) { %>Error: Failed to update customer.<% } %>
        <% if ("add_failed".equals(error)) { %>Error: Failed to add customer.<% } %>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        var popup = document.getElementById("errorPopup");
        popup.style.display = "block";
        setTimeout(function () {
            popup.style.display = "none";
        }, 3000);
    });
</script>
<% } %>

<% if ("deleted".equals(success) || "updated".equals(success) || "added".equals(success)) { %>
<div id="successPopup" class="popup success">
    <div class="popup-content">
        <% if ("deleted".equals(success)) { %>Customer deleted successfully!<% } %>
        <% if ("updated".equals(success)) { %>Customer updated successfully!<% } %>
        <% if ("added".equals(success)) { %>Customer added successfully!<% } %>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        var popup = document.getElementById("successPopup");
        popup.style.display = "block";
        setTimeout(function () {
            popup.style.display = "none";
        }, 3000);
    });
</script>
<% } %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Staff Dashboard</title>
        <link rel="stylesheet" href="CSS/customerManagement.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <script>
            function confirmDelete(userId) {
                if (confirm("Are you sure you want to delete this customer?")) {
                    document.getElementById("delete-form-" + userId).submit();
                }
            }
        </script>
    </head>
    <body>
        <div class="sidebar">
            <h2><i class="fa-solid fa-bars"></i> Staff Dashboard</h2>
            <a href="#"><i class="fa-solid fa-cart-shopping"></i> Order Management</a>
            <a href="staffDashboard.jsp" class="active"><i class="fa-solid fa-user"></i> Customer Management</a>
            <a href="/logout" style="color: red;"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
        </div>

        <div class="container">
            <button class="add-new-btn" onclick="location.href = 'addCustomer.jsp'">Create Customer</button>
            <h2>Customer List</h2>
            <table border="1">
                <tr>
                    <th>ID</th>
                    <th>Email</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Loyalty Points</th>
                    <th>Payment Method</th>
                    <th>Actions</th>
                </tr>
                <% if (customers != null && !customers.isEmpty()) { %>
                <% for (Customer customer : customers) { %>
                <tr id="row-<%= customer.getUserId() %>">
                    <td><%= customer.getUserId() %></td>
                    <td><%= customer.getEmail() %></td>
                    <td><%= customer.getFirstName() %></td>
                    <td><%= customer.getLastName() %></td>
                    <td><%= customer.getPhone() %></td>
                    <td><%= customer.getAddress() %></td>
                    <td><%= customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : "0" %></td>
                    <td><%= customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "N/A" %></td>
                    <td style="text-align: center;">
                        <div class="button_gr">
                            <button class="view_button" onclick="location.href = 'viewCustomer.jsp?userId=<%= customer.getUserId() %>'">View</button>
                            <button class="edit_button" onclick="location.href = 'editCustomer.jsp?userId=<%= customer.getUserId() %>'">Edit</button>
                            <button type="button" class="delete_button" onclick="confirmDelete(<%= customer.getUserId() %>)">Delete</button>
                            <form id="delete-form-<%= customer.getUserId() %>" action="DeleteCustomerServlet" method="post" style="display: none;">
                                <input type="hidden" name="userId" value="<%= customer.getUserId() %>">
                            </form>
                        </div>
                    </td>
                </tr>
                <% } %>
                <% } else { %>
                <tr>
                    <td colspan="9">No customers found</td>
                </tr>
                <% } %>
            </table>
        </div>
    </body>
</html>