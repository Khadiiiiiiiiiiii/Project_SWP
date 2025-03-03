<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Add New Customer</title>
        <link rel="stylesheet" href="CSS/addCustomer.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
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
    </head>
    <body>
        <div class="container">
            <div class="form-container">
                <h2>Add New Customer</h2>
                <form action="AddCustomerServlet" method="post">
                    <label>Email:</label>
                    <input type="email" name="email" required><br>

                    <label>Password:</label>
                    <div class="password-container">
                        <input type="password" id="password" name="password" required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('password', this)"></i>
                    </div><br>

                    <label>Confirm Password:</label>
                    <div class="password-container">
                        <input type="password" id="confirmPassword" name="confirmPassword" required>
                        <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('confirmPassword', this)"></i>
                    </div><br>

                    <label>First Name:</label>
                    <input type="text" name="firstName" required><br>

                    <label>Last Name:</label>
                    <input type="text" name="lastName" required><br>

                    <label>Phone:</label>
                    <input type="text" name="phone"><br>

                    <label>Address:</label>
                    <textarea name="address" required></textarea><br>

                    <label>Loyalty Points:</label>
                    <input type="number" name="loyaltyPoints" value="0"><br>

                    <label>Preferred Payment Method:</label>
                    <input type="text" name="preferredPaymentMethod"><br>

                    <input type="submit" value="Add Customer">
                    <a href="staffDashboard.jsp">Back to Dashboard</a>
                </form>
            </div>
        </div>
    </body>
</html>