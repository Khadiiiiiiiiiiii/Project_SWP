<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Order Management</title>
        <link rel="stylesheet" href="CSS/ManageProduct.css">
    </head>
    <body>
        <!-- Thanh bar trên cùng -->
        <header>
            <div style="text-align: center;">
                <nav>
                    <ul>
                        <li onclick="showSection('ViewOrderList')">Order Management</li>
                    </ul>
                </nav>    
            </div>  
            <a class="btn logout" href="admin">Back</a>
        </header>

        <!-- Khu vực quản lý List -->
        </br>
        <section id="ViewOrderList">
            <div class="container mt-4">
                <!-- Kiểm tra nếu danh sách rỗng -->
                <c:if test="${empty orderList}">
                    <p class="text-danger">No products found. Check database connection or data.</p>
                </c:if>

                <!-- Hiển thị bảng -->
                <table border="1">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Customer Name</th>
                            <th>Order Date</th>
                            <th>Total Amount</th>
                            <th>Order Status</th>
                            <th>Shipping Address</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orderList}" var="o">
                            <tr>
                                <td>${o.order_id}</td>
                                <td>${o.first_name} ${o.last_name}</td>
                                <td>${o.order_date}</td>
                                <td> <!-- chuyen so dang khoa hoc thanh so binh thuong-->
                                    <fmt:formatNumber value="${o.total_amount}" type="number" pattern="#,###.00"/>
                                </td> 
                                <td>
                                    <select name="order_status" onchange="updateOrderStatus(${o.order_id}, this.value)">
                                        <option value="processing" ${o.order_status.toLowerCase() == 'processing' ? 'selected' : ''}>Processing</option>
                                        <option value="completed" ${o.order_status.toLowerCase() == 'completed' ? 'selected' : ''}>Completed</option>
                                        <option value="canceled" ${o.order_status.toLowerCase() == 'canceled' ? 'selected' : ''}>Canceled</option>
                                    </select>
                                </td>
                                <td>${o.shipping_address}</td>
                                <td>
                                    <a href="OrderManagementDetailController?order_id=${o.order_id}" class="btn btn-info btn-sm">Detail</a>
                                    <a href="ViewProductInOrderController?order_id=${o.order_id}" class="btn btn-info btn-sm">Product In Order</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>           
            </div>
        </section>
        <script>
            function showSection(sectionId) {
                document.getElementById("ViewOrderList").style.display = "none";
                document.getElementById(sectionId).style.display = "block";
            }

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
    </body>
</html>