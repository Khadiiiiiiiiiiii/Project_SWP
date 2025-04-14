<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
                <h2>Admin Dashboard</h2>
            </div>
            <nav>
                <ul>
                    <li>
                        <a href="admin" class="<%= "admin".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-users"></i> Staff Management
                        </a>
                    </li>
                    <li>
                        <a href="RevenueReport.jsp" class="<%= "RevenueReport.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-chart-line"></i> View Revenue Report
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement" class="<%= "ProductManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="CustomerManagement.jsp" class="<%= "CustomerManagement.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-user-friends"></i> Customer Management
                        </a>
                    </li>
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
        </br>
        <div class="main-content">
            <div class="container">
                <section id="ViewOrderList">
                    <div class="container mt-4">
                        <!-- Kiểm tra nếu danh sách rỗng -->
                        <c:if test="${empty orderList}">
                            <p class="text-danger">No products found. Check database connection or data.</p>
                        </c:if>
                        <h2>Order Management</h2>
                        <!-- Hiển thị bảng -->
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