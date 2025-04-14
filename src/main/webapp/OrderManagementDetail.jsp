<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Order Management Detail</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    </head>
    <body>
        <div class="container mt-4">
            <table class="table table-bordered mt-4">
                <thead class="table-dark">
                <div class="text-center"><h2>Order Management Detail</h2></div>
                <tr>
                    <th colspan="2" class="text-center">Information</th>
                </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><strong>Order ID</strong></td>
                        <td>${order.order_id}</td>
                    </tr>
                    <tr>
                        <td><strong>Customer Name</strong></td>
                        <td>${order.first_name} ${order.last_name}</td>
                    </tr>
                    <tr>
                        <td><strong>Email</strong></td>
                        <td>${order.email}</td>
                    </tr>
                    <tr>
                        <td><strong>Phone</strong></td>
                        <td>${order.phone}</td>
                    </tr>
                    <tr>
                        <td><strong>Product Name</strong></td>
                        <td>
                            <c:forEach items="${order.nameProducts}" var="productName" varStatus="loop">
                                ${productName}<c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </td>
                    </tr>
                    <tr>
                        <td><strong>Quantity</strong></td>
                        <td>${order.quantity}</td>
                    </tr>
                    <tr>
                        <td><strong>Total Amount</strong></td>
                        <td><fmt:formatNumber value="${order.total_amount}" type="number" pattern="#,###.00"/></td>
                    </tr>
                    <tr>
                        <td><strong>Order Date</strong></td>
                        <td>${order.order_date}</td>
                    </tr>
                    <tr>
                        <td><strong>Order Status</strong></td>
                        <td>${order.order_status}</td>
                    </tr>
                    <tr>
                        <td><strong>Shipping Address</strong></td>
                        <td>${order.shipping_address}</td>
                    </tr>
                </tbody>
            </table>
            <a class="btn btn-secondary" href="ListOrderManagement">Back</a>
        </div>
    </body>
</html>