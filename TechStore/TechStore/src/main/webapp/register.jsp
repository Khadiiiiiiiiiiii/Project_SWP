<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Register</title>
        <link rel="stylesheet" href="CSS/register.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <script src="https://accounts.google.com/gsi/client" async defer></script>
    </head>
    <body>

        <%@ include file="navbar.jsp" %> <!-- Import navbar -->

        <div class="auth-container">
            <h2>Sign Up</h2>

            <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
            <% } %>

            <form action="register" method="post" onsubmit="return validatePassword()">
                <div class="input-group">
                    <label>Email:</label>
                    <input type="email" name="email" placeholder="Enter your email" required>
                </div>

                <div class="input-group">
                    <label>Password:</label>
                    <input type="password" id="password" name="password" placeholder="Enter password" required>
                </div>

                <div class="input-group">
                    <label>Confirm Password:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm password" required>
                </div>

                <div class="input-group">
                    <label>First Name:</label>
                    <input type="text" name="firstName" placeholder="First Name" required>
                </div>

                <div class="input-group">
                    <label>Last Name:</label>
                    <input type="text" name="lastName" placeholder="Last Name" required>
                </div>

                <div class="input-group">
                    <label>Phone:</label>
                    <input type="text" name="phone" placeholder="Phone Number">
                </div>

                <div class="input-group">
                    <label>Address:</label>
                    <input type="text" name="address" placeholder="Address">
                </div>

                <input type="submit" class="auth-btn" value="Create Account">
            </form>

            <p class="divider">Or sign up with</p>

            <p class="signup-text">Already have an account? <a href="login.jsp">Login here</a></p>
        </div>

        <script>
            function validatePassword() {
                let password = document.getElementById("password").value;
                let confirmPassword = document.getElementById("confirmPassword").value;

                if (password !== confirmPassword) {
                    alert("Passwords do not match! Please re-enter.");
                    return false; // Ngăn không cho form submit
                }
                return true;
            }
        </script>

    </body>
</html>
