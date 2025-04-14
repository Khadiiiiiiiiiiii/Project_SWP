<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Register</title>
        <link rel="stylesheet" href="CSS/RegisterPage.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <script src="https://accounts.google.com/gsi/client" async defer></script>
    </head>
    <body>
        <%@ include file="navbar.jsp" %>

        <div class="container">
            <div class="video-container">
                <video autoplay muted loop>
                    <source src="./img/background_vid.mp4" type="video/mp4">
                    Your browser does not support the video tag.
                </video>
            </div>

            <div class="auth-container">
                <div class="form-container">
                    <h2>SIGN UP</h2>
                    <p class="subtext">Create a new account! Please enter your details</p>

                    <div class="form-scrollable">
                        <form action="register" method="post" onsubmit="return validateForm()">
                            <div class="input-group">
                                <label for="email">Email</label>
                                <input type="email" name="email" placeholder="Enter your email..." 
                                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>" 
                                       required>
                            </div>

                            <div class="input-group">
                                <div class="password-container">
                                    <label for="password">Password</label>
                                    <input type="password" id="password" name="password" 
                                           placeholder="Enter password..." required>
                                    <i class="fas fa-eye-slash toggle-password" 
                                       onclick="togglePassword('password', this)"></i>
                                </div>
                            </div>

                            <div class="input-group">
                                <div class="password-container">
                                    <label for="confirmPassword">Confirm Password</label>
                                    <input type="password" id="confirmPassword" name="confirmPassword" 
                                           placeholder="Confirm password..." required>
                                    <i class="fas fa-eye-slash toggle-password" 
                                       onclick="togglePassword('confirmPassword', this)"></i>
                                </div>
                            </div>

                            <div class="input-group">
                                <label for="firstName">First Name</label>
                                <input type="text" name="firstName" placeholder="First Name..." 
                                       value="<%= request.getParameter("firstName") != null ? request.getParameter("firstName") : "" %>" 
                                       required>
                            </div>

                            <div class="input-group">
                                <label for="lastName">Last Name</label>
                                <input type="text" name="lastName" placeholder="Last Name..." 
                                       value="<%= request.getParameter("lastName") != null ? request.getParameter("lastName") : "" %>" 
                                       required>
                            </div>

                            <div class="input-group">
                                <label for="phone">Phone</label>
                                <input type="text" name="phone" placeholder="Phone Number..." 
                                       value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>" 
                                       required>
                            </div>

                            <div class="input-group">
                                <label for="address">Address</label>
                                <input type="text" name="address" placeholder="Address..." 
                                       value="<%= request.getParameter("address") != null ? request.getParameter("address") : "" %>" 
                                       required>
                            </div>

                            <input type="submit" class="auth-btn" value="Create Account">

                            <%
                                String successMessage = (String) request.getAttribute("successMessage");
                                String errorMessage = (String) request.getAttribute("errorMessage");
                                if (successMessage != null) {
                            %>
                            <p id="register-message" class="message success-message"><%= successMessage %></p>
                            <%
                                } else if (errorMessage != null) {
                            %>
                            <p id="register-message" class="message error-message"><%= errorMessage %></p>
                            <%
                                }
                            %>
                            <p class="signup-text">Already have an account? <a href="login.jsp">Sign in now!</a></p>
                        </form>
                    </div>
                </div>
            </div>
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

            // Add fade-in animation on page load
            window.addEventListener('load', () => {
                const formContainer = document.querySelector('.form-container');
                formContainer.style.opacity = '0';
                setTimeout(() => {
                    formContainer.style.transition = 'opacity 0.5s ease-in-out';
                    formContainer.style.opacity = '1';
                }, 100);
            });
        </script>
    </body>
</html>