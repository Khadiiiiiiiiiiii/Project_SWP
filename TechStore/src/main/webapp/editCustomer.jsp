<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.Customer, com.mvc.DAO.StaffDAO, com.mvc.dal.DBContext, java.sql.Connection" %>

<%
    String userIdStr = request.getParameter("userId");
    Customer customer = null;
    if (userIdStr != null) {
        try {
            int userId = Integer.parseInt(userIdStr);
            Connection connection = DBContext.getConnection();
            StaffDAO staffDAO = new StaffDAO(connection);
            customer = staffDAO.getCustomerById(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Edit Customer</title>
        <link rel="stylesheet" href="CSS/editCustomer.css">
    </head>
    <body>
        <div class="container" id="edit-customer-container">
            <h2 class="title">Edit Customer</h2>
            <% if (customer != null) { %>
            <form action="UpdateCustomerServlet" method="post" class="edit-form" id="edit-customer-form">
                <input type="hidden" name="userId" value="<%= customer.getUserId() %>">

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="<%= customer.getEmail() %>" required class="input-field">

                <label for="firstName">First Name:</label>
                <input type="text" id="firstName" name="firstName" value="<%= customer.getFirstName() %>" required class="input-field">

                <label for="lastName">Last Name:</label>
                <input type="text" id="lastName" name="lastName" value="<%= customer.getLastName() %>" required class="input-field">

                <label for="phone">Phone:</label>
                <input type="text" id="phone" name="phone" value="<%= customer.getPhone() %>" class="input-field">

                <label for="address">Address:</label>
                <textarea id="address" name="address" class="input-field textarea"><%= customer.getAddress() %></textarea>

                <label for="loyaltyPoints">Loyalty Points:</label>
                <input type="number" id="loyaltyPoints" name="loyaltyPoints" value="<%= customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0 %>" class="input-field">

                <label for="preferredPaymentMethod">Preferred Payment Method:</label>
                <input type="text" id="preferredPaymentMethod" name="preferredPaymentMethod" value="<%= customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "" %>" class="input-field">

                <input type="submit" value="Update" class="edit-button">
                <a href="staffDashboard.jsp" class="view-button">Back to Dashboard</a>
            </form>
            <% } else { %>
            <p class="error-message">Customer not found!</p>
            <a href="staffDashboard.jsp" class="view-button">Back to Dashboard</a>
            <% } %>
        </div>
    </body>
</html>