<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Change Password</title>
        <link rel="stylesheet" href="CSS/ChangePass.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body>

        <header><%@ include file="navbar.jsp" %></header>

        <div class="auth-container">
            <h2>Change Password</h2>
            <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
            <% } %>

            <% if (request.getAttribute("successMessage") != null) { %>
            <p class="success-message"><%= request.getAttribute("successMessage") %></p>
            <a href="viewProfile.jsp" class="back-to-profile">Back to Profile</a>
            <% } else { %>
            <form action="changePassword" method="post" onsubmit="return validateForm()">
                <div class="input-group">
                    <label>Old Password:</label>
                    <div class="password-container">
                        <input type="password" id="oldPassword" name="oldPassword" required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('oldPassword', this)"></i>
                    </div>
                </div>

                <div class="input-group">
                    <label>New Password:</label>
                    <div class="password-container">
                        <input type="password" id="newPassword" name="newPassword" required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('newPassword', this)"></i>
                    </div>
                </div>

                <div class="input-group">
                    <label>Confirm New Password:</label>
                    <div class="password-container">
                        <input type="password" id="confirmNewPassword" name="confirmNewPassword" required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('confirmNewPassword', this)"></i>
                    </div>
                </div>

                <button type="submit" class="auth-btn">Save Changes</button>
                <a href="viewProfile.jsp" class="account-info-link" style="text-decoration: none;">Click here to view Account Information!</a>
            </form>
            <% } %>
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