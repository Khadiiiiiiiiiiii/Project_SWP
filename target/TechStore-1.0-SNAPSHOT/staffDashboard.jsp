<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.mvc.model.User" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !("STAFF".equals(user.getRole()) || "Store Manager".equalsIgnoreCase(user.getRole()))) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Kiểm tra nếu orderList chưa được đặt (tức là truy cập trực tiếp vào staffDashboard.jsp)
    if (request.getAttribute("orderList") == null) {
        response.sendRedirect("ListOrderManagement");
        return;
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Order Management</title>
        <link rel="stylesheet" href="CSS/OrderManagement.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">        
    </head>
    <body>
        <div class="sidebar">
            <div class="sidebar-header">
                <h2><%= "Store Manager".equalsIgnoreCase(user.getRole()) ? "Store Manager Dashboard" : "Staff Dashboard" %></h2>
            </div>
            <nav>
                <ul>
                    <% if ("Store Manager".equalsIgnoreCase(user.getRole())) { %>
                    <li>
                        <a href="storeManagerDashboard.jsp" class="<%= "storeManagerDashboard.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-tags"></i> Manage Promotions
                        </a>
                    </li>
                    <li>
                        <a href="ManageProductDiscounts.jsp" class="<%= "ManageProductDiscounts.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-percent"></i> Manage Product Discounts
                        </a>
                    </li>
                    <li>
                        <a href="reviewsManagement" class="<%= request.getRequestURI().contains("reviewsManagement") ? "active" : "" %>">
                            <i class="fas fa-comment-dots"></i> Manage Reviews
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement" class="<%= "ProductManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <% } %>
                    <li>
                        <a href="ListOrderManagement" class="active">
                            <i class="fas fa-shopping-cart"></i> Order Management
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

        <!-- Khu vực quản lý List -->
        <br>
        <div class="main-content">
            <div class="container">
                <section id="ViewOrderList">
                    <div class="container mt-4">
                        <c:if test="${empty orderList}">
                            <p class="text-danger">No orders found. Check database connection or data.</p>
                        </c:if>
                        <h2>Order Management</h2>
                        <table border="1">
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th style="width: 180px;">Customer Name</th>
                                    <th style="width: 180px;">Order Date</th>
                                    <th>Total Amount</th>
                                    <th>Order Status</th>
                                    <th style="width: 150px;">Shipping Address</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${orderList}" var="o">
                                    <tr>
                                        <td>${o.order_id}</td>
                                        <td>${o.first_name} ${o.last_name}</td>
                                        <td>${o.order_date}</td>
                                        <td>
                                            <fmt:formatNumber value="${o.total_amount}" type="number" pattern="#,###.00"/>
                                        </td> 
                                        <td>
                                            <select name="order_status" onchange="updateOrderStatus(${o.order_id}, this.value)">
                                                <option value="Processing" ${o.order_status == 'Processing' ? 'selected' : ''}>Processing</option>
                                                <option value="Pending" ${o.order_status == 'Pending' ? 'selected' : ''}>Pending</option>
                                                <option value="completed" ${o.order_status == 'completed' ? 'selected' : ''}>Completed</option>
                                                <option value="Cancel" ${o.order_status == 'Cancel' ? 'selected' : ''}>Canceled</option>
                                            </select>
                                        </td>
                                        <td>${o.shipping_address}</td>
                                        <td> 
                                            <div class="button_gr">
                                                <a href="OrderManagementDetailController?order_id=${o.order_id}" class="update_button">Detail</a>
                                                <a href="ViewProductInOrderController?order_id=${o.order_id}" class="delete_button">Product In Order</a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>           
                    </div>
                </section>
                <script>
                    function updateOrderStatus(orderId, status) {
                        fetch('ListOrderManagement', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: 'action=updateStatus&order_id=' + encodeURIComponent(orderId) + '&status=' + encodeURIComponent(status),
                            credentials: 'same-origin'
                        })
                                .then(response => {
                                    if (response.ok) {
                                        alert('Order status updated successfully!');
                                        location.reload();
                                    } else if (response.status === 404) {
                                        alert('Order not found. Please check the order ID.');
                                    } else {
                                        alert('Failed to update order status.');
                                    }
                                })
                                .catch(error => {
                                    console.error('Error:', error);
                                    alert('Error updating order status.');
                                });
                    }
                </script>
            </div>
        </div>
    </body>
</html>