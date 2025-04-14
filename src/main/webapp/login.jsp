<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login - Tech Store</title>
        <link rel="stylesheet" href="CSS/loginForm.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body>
        <!-- Navbar -->
        <header style="width: 100%;">
            <%@ include file="navbar.jsp" %>
        </header>

        <!-- Container Layout -->
        <div class="container">
            <!-- Left Section with Video Background -->
            <div class="left-section">
                <video autoplay muted loop id="background-video">
                    <source src="./img/background_vid.mp4" type="video/mp4">
                    Your browser does not support the video tag.
                </video>
            </div>

            <!-- Right Section Login Form -->
            <div class="right-section">
                <div class="login-container">
                    <h2>Sign In</h2>
                    <p class="subtext">Welcome back! Please enter your details</p>
                    <form action="login" method="post">
                        <div class="input-group">
                            <label for="email">Email</label>
                            <input type="email" id="email" name="email" placeholder="Enter your email..." required>
                        </div>

                        <div class="input-group">
                            <label for="password">Password</label>
                            <div class="password-wrapper">
                                <input type="password" id="password" name="password" placeholder="Enter password..." required>
                                <span class="toggle-password"><i class="fa fa-eye-slash"></i></span>
                            </div>
                        </div>

                        <div class="extra-options">
                            <a href="forgotPassword.jsp" class="forgot-password">Forgot password?</a>
                        </div>

                        <button type="submit" class="login-btn">Login</button>

                        <% if (request.getAttribute("errorMessage") != null) { %>
                        <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
                        <% } %>

                        <p class="signup-text">Don't have an account? <a href="register.jsp">Sign up now!</a></p>
                    </form>
                </div>
            </div>
        </div>

        <script>
            document.querySelector(".toggle-password").addEventListener("click", function () {
                let passwordInput = document.getElementById("password");
                let icon = this.querySelector("i");

                if (passwordInput.type === "password") {
                    passwordInput.type = "text";
                    icon.classList.remove("fa-eye-slash");
                    icon.classList.add("fa-eye");
                } else {
                    passwordInput.type = "password";
                    icon.classList.remove("fa-eye");
                    icon.classList.add("fa-eye-slash");
                }
            });
        </script>
    </body>

</html>