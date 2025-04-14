<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.Connection, java.util.List, com.mvc.model.Customer, com.mvc.DAO.CustomerDAO, com.mvc.dal.DBContext" %>

<%
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");

    if (customers == null) {
        Connection connection = DBContext.getConnection(); 
        CustomerDAO customerDAO = new CustomerDAO(connection);
        customers = customerDAO.getAllCustomers();
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
<% if ("lock_success".equals(success)) { %>
<div id="successPopup" class="popup success">
    <div class="popup-content">Customer locked successfully!</div>
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
<% } else if ("unlock_success".equals(success)) { %>
<div id="successPopup" class="popup success">
    <div class="popup-content">Customer unlocked successfully!</div>
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
<% if ("lock_failed".equals(error)) { %>
<div id="errorPopup" class="popup error">
    <div class="popup-content">Error: Failed to lock customer.</div>
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
<% } else if ("unlock_failed".equals(error)) { %>
<div id="errorPopup" class="popup error">
    <div class="popup-content">Error: Failed to unlock customer.</div>
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
        <link rel="stylesheet" href="CSS/CustomerManagement.css">
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
            <div class="sidebar-header">
                <h2>Admin Dashboard</h2>
            </div>
            <nav>
                <ul>
                    <li>
                        <a href="admin" class="<%= "admin".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-users"></i> Staff Management
                        </a>
                    </li>
                    <li>
                        <a href="RevenueReport.jsp" class="<%= "RevenueReport.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-chart-line"></i> View Revenue Report
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement" class="<%= "ProductManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="CustomerManagement.jsp" class="<%= "CustomerManagement.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-user-friends"></i> Customer Management
                        </a>
                    </li>
                    <li>
                        <a href="ListOrderManagement" class="<%= "ListOrderManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-shopping-cart"></i> Order Management
                        </a>
                    </li>
                    <li>
                        <a href="logout" class="logout">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="main-content">
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
                        <td data-label="ID"><%= customer.getUserId() %></td>
                        <td data-label="Email"><%= customer.getEmail() %></td>
                        <td data-label="First Name"><%= customer.getFirstName() %></td>
                        <td data-label="Last Name"><%= customer.getLastName() %></td>
                        <td data-label="Phone"><%= customer.getPhone() %></td>
                        <td data-label="Address"><%= customer.getAddress() %></td>
                        <td data-label="Loyalty Points"><%= customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : "0" %></td>
                        <td data-label="Payment Method"><%= customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "N/A" %></td>
                        <td data-label="Actions" style="text-align: center;">
                            <div class="button_gr">
                                <button class="view_button" onclick="location.href = 'viewCustomer.jsp?userId=<%= customer.getUserId() %>'">View</button>
                                <button class="edit_button" onclick="location.href = 'editCustomer.jsp?userId=<%= customer.getUserId() %>'">Edit</button>
                                <% if ("active".equals(customer.getStatus())) { %>
                                <button type="button" class="lock_button" onclick="confirmLock(<%= customer.getUserId() %>)">Lock</button>
                                <form id="lock-form-<%= customer.getUserId() %>" action="LockCustomerServlet" method="post" style="display: none;">
                                    <input type="hidden" name="userId" value="<%= customer.getUserId() %>">
                                    <input type="hidden" name="action" value="lock">
                                </form>
                                <% } else if ("disable".equals(customer.getStatus())) { %>
                                <button type="button" class="unlock_button" onclick="confirmUnlock(<%= customer.getUserId() %>)">Unlock</button>
                                <form id="unlock-form-<%= customer.getUserId() %>" action="LockCustomerServlet" method="post" style="display: none;">
                                    <input type="hidden" name="userId" value="<%= customer.getUserId() %>">
                                    <input type="hidden" name="action" value="unlock">
                                </form>
                                <% } %>
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
        </div>
        <script>
            function showSection(sectionId) {
                document.getElementById("staffManagement").style.display = "none";
                document.getElementById("revenueReport").style.display = "none";
                document.getElementById("ProductManagement").style.display = "none";
                document.getElementById("CustomerManagement").style.display = "none";

                if (sectionId === "ProductManagement") {
                    window.location.href = "ProductManagement";
                } else {
                    document.getElementById(sectionId).style.display = "block";
                }
            }

            function confirmDisable() {
                if (confirm("Are you sure you want to disable this account?")) {
                    let form = document.createElement("form");
                    form.method = "POST";
                    form.action = "DisableAccountServlet";
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function confirmLock(userId) {
                if (confirm("Are you sure you want to lock this customer?")) {
                    document.getElementById("lock-form-" + userId).submit();
                }
            }

            function confirmUnlock(userId) {
                if (confirm("Are you sure you want to unlock this customer?")) {
                    document.getElementById("unlock-form-" + userId).submit();
                }
            }
        </script>
    </body>
</html>