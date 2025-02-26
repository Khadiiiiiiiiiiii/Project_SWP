<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login</title>
        <link rel="stylesheet" href="CSS/loginPage.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body>

        <%@ include file="navbar.jsp" %> 

        <div class="login-container">
            <h2>Login</h2>

            <form action="login" method="post">
                <div class="input-group">
                    <label>Email:</label>
                    <input type="email" name="email" placeholder="Enter email..." required>
                </div>

                <div class="input-group">
                    <label>Password:</label>
                    <div class="password-wrapper">
                        <input type="password" id="password" name="password" placeholder="Enter password" required>
                        <i class="fa-solid fa-eye-slash password-toggle" onclick="togglePassword('password', this)"></i>
                    </div>
                </div>

                <a href="forgotPassword.jsp" class="forgot-password">Forgot password?</a>

                <button type="submit" class="login-btn">Login</button>

                <% if (request.getAttribute("errorMessage") != null) { %>
                <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
                <% } %>

                <p class="signup-text">Don't have an account? <a href="register.jsp">Sign up now!</a></p>
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
