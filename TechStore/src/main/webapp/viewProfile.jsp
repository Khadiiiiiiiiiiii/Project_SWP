<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.User" %>

<%
    // Kiểm tra session
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp"); // Chưa đăng nhập -> chuyển về trang đăng nhập
        return;
    }

    // Lấy thông tin user từ session
    User user = (User) session.getAttribute("user");
    String successMessage = request.getParameter("success");
    String errorMessage = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thông tin cá nhân</title>
        <link rel="stylesheet" href="CSS/viewProfile.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body>

        <%@ include file="navbar.jsp" %> 

        <div class="profile-container">
            <aside class="sidebar">
                <div class="user-info">
                    <i class="fa-solid fa-user-circle user-icon"></i>
                    <p class="user-name"><%= user.getFirstName() %> <%= user.getLastName() %></p>
                </div>
                <nav class="menu">
                    <a href="viewProfile.jsp" class="active">Account Information</a>
                    <a href="viewAddress.jsp">Address</a>
                    <a href="#">Order History</a>
                    <a href="#">Track Order</a>
                    <a href="/logout" class="logout">Log out</a>
                </nav>
            </aside>

            <main class="profile-content">
                <h2>Account Information</h2>

                <%-- Hiển thị thông báo thành công hoặc lỗi --%>
                <% if ("true".equals(successMessage)) { %>
                <p class="success-message">Profile updated successfully!</p>
                <% } else if ("update_failed".equals(errorMessage)) { %>
                <p class="error-message">Update failed. Please try again.</p>
                <% } else if ("server_error".equals(errorMessage)) { %>
                <p class="error-message">Server error. Please contact support.</p>
                <% } %>

                <form action="update" method="post">
                    <label>Name:</label>
                    <input type="text" name="name" value="<%= user.getFirstName() %> <%= user.getLastName() %>" required>

                    <label>Phone Number:</label>
                    <input type="text" name="phone" value="<%= user.getPhone() != null ? user.getPhone() : "" %>" required>

                    <label>Address:</label>
                    <input type="text" name="address" value="<%= user.getAddress() != null ? user.getAddress() : "" %>">

                    <label>Email:</label>
                    <input type="email" name="email" value="<%= user.getEmail() %>" readonly>

                    <div class="buttons">
                        <button type="submit" class="save-btn">Save Change</button>
                        <form id="deleteForm" action="deleteAccount" method="post">
                            <button type="button" class="delete-btn" onclick="confirmDelete()">Delete Account</button>
                        </form>
                    </div>

                    <script>
                        function confirmDelete() {
                            if (confirm("Bạn có chắc chắn muốn xóa tài khoản không?")) {
                                document.getElementById("deleteForm").submit();
                            }
                        }
                    </script>
                </form>
            </main>
        </div>

    </body>
</html>
