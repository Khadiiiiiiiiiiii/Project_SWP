<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Register</title>
        <link rel="stylesheet" href="CSS/register.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <script src="https://accounts.google.com/gsi/client" async defer></script>
    </head>
    <body>

        <%@ include file="navbar.jsp" %>

        <div class="auth-container">
            <h2>Sign Up</h2>

            <%-- Hiển thị thông báo lỗi nếu có --%>
            <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error-message" style="color: red;"><%= request.getAttribute("errorMessage") %></p>
            <% } %>

            <form action="register" method="post" onsubmit="return validateForm()">
                <div class="input-group">
                    <label>Email:</label>
                    <input type="email" name="email" placeholder="Enter your email" value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>" required>
                </div>

                <div class="input-group">
                    <label>Password:</label>
                    <div class="password-container">
                        <input type="password" id="password" name="password" placeholder="Enter password" required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('password', this)"></i>
                    </div>
                </div>

                <div class="input-group">
                    <label>Confirm Password:</label>
                    <div class="password-container">
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm password" required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('confirmPassword', this)"></i>
                    </div>
                </div>

                <div class="input-group">
                    <label>First Name:</label>
                    <input type="text" name="firstName" placeholder="First Name" value="<%= request.getParameter("firstName") != null ? request.getParameter("firstName") : "" %>" required>
                </div>

                <div class="input-group">
                    <label>Last Name:</label>
                    <input type="text" name="lastName" placeholder="Last Name" value="<%= request.getParameter("lastName") != null ? request.getParameter("lastName") : "" %>" required>
                </div>

                <div class="input-group">
                    <label>Phone:</label>
                    <input type="text" name="phone" placeholder="Phone Number" value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>">
                </div>

                <div class="input-group">
                    <label>Address:</label>
                    <input type="text" name="address" placeholder="Address" value="<%= request.getParameter("address") != null ? request.getParameter("address") : "" %>">
                </div>

                <input type="submit" class="auth-btn" value="Create Account">
            </form>

            <p class="signup-text">Already have an account? <a href="login.jsp">Login here</a></p>
        </div>

        <script>
            function validateForm() {
                let password = document.getElementById("password").value;
                let confirmPassword = document.getElementById("confirmPassword").value;

                if (password !== confirmPassword) {
                    alert("Passwords do not match! Please re-enter.");
                    return false;
                }
                return true;
            }

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

        <style>
            .password-container {
                display: flex;
                align-items: center;
                position: relative;
            }

            .password-container input {
                width: 100%;
                padding-right: 40px;
            }

            .toggle-password {
                position: absolute;
                right: 10px;
                cursor: pointer;
                color: #888;
            }

            .toggle-password:hover {
                color: #000;
            }
        </style>

    </body>
</html>
