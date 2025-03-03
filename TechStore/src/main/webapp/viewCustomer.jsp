<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.Customer, com.mvc.model.Order, com.mvc.DAO.StaffDAO, com.mvc.dal.DBContext, java.sql.Connection, java.util.List" %>

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
        <title>View Customer Details</title>
        <link rel="stylesheet" href="CSS/viewCustomer.css">
    </head>
    <body>
        <div class="container">
            <h2>Customer Details</h2>
            <% if (customer != null) { %>
            <div class="customer-details">
                <h3>Customer Information</h3>
                <p><strong>ID:</strong> <%= customer.getUserId() %></p>
                <p><strong>Email:</strong> <%= customer.getEmail() %></p>
                <p><strong>First Name:</strong> <%= customer.getFirstName() %></p>
                <p><strong>Last Name:</strong> <%= customer.getLastName() %></p>
                <p><strong>Phone:</strong> <%= customer.getPhone() != null ? customer.getPhone() : "N/A" %></p>
                <p><strong>Address:</strong> <%= customer.getAddress() != null ? customer.getAddress() : "N/A" %></p>
                <p><strong>Loyalty Points:</strong> <%= customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : "0" %></p>
                <p><strong>Preferred Payment Method:</strong> <%= customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "N/A" %></p>
            </div>

            <div class="orders-table">
                <h3>Order History</h3>
                <% List<Order> orders = customer.getOrders(); %>
                <% if (orders != null && !orders.isEmpty()) { %>
                <table border="1">
                    <tr>
                        <th>Order ID</th>
                        <th>Total Amount</th>
                        <th>Order Date</th>
                        <th>Shipping Address</th>
                        <th>Status</th>
                    </tr>
                    <% for (Order order : orders) { %>
                    <tr>
                        <td><%= order.getOrderId() %></td>
                        <td><%= order.getTotalAmount() %></td>
                        <td><%= order.getOrderDate() %></td>
                        <td><%= order.getShippingAddress() != null ? order.getShippingAddress() : "N/A" %></td>
                        <td><%= order.getOrderStatus() %></td>
                    </tr>
                    <% } %>
                </table>
                <% } else { %>
                <p>No orders found for this customer.</p>
                <% } %>
            </div>
            <% } else { %>
            <p>Customer not found!</p>
            <% } %>
            <a href="staffDashboard.jsp" class="view_button">Back to Dashboard</a>
        </div>
    </body>
</html>