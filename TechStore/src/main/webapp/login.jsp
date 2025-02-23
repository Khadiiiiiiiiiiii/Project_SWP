<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login</title>
        <link rel="stylesheet" href="CSS/login.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body>

        <%@ include file="navbar.jsp" %> <!-- Import thanh navbar -->

        <div class="login-container">
            <h2>Login</h2>

            <form action="login" method="post">
                <div class="input-group">
                    <label>Email:</label>
                    <input type="email" name="email" placeholder="Enter username..." required>
                </div>

                <div class="input-group">
                    <label>Password:</label>
                    <input type="password" name="password" placeholder="Enter password..." required>
                </div>

                <a href="#" class="forgot-password">Forgot password</a>

                <button type="submit" class="login-btn">Login</button>

                <% if (request.getAttribute("errorMessage") != null) { %>
                <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
                <% } %>

                <div class="divider">Or login with</div>

                <!-- Google Sign-In Button -->
                <div class="google-btn" id="googleSignIn">
                    <i class="fa-brands fa-google"></i> Sign in with Google
                </div>

                <!-- Google Sign-In Script -->
                <script src="https://accounts.google.com/gsi/client" async defer></script>
                <script>
                    function handleCredentialResponse(response) {
                        // Gửi token ID Google đến LoginServlet
                        fetch('LoginServlet', {
                            method: 'POST',
                            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                            body: 'idToken=' + encodeURIComponent(response.credential)
                        }).then(res => window.location.href = 'Home.jsp');
                    }

                    window.onload = function () {
                        google.accounts.id.initialize({
                            client_id: "YOUR_GOOGLE_CLIENT_ID",
                            callback: handleCredentialResponse
                        });

                        google.accounts.id.renderButton(
                                document.getElementById("googleSignIn"),
                                {theme: "outline", size: "large"}
                        );

                        google.accounts.id.prompt();
                    }
                </script>


                <p class="signup-text">Don't have an account? <a href="register.jsp">Sign up now!</a></p>
            </form>
        </div>

    </body>
</html>
