<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.Customer, com.mvc.model.Orderr, com.mvc.DAO.CustomerDAO, com.mvc.dal.DBContext, java.sql.Connection, java.util.List" %>

<%
    String userIdStr = request.getParameter("userId");
    Customer customer = null;
    if (userIdStr != null) {
        try {
            int userId = Integer.parseInt(userIdStr);
            Connection connection = DBContext.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);
            customer = customerDAO.getCustomerByID(userId);
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
        <link rel="stylesheet" href="CSS/viewCus.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    </head>
    <body>
        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Admin Dashboard</h2>
            </div>
            <nav>
                <ul>
                    <li>
                        <a href="admin-dashboard.jsp" class="<%= "admin-dashboard.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-users"></i> Staff Management
                        </a>
                    </li>
                    <li>
                        <a href="RevenueReport.jsp" class="<%= "RevenueReport.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-chart-line"></i> View Revenue Report
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement.jsp" class="<%= "ProductManagement.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="CustomerManagement.jsp" class="active">
                            <i class="fas fa-user-friends"></i> Customer Management
                        </a>
                    </li>
                    <li>
                        <a href="logout" class="logout">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="main-content">
            <div class="container">
                <h2>Customer Details</h2>
                <% if (customer != null) { %>
                <div class="customer-details">
                    <h3>Customer Information</h3>
                    <div class="info-grid">
                        <p><strong>ID:</strong> <%= customer.getUserId() %></p>
                        <p><strong>Email:</strong> <%= customer.getEmail() %></p>
                        <p><strong>First Name:</strong> <%= customer.getFirstName() %></p>
                        <p><strong>Last Name:</strong> <%= customer.getLastName() %></p>
                        <p><strong>Phone:</strong> <%= customer.getPhone() != null ? customer.getPhone() : "N/A" %></p>
                        <p><strong>Address:</strong> <%= customer.getAddress() != null ? customer.getAddress() : "N/A" %></p>
                        <p><strong>Loyalty Points:</strong> <%= customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : "0" %></p>
                        <p><strong>Preferred Payment Method:</strong> <%= customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "N/A" %></p>
                        <p><strong>Status:</strong> <%= customer.getStatus() %></p>
                    </div>
                </div>

                <div class="orders-table">
                    <h3>Order History</h3>
                    <% List<Orderr> orders = customer.getOrders(); %>
                    <% if (orders != null && !orders.isEmpty()) { %>
                    <div class="table-wrapper">
                        <table border="1">
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th>Total Amount</th>
                                    <th>Order Date</th>
                                    <th>Shipping Address</th>
                                    <th>Order Status</th>
                                    <th>Payment ID</th>
                                    <th>Payment Date</th>
                                    <th>Payment Amount</th>
                                    <th>Payment Status</th>
                                    <th>Promotion ID</th>
                                    <th>Promotion Code</th>
                                    <th>Discount (%)</th>
                                    <th>Expiration Date</th>
                                    <th>Promotion Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Orderr order : orders) { %>
                                <tr>
                                    <td><%= order.getOrderId() %></td>
                                    <td><%= order.getTotalAmount() %></td>
                                    <td><%= order.getOrderDate() %></td>
                                    <td><%= order.getShippingAddress() != null ? order.getShippingAddress() : "N/A" %></td>
                                    <td><%= order.getOrderStatus() %></td>
                                    <td><%= order.getPaymentId() %></td>
                                    <td><%= order.getPaymentDate() != null ? order.getPaymentDate() : "N/A" %></td>
                                    <td><%= order.getPaymentAmount() != null ? order.getPaymentAmount() : "N/A" %></td>
                                    <td><%= order.getPaymentStatus() != null ? order.getPaymentStatus() : "N/A" %></td>
                                    <td><%= order.getPromotionId() %></td>
                                    <td><%= order.getPromotionCode() != null ? order.getPromotionCode() : "N/A" %></td>
                                    <td><%= order.getDiscountPercentage() != null ? order.getDiscountPercentage() : "N/A" %></td>
                                    <td><%= order.getExpirationDate() != null ? order.getExpirationDate() : "N/A" %></td>
                                    <td><%= order.getPromotionStatus() != null ? order.getPromotionStatus() : "N/A" %></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } else { %>
                    <p class="no-orders">No orders found for this customer.</p>
                    <% } %>
                </div>
                <% } else { %>
                <p class="error-message">Customer not found!</p>
                <% } %>
                <a href="CustomerManagement.jsp" class="back-btn">Back to Dashboard</a>
            </div>
        </div>
    </body>
</html>