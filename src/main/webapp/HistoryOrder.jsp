<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.mvc.DAO.ProductDAO, com.mvc.model.Product, com.mvc.DAO.PromotionDAO, com.mvc.model.Promotion, java.util.List" %>
<% 
    pageContext.setAttribute("productDAO", new ProductDAO());
    PromotionDAO promotionDAO = new PromotionDAO();
    List<Promotion> availablePromotions = promotionDAO.getActivePromotions();
    pageContext.setAttribute("availablePromotions", availablePromotions);
    System.out.println("Available promotions in cart.jsp (server-side): " + availablePromotions.size());
    for (Promotion promo : availablePromotions) {
        System.out.println("Promotion (server-side): " + promo.getCode() + " (" + promo.getDiscountPercentage() + "%), Expiration: " + promo.getExpirationDate() + ", Status: " + promo.getStatus());
    }
%>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Shopping Cart</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f8f8f8;
                margin: 0;
                padding: 0;
            }
            .cart-container {
                width: 80%;
                margin: 30px auto;
                background-color: white;
                border-radius: 10px;
                padding: 20px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
            .cart-container table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 20px;
            }
            .cart-container th, .cart-container td {
                padding: 10px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }
            .cart-container th {
                background-color: #f1f1f1;
            }
            .cart-container tr:hover {
                background-color: #f9f9f9;
            }
            .cart-container .price-column {
                width: 15%;
            }
            .cart-container .actions-column {
                width: 20%;
            }
            input[type="number"] {
                padding: 5px;
                width: 50px;
                margin-right: 10px;
            }
            input[type="submit"], .cart-container .btn {
                background-color: #4CAF50;
                color: white;
                border: none;
                padding: 10px 20px;
                cursor: pointer;
                border-radius: 5px;
            }
            input[type="submit"]:hover, .cart-container .btn:hover {
                background-color: #45a049;
            }
            .continue-shopping {
                text-align: center;
                margin-top: 20px;
            }
            .continue-shopping input {
                background-color: #007bff;
                padding: 10px 20px;
                color: white;
                border: none;
                border-radius: 5px;
                cursor: pointer;
            }
            .continue-shopping input:hover {
                background-color: #0056b3;
            }
            .total-price {
                font-size: 18px;
                font-weight: bold;
                text-align: right;
                margin-top: 20px;
            }
            .discount-details {
                font-size: 16px;
                color: #28a745;
                margin-left: 10px;
            }
            .promo-discount {
                font-size: 16px;
                color: #ff8c00;
                margin-left: 10px;
            }
            .checkout-btn {
                display: block;
                width: 100%;
                background-color: #28a745;
                color: white;
                padding: 12px;
                border: none;
                cursor: pointer;
                font-size: 16px;
                margin-top: 20px;
                border-radius: 5px;
            }
            .checkout-btn:hover {
                background-color: #218838;
            }
            .empty-cart-message {
                text-align: center;
                font-size: 18px;
            }
            .discount-section {
                margin-top: 20px;
                text-align: right;
            }
            .discount-section select {
                padding: 5px;
                font-size: 16px;
                margin-right: 10px;
            }
            .discount-section .expiration-info {
                font-size: 14px;
                color: #666;
                margin-left: 10px;
            }
            .discount-section .remove-promo {
                background-color: #ff4444;
                color: white;
                border: none;
                padding: 2px 6px;
                border-radius: 3px;
                cursor: pointer;
                margin-left: 5px;
                font-size: 14px;
            }
            .discount-section .remove-promo:hover {
                background-color: #cc0000;
            }
            #confirmationDialog {
                display: none;
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                background-color: white;
                padding: 20px;
                border: 1px solid #ccc;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                z-index: 1000;
                text-align: center;
            }
            #confirmationDialog button {
                margin: 0 10px;
                padding: 5px 10px;
                cursor: pointer;
            }
            #overlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                z-index: 999;
            }
            #applyPromoBtn {
                background-color: red;
                color: white;
                font-size: 11px;
                padding: 10px 20px;
                border: none;
                cursor: pointer;
                border-radius: 5px; /* Bo góc nhẹ */
                font-weight: bold;
            }
            #applyPromoBtn:hover {
                background-color: darkred; /* Hiệu ứng hover */
            }
        </style>
    </head>
    <body>
        <%@ include file="navbar.jsp" %>

        <div class="cart-container">
            <h2>History Order</h2>
            <table>
                <thead>
                    <tr>
                        <th>Order</th>
                        <th class="price-column">Total Amount</th>
                        <th>Order Date</th>
                        <th>Shipping Address</th>
                        <th class="price-column">Order Status</th>
                        <th class="price-column">Product</th>
                        <th class="actions-column">Image</th>
                        <th class="price-column">Quantity</th>
                        <th>Price</th>
                        <th>Promotion</th> <!-- Thêm cột mới để hiển thị thông tin mã giảm giá -->
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="group" items="${sessionScope.groupedOrders}">
                        <c:set var="firstOrder" value="${group.value[0]}"/>
                        <tr>
                            <td rowspan="${fn:length(group.value)}">${firstOrder.orderId}</td>
                            <td rowspan="${fn:length(group.value)}" class="price-column">
                                <fmt:formatNumber value="${firstOrder.totalAmount}" type="number" maxFractionDigits="2"/>
                            </td>
                            <td rowspan="${fn:length(group.value)}">${firstOrder.orderDate}</td>
                            <td rowspan="${fn:length(group.value)}">${firstOrder.shippingAddress}</td>
                            <td rowspan="${fn:length(group.value)}">
                                ${firstOrder.orderStatus}
                                <c:if test="${firstOrder.orderStatus == 'processing' || firstOrder.orderStatus == 'Processing'}">
                                    <!-- Button cancel order -->
                                    <button style="background-color: #ff4444; color: white; padding: 8px 16px; border: none; cursor: pointer; border-radius: 5px; font-size: 14px;" onclick="showCancelPopup(${firstOrder.orderId})">Cancel</button>
                                </c:if>
                            </td>
                            <!-- Dòng đầu tiên hiển thị sản phẩm -->
                            <td>${group.value[0].name}</td>
                            <td><img style="width: 100px" src="./${group.value[0].image}"></td>
                            <td>${group.value[0].quantity}</td>
                            <td class="price-column">
                                <fmt:formatNumber value="${group.value[0].pricerByQuantity}" type="number" maxFractionDigits="2"/>
                            </td>
                            <td rowspan="${fn:length(group.value)}">
                                <c:choose>
                                    <c:when test="${firstOrder.promoCode != 'No promotion applied'}">
                                        ${firstOrder.promoCode} (${firstOrder.discountPercentage}% off, saved <fmt:formatNumber value="${firstOrder.discountAmount}" type="number" maxFractionDigits="2"/>)
                                    </c:when>
                                    <c:otherwise>
                                        No promotion applied
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <!-- Hiển thị các dòng tiếp theo của cùng một orderId -->
                        <c:forEach var="ho" items="${group.value}" begin="1">
                            <tr>
                                <td>${ho.name}</td>
                                <td><img style="width: 100px" src="./${ho.image}"></td>
                                <td>${ho.quantity}</td>
                                <td class="price-column">
                                    <fmt:formatNumber value="${ho.pricerByQuantity}" type="number" maxFractionDigits="2"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Popup confirm cancel order -->
        <div id="confirmationDialog" style="display: none;">
            <h3>Are you sure you want to cancel this order?</h3>
            <button onclick="cancelOrder()">Yes</button>
            <button onclick="closePopup()">No</button>
        </div>
        <div id="overlay" style="display: none;" onclick="closePopup()"></div>

        <script>
            let orderIdToCancel = null;

            // Show popup
            function showCancelPopup(orderId) {
                orderIdToCancel = orderId;
                document.getElementById("confirmationDialog").style.display = "block";
                document.getElementById("overlay").style.display = "block";
            }

            // Close popup
            function closePopup() {
                document.getElementById("confirmationDialog").style.display = "none";
                document.getElementById("overlay").style.display = "none";
            }

            // Handle cancel order
            function cancelOrder() {
                // Redirect to historyOrder servlet with POST method
                const form = document.createElement("form");
                form.method = "GET";
                form.action = "cancelOrder"; // Replace with correct servlet URL

                const orderIdField = document.createElement("input");
                orderIdField.type = "hidden";
                orderIdField.name = "orderId";
                orderIdField.value = orderIdToCancel;

                form.appendChild(orderIdField);
                document.body.appendChild(form);
                form.submit(); // Submit the form
            }
        </script>
    </body>
</html>