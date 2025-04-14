<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Add New Customer</title>
        <link rel="stylesheet" href="CSS/addCustomer.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body>
        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Admin Dashboard</h2>
            </div>
            <nav>
                <ul>
                    <li>
                        <a href="admin-dashboard.jsp" class="<%= "admin-dashboard.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-users"></i> Staff Management
                        </a>
                    </li>
                    <li>
                        <a href="RevenueReport.jsp" class="<%= "RevenueReport.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-chart-line"></i> View Revenue Report
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement.jsp" class="<%= "ProductManagement.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="CustomerManagement.jsp" class="active">
                            <i class="fas fa-user-friends"></i> Customer Management
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
        <div class="container">
            <div class="form-container">
                <h2>Add New Customer</h2>

                <%-- Hi?n th? thông báo l?i t? servlet --%>
                <%
                    List<String> errors = (List<String>) request.getAttribute("errors");
                    if (errors != null && !errors.isEmpty()) {
                        out.println("<div class='error-messages'>");
                        out.println("<h3>Please correct the following errors:</h3>");
                        out.println("<ul>");
                        for (String error : errors) {
                            out.println("<li>" + error + "</li>");
                        }
                        out.println("</ul>");
                        out.println("</div>");
                    }
                %>

                <form name="customerForm" action="AddCustomerServlet" method="post">
                    <label>Email:</label>
                    <input type="email" name="email" placeholder="Enter email..." value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>" required>

                    <label>Password:</label>
                    <div class="password-container">
                        <input type="password" id="password" name="password" placeholder="Enter password..." required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('password', this)"></i>
                    </div>

                    <label>Confirm Password:</label>
                    <div class="password-container">
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Enter confirm password..."required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('confirmPassword', this)"></i>
                    </div>

                    <label>First Name:</label>
                    <input type="text" name="firstName" placeholder="Enter first name..." value="<%= request.getAttribute("firstName") != null ? request.getAttribute("firstName") : "" %>" required>

                    <label>Last Name:</label>
                    <input type="text" name="lastName" placeholder="Enter last name..." value="<%= request.getAttribute("lastName") != null ? request.getAttribute("lastName") : "" %>" required>

                    <label>Phone:</label>
                    <input type="text" name="phone" placeholder="Enter phone..." value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>" required>

                    <label>Address:</label>
                    <textarea name="address" placeholder="Enter address..." required><%= request.getAttribute("address") != null ? request.getAttribute("address") : "" %></textarea>

                    <label>Loyalty Points:</label>
                    <input type="number" name="loyaltyPoints" value="<%= request.getAttribute("loyaltyPoints") != null ? request.getAttribute("loyaltyPoints") : "0" %>" min="0" required>

                    <label>Preferred Payment Method:</label>
                    <div class="select-container">
                        <select name="preferredPaymentMethod" required>
                            <option value="Credit Card">Credit Card</option>
                            <option value="Debit Card">Debit Card</option>
                            <option value="Mobile Payment">Mobile Payment</option>
                            <option value="Cash on Delivery">Cash on Delivery</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>
                    <input type="submit" value="Add Customer">
                    <a href="CustomerManagement.jsp" style="text-align: center;">Back to Dashboard</a>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    function togglePassword(fieldId, icon) {
        let field = document.getElementById(fieldId);
        if (field.type === "password") {
            field.type = "text";
            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");
        } else {
            field.type = "password";
            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");
        }
    }
</script>
</body>
</html>