<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.User" %>
<%@ page import="com.mvc.DAO.UserDAO" %>

<%
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String errorParam = request.getParameter("error");
    String errorDetails = request.getParameter("details");

    String fullName = (String) request.getAttribute("name");
    String phone = (String) request.getAttribute("phone");
    String address = (String) request.getAttribute("address");
    String paymentMethod = (String) request.getAttribute("paymentMethod");

    if (fullName == null) fullName = user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "");
    if (phone == null) phone = user.getPhone() != null ? user.getPhone() : "";
    if (address == null) address = user.getAddress() != null ? user.getAddress() : "";
    if (paymentMethod == null) {
        UserDAO userDAO = new UserDAO();
        try {
            paymentMethod = userDAO.getPreferredPaymentMethod(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            paymentMethod = "";
        }
    }

    // Xử lý thông báo lỗi
    if ("disable_failed".equals(errorParam)) {
        errorMessage = "Failed to disable account. Please try again.";
    } else if ("server_error".equals(errorParam)) {
        errorMessage = "Server error occurred. Please contact support.";
    }
%>

<% if ("server_error".equals(errorParam)) { %>
<p class="error-message">Server error occurred. Please contact support.</p>
<%-- Hiển thị chi tiết lỗi khi debug, xóa dòng này sau khi sửa lỗi xong --%>
<% if (errorDetails != null) { %>
<p class="error-details">Details: <%= errorDetails %></p>
<% } %>
<% } else if ("delete_constraint_failed".equals(errorParam)) { %>
<p class="error-message">Cannot delete account due to existing constraints.</p>
<% } %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Account Information</title>
        <link rel="stylesheet" href="CSS/viewCustomerProfile.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body class="banner-background profile-page">
        <%@ include file="navbar.jsp" %>

        <div class="profile-container">
            <aside class="sidebar">
                <div class="user-info">
                    <i class="fa-solid fa-user-circle user-icon"></i>
                    <p class="user-name"><%= user.getFirstName() %> <%= user.getLastName() %></p>
                </div>
                <nav class="menu">
                    <a href="viewProfile.jsp" class="active"><i class="fa-solid fa-user"></i> Account Info</a>
                    <a href="historyOrder"><i class="fa-solid fa-cart-shopping"></i> Order History</a>
                    <a href="logout" class="logout"><i class="fa-solid fa-right-from-bracket"></i> Log out</a>
                </nav>
            </aside>

            <main class="profile-content">
                <h2>Account Information</h2>

                <% if (successMessage != null) { %>
                <p class="success-message"><%= successMessage %></p>
                <% } %>

                <% if (errorMessage != null) { %>
                <p class="error-message"><%= errorMessage %></p>
                <% } %>

                <label>Name:</label>
                <input type="text" name="name" value="<%= fullName %>" readonly>

                <label>Phone Number:</label>
                <input type="text" name="phone" value="<%= phone %>" readonly>

                <label>Address:</label>
                <input type="text" name="address" value="<%= address %>" readonly>

                <label>Payment Method:</label>
                <input type="text" name="paymentMethod" value="<%= paymentMethod %>" readonly>

                <label>Email:</label>
                <input type="email" name="email" value="<%= user.getEmail() %>" readonly>

                <a href="changePassword.jsp" class="changePass">Click here to change password!</a>
                <button onclick="window.location.href = 'editProfile.jsp'" class="save-btn">Edit Account</button>
                <button type="button" class="delete-btn" onclick="confirmDisable()">Delete Account</button>

                <script>
                    function confirmDisable() {
                        if (confirm("Are you sure you want to disable this account?")) {
                            let form = document.createElement("form");
                            form.method = "POST";
                            form.action = "DisableAccountServlet";
                            document.body.appendChild(form);
                            form.submit();
                        }
                    }
                </script>
            </main>
        </div>
    </body>
</html>