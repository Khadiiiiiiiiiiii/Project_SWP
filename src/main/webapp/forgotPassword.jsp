<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Forgot Password</title>
        <link rel="stylesheet" href="CSS/forgotPassword.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body class="reset-body">

        <%@ include file="navbar.jsp" %>

        <div class="reset-container">
            <h2 class="reset-title">Reset Password</h2>

            <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
            <% } %>

            <% if (session.getAttribute("successMessage") != null) { %>
            <p class="success-message"><%= session.getAttribute("successMessage") %></p>
            <% session.removeAttribute("successMessage"); %> <!-- Xóa thông báo sau khi hiển thị -->
            <% } %>
            <form action="forgotPassword" method="post">
                <label for="email" class="reset-label">Enter your email:</label>
                <input type="email" id="email" name="email" class="reset-input" placeholder="Enter your email" required>

                <label for="newPassword" class="reset-label">New Password:</label>
                <div class="reset-input-wrapper">
                    <input type="password" id="newPassword" name="newPassword" class="reset-input" placeholder="Enter new password" required>
                    <i class="fa-solid fa-eye-slash reset-eye-icon" onclick="togglePassword('newPassword', this)"></i>
                </div>

                <label for="confirmPassword" class="reset-label">Confirm Password:</label>
                <div class="reset-input-wrapper">
                    <input type="password" id="confirmPassword" name="confirmPassword" class="reset-input" placeholder="Confirm new password" required>
                    <i class="fa-solid fa-eye-slash reset-eye-icon" onclick="togglePassword('confirmPassword', this)"></i>
                </div>

                <button type="submit" class="reset-btn">Reset Password</button>
                <a href="login.jsp" class="back-to-login">Back to Login</a>
            </form>
        </div>

        <script>
            function togglePassword(inputId, iconElement) {
                let input = document.getElementById(inputId);
                if (input.type === "password") {
                    input.type = "text";
                    iconElement.classList.remove("fa-eye-slash");
                    iconElement.classList.add("fa-eye");
                } else {
                    input.type = "password";
                    iconElement.classList.remove("fa-eye");
                    iconElement.classList.add("fa-eye-slash");
                }
            }
        </script>

    </body>
</html>
