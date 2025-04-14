<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.User" %>
<%@ page import="com.mvc.DAO.UserDAO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");
    String successMessage = (String) request.getAttribute("successMessage");
    // String errorMessage = (String) request.getAttribute("errorMessage"); // Không cần nữa vì dùng errors
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

    // Xử lý thông báo lỗi từ tham số URL
    if ("disable_failed".equals(errorParam)) {
        request.setAttribute("errorMessage", "Failed to disable account. Please try again.");
    } else if ("server_error".equals(errorParam)) {
        request.setAttribute("errorMessage", "Server error occurred. Please contact support.");
    }
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Edit Profile</title>
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
                    <a href="purchaseHistory.jsp"><i class="fa-solid fa-cart-shopping"></i> Order History</a>
                    <a href="logout" class="logout"><i class="fa-solid fa-right-from-bracket"></i> Log out</a>
                </nav>
            </aside>

            <main class="profile-content">
                <h2>Edit Profile</h2>

                <% if (successMessage != null) { %>
                <p class="success-message"><%= successMessage %></p>
                <% } %>

                <%-- Hiển thị danh sách lỗi từ UpdateProfileServlet --%>
                <c:if test="${not empty errors}">
                    <ul class="error-list">
                        <c:forEach var="error" items="${errors}">
                            <li class="error-message">${error}</li>
                            </c:forEach>
                    </ul>
                </c:if>

                <%-- Hiển thị lỗi đơn từ errorMessage (nếu có) --%>
                <c:if test="${not empty errorMessage}">
                    <p class="error-message">${errorMessage}</p>
                </c:if>

                <%-- Hiển thị chi tiết lỗi khi server_error --%>
                <% if ("server_error".equals(errorParam) && errorDetails != null) { %>
                <p class="error-details">Details: <%= errorDetails %></p>
                <% } %>

                <form action="update" method="post" class="viewForm">
                    <label>Name:</label>
                    <input type="text" name="name" value="<%= fullName %>" required>

                    <label>Phone Number:</label>
                    <input type="text" name="phone" value="<%= phone %>" required>

                    <label>Address:</label>
                    <input type="text" name="address" value="<%= address %>">

                    <label>Payment Method:</label>
                    <select name="paymentMethod" required>
                        <option value="Credit Card" <%= "Credit Card".equals(paymentMethod) ? "selected" : "" %>>Credit Card</option>
                        <option value="PayPal" <%= "PayPal".equals(paymentMethod) ? "selected" : "" %>>PayPal</option>
                        <option value="Cash on Delivery" <%= "Cash on Delivery".equals(paymentMethod) ? "selected" : "" %>>Cash on Delivery</option>
                    </select>

                    <label>Email:</label>
                    <input type="email" name="email" value="<%= user.getEmail() %>" readonly>

                    <a href="changePassword.jsp" class="changePass">Click here to change password!</a>
                    <button type="submit" class="save-btn">Save Change</button>
                    <a href="viewProfile.jsp" class="changePass" style="padding-top: 10px; text-align: center;">Back to Profile Page!</a>
                </form>

                <script>
                    function confirmDisable() {
                        if (confirm("Are you sure you want to delete  this account?")) {
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