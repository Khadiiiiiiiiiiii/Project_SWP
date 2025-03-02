<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.User" %>

<%
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");

    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");

    String fullName = (String) request.getAttribute("name");
    String phone = (String) request.getAttribute("phone");
    String address = (String) request.getAttribute("address");

    if (fullName == null) fullName = user.getFirstName() + " " + user.getLastName();
    if (phone == null) phone = user.getPhone() != null ? user.getPhone() : "";
    if (address == null) address = user.getAddress() != null ? user.getAddress() : "";
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Account Information</title>
        <link rel="stylesheet" href="./CSS/viewProfile.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body class="banner-background profile-page">
        <img src='./img/banner.jpg' class='banner'>

        <%@ include file="navbar.jsp" %> 

        <div class="profile-container">
            <!-- Sidebar -->
            <aside class="sidebar">
                <div class="user-info">
                    <i class="fa-solid fa-user-circle user-icon"></i>
                    <p class="user-name"><%= user.getFirstName() %> <%= user.getLastName() %></p>
                </div>
                <nav class="menu">
                    <a href="viewProfile.jsp" class="active">
                        <i class="fa-solid fa-user" style="padding-right: 5px;"></i>Account Information
                    </a>
                    <a href="#">
                        <i class="fa-solid fa-cart-shopping" style="padding-right: 5px;"></i>Order History
                    </a>
                    <a href="#">
                        <i class="fa-solid fa-truck-fast" style="padding-right: 5px;"></i>Track Order
                    </a>
                    <a href="/logout" class="logout">
                        <i class="fa-solid fa-right-from-bracket" style="padding-right: 5px;"></i>Log out
                    </a>
                </nav>
            </aside>

            <!-- Profile Content -->
            <main class="profile-content">
                <h2>Account Information</h2>

                <% if (successMessage != null) { %>
                <p class="success-message"><%= successMessage %></p>
                <% } %>

                <% if (errorMessage != null) { %>
                <p class="error-message"><%= errorMessage %></p>
                <% } %>

                <form action="update" method="post" class="viewForm">
                    <div class="viewLabel">
                        <label>Name:</label>
                    </div>
                    <div class="viewInput">
                        <input type="text" name="name" value="<%= fullName %>" required>
                    </div>

                    <div class="viewLabel">
                        <label>Phone Number:</label>
                    </div>
                    <div class="viewInput">
                        <input type="text" name="phone" value="<%= phone %>" required>
                    </div>

                    <div class="viewLabel">
                        <label>Address:</label>
                    </div>
                    <div class="viewInput">
                        <input type="text" name="address" value="<%= address %>">
                    </div>

                    <div class="viewLabel">
                        <label>Email:</label>
                    </div>
                    <div class="viewInput">
                        <input type="email" name="email" value="<%= user.getEmail() %>" required>
                    </div>

                    <a href="changePassword.jsp" class="changePass" 
                       style="color: #33ccff; text-decoration: none; transition: color 0.3s ease;"
                       onmouseover="this.style.color = '#ff6600'" 
                       onmouseout="this.style.color = '#33ccff'">
                        Click here to change password!
                    </a>

                    <div class="buttons">
                        <button type="submit" class="save-btn">Save Change</button>

                        <form id="deleteForm" action="deleteAccount" method="post">
                            <button type="button" class="delete-btn" onclick="confirmDelete()">Delete Account</button>
                        </form>
                    </div>

                    <script>
                        function confirmDelete() {
                            if (confirm("Are you sure to delete this account?")) {
                                document.getElementById("deleteForm").submit();
                            }
                        }
                    </script>
                </form>
            </main>
        </div>

    </body>
</html>
