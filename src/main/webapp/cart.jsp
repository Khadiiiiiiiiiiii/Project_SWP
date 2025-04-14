<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
            <h2>Your Cart</h2>

            <c:choose>
                <c:when test="${empty cartItems or emptyCart}">
                    <p class="empty-cart-message">Your cart is empty or no items fetched. Please add items or check database.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th></th>
                                <th>Product</th>
                                <th class="price-column">Price</th>
                                <th>Stock</th>
                                <th>Quantity</th>
                                <th class="price-column">Discounted(%)</th>
                                <th class="price-column">Total</th>
                                <th class="actions-column">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${cartItems}">
                                <c:set var="product" value="${productDAO.getProductById(item.productId)}" />
                                <c:if test="${not empty product}">
                                    <tr>
                                        <td><img style="width: 100px" src="./${product.imageUrl}"></td>
                                        <td>${product.name} - ${item.cartItemId}</td>
                                        <td class="price-column"><fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="2"/></td>
                                        <td>${product.stockQuantity}</td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/CartServlet" method="post">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="product_id" value="${product.productId}">
                                                <input type="hidden" name="cart_item_id" value="${item.cartItemId}">
                                                <div style="display: flex">
                                                    <input type="number" name="quantity" value="${item.quantity}" min="1" max="${product.stockQuantity}">
                                                    <input type="submit" value="Update">
                                                </div>
                                            </form>
                                        </td>
                                        <td class="price-column">
                                            <fmt:formatNumber value="${product.discountPrice}" type="number" maxFractionDigits="2"/>
                                            <c:if test="${not empty product.discountPrice}">%</c:if>

                                            </td>
                                            <td class="price-column"><fmt:formatNumber value="${item.totalPrice}" type="number" maxFractionDigits="2"/></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/CartServlet" method="post">
                                                <input type="hidden" name="action" value="removeCartItem">
                                                <input type="hidden" name="cart_item_id" value="${item.cartItemId}">
                                                <input type="submit" value="Remove" class="btn">
                                            </form>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="total-price">
                        Total: <span id="cartTotalPrice"><fmt:formatNumber value="${totalPrice != null ? totalPrice : 0}" type="number" maxFractionDigits="2"/></span>
                        <span id="promoDiscount" class="promo-discount"></span>
                    </div>

                    <form action="${pageContext.request.contextPath}/vnpaycontroller" method="POST">
                        <input type="hidden" id="totalPrice" name="totalPrice" value="${totalPrice}">
                        <input type="hidden" id="selectedPromoCode" name="cartPromoCode" value="">
                        <input type="submit" value="Proceed to Checkout" class="checkout-btn">
                    </form>
                    <form action="${pageContext.request.contextPath}/paymentCodServlet" method="GET"  onsubmit="return showCODPopup(event)">
                        <input type="hidden" id="totalPriceCOD" name="totalPrice" value="${totalPrice}">
                        <input type="hidden" id="selectedPromoCodeCOD" name="cartPromoCode" value="">
                        <input type="submit" value="Payment COD" class="checkout-btn" style="background-color: #ffc107; color: black;">
                    </form>
                    <div id="confirmationPopup" style="display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background-color: white; padding: 20px; border: 1px solid #ccc; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); z-index: 1000; text-align: center;">
                        <h3>Are you sure you want to proceed with the COD payment?</h3>
<!--                        <p id="totalAmountText">Total Amount: <span id="totalAmountPopup"></span> VND</p>-->
                        <button onclick="confirmCODPayment()" style="padding: 10px 20px; background-color: green; color: white; border: none; border-radius: 5px; cursor: pointer;">Yes, Proceed</button>
                        <button onclick="closePopup()" style="padding: 10px 20px; background-color: red; color: white; border: none; border-radius: 5px; cursor: pointer;">Cancel</button>
                    </div>

                    <!-- Đảm bảo có overlay để che khuất nền khi popup hiện lên -->
                    <div id="overlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 999;"></div>
                    <script>
                        // Hàm hiển thị popup xác nhận khi ấn nút Payment COD
                        function showCODPopup(event) {
                            event.preventDefault();  // Ngừng việc submit form ngay lập tức

                            // Lấy số tiền từ hidden field và hiển thị trong popup
//                            var totalPrice = document.getElementById("totalPriceCOD").value;
//                            document.getElementById("totalAmountPopup").innerText = totalPrice;

                            // Hiển thị popup và overlay
                            document.getElementById("confirmationPopup").style.display = "block";
                            document.getElementById("overlay").style.display = "block";
                        }

                        // Hàm xác nhận thanh toán COD
                        function confirmCODPayment() {
                            // Submit form thanh toán COD
                            document.querySelector("form[onsubmit='return showCODPopup(event)']").submit();
                        }

                        // Hàm đóng popup nếu người dùng nhấn Cancel
                        function closePopup() {
                            document.getElementById("confirmationPopup").style.display = "none";
                            document.getElementById("overlay").style.display = "none";
                        }
                    </script>
                </c:otherwise>
            </c:choose>

            <form action="${pageContext.request.contextPath}/category?category=all" class="continue-shopping">
                <input type="submit" value="Continue Shopping">
            </form>

            <c:if test="${not empty sessionScope.user}">
                <div class="discount-section">
                    <label for="cartPromoCode"></label>
                    <span style="margin-right: 5px;">🎟️ Available Promotions:</span>
                    <select id="cartPromoCode" name="cartPromoCode">
                        <option value="">Select promotion code</option>
                        <c:forEach var="promotion" items="${availablePromotions}">
                            <option value="${promotion.code}" data-discount="${promotion.discountPercentage}"
                                    data-expiration="${promotion.expirationDate}">
                                ${promotion.code} (${promotion.discountPercentage}%)
                            </option>
                        </c:forEach>
                    </select>
                    <button id="applyPromoBtn" onclick="applyPromotion()">Apply</button>
                    <span id="expirationInfo" class="expiration-info"></span>
                    <button id="removePromoBtn" class="remove-promo" style="display: none;" onclick="showConfirmation()">X</button>
                </div>
            </c:if>
        </div>

        <div id="overlay"></div>
        <div id="confirmationDialog">
            <p>Are you sure you want to delete the promotion code?</p>
            <button onclick="confirmRemove()">Yes</button>
            <button onclick="hideConfirmation()">No</button>
        </div>

        <script>
            document.getElementById("cartPromoCode").addEventListener("change", function () {
                const selectedValue = this.value;
                document.getElementById("selectedPromoCode").value = selectedValue;
                const codInput = document.getElementById("selectedPromoCodeCOD");
                if (codInput) {
                    codInput.value = selectedValue;
                }
            });

            const initialTotalPrice = ${totalPrice != null ? totalPrice : 0}; // Giá sau giảm giá trực tiếp
            let currentPromoCode = "";
            let currentDiscountPercentage = 0;
            let currentDiscountedTotal = initialTotalPrice;

            function applyPromotion() {
                var select = document.getElementById("cartPromoCode");
                var selectedOption = select.options[select.selectedIndex];
                var code = selectedOption.value;
                var discountPercentage = parseFloat(selectedOption.getAttribute("data-discount")) || 0;
                var expirationDate = selectedOption.getAttribute("data-expiration") || "";
                var cartTotalPrice = document.getElementById("cartTotalPrice");
                var promoDiscount = document.getElementById("promoDiscount");
                var expirationInfo = document.getElementById("expirationInfo");
                var removePromoBtn = document.getElementById("removePromoBtn");

                if (code && discountPercentage > 0) {
                    currentPromoCode = code;
                    currentDiscountPercentage = discountPercentage;

                    // Gọi API để áp mã giảm giá và lấy giá trị từ server
                    fetch('${pageContext.request.contextPath}/CartServlet', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        body: 'action=applyCartPromo&promoCode=' + encodeURIComponent(code)
                    })
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    console.log("Applied promo successfully, discountedTotal: " + data.discountedTotal);
                                    currentDiscountedTotal = data.discountedTotal;

                                    // Cập nhật giao diện với giá trị từ server
                                    if (cartTotalPrice) {
                                        cartTotalPrice.innerHTML = initialTotalPrice.toLocaleString('en-US', {maximumFractionDigits: 2});
                                    }

                                    // Hiển thị số tiền sau khi áp mã giảm giá
                                    var promoDiscountValue = initialTotalPrice - currentDiscountedTotal;
                                    if (promoDiscount) {
                                        promoDiscount.innerHTML = promoDiscountValue > 0 ? "(Giá sau khi áp mã: " + currentDiscountedTotal.toLocaleString('en-US', {maximumFractionDigits: 2}) + " VND)" : "";
                                    }

                                    // Hiển thị ngày hết hạn và nút xóa
                                    expirationInfo.textContent = expirationDate ? "Hết hạn: " + expirationDate : "";
                                    removePromoBtn.style.display = "inline";

                                    // Giữ mã đã áp dụng trong dropdown
                                    for (var i = 0; i < select.options.length; i++) {
                                        if (select.options[i].value === code) {
                                            select.selectedIndex = i;
                                            break;
                                        }
                                    }
                                } else {
                                    alert(data.error || "Promotion code is invalid or expired.");
                                    resetPrice();
                                }
                            })
                            .catch(error => {
                                console.error('Fetch error:', error);
                                alert("Error applying discount code.");
                                resetPrice();
                            });
                } else {
                    resetPrice();
                }
            }

            function resetPrice() {
                var cartTotalPrice = document.getElementById("cartTotalPrice");
                var promoDiscount = document.getElementById("promoDiscount");
                var expirationInfo = document.getElementById("expirationInfo");
                var removePromoBtn = document.getElementById("removePromoBtn");
                var select = document.getElementById("cartPromoCode");

                if (cartTotalPrice) {
                    cartTotalPrice.innerHTML = initialTotalPrice.toLocaleString('en-US', {maximumFractionDigits: 2});
                }
                if (promoDiscount) {
                    promoDiscount.innerHTML = "";
                }
                expirationInfo.textContent = "";
                removePromoBtn.style.display = "none";
                select.selectedIndex = 0; // Reset dropdown về giá trị mặc định
                currentPromoCode = "";
                currentDiscountPercentage = 0;
                currentDiscountedTotal = initialTotalPrice;

                // Xóa mã đã áp dụng trong session
                fetch('${pageContext.request.contextPath}/CartServlet', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: 'action=removeCartPromo'
                });
            }

            function showConfirmation() {
                document.getElementById("overlay").style.display = "block";
                document.getElementById("confirmationDialog").style.display = "block";
            }

            function hideConfirmation() {
                document.getElementById("overlay").style.display = "none";
                document.getElementById("confirmationDialog").style.display = "none";
            }

            function confirmRemove() {
                if (currentPromoCode) {
                    fetch('${pageContext.request.contextPath}/CartServlet', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        body: 'action=removeCartPromo&promoCode=' + encodeURIComponent(currentPromoCode)
                    })
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    resetPrice();
                                    hideConfirmation();
                                } else {
                                    alert(data.message || "Error deleting promotion code.");
                                    hideConfirmation();
                                }
                            })
                            .catch(error => {
                                console.error('Error removing promo:', error);
                                alert("An error occurred while deleting the promotion code.");
                                hideConfirmation();
                            });
                } else {
                    alert("There are no promotion codes to delete.");
                    hideConfirmation();
                }
            }

            window.onload = function () {
                const select = document.getElementById("cartPromoCode");
                if (select) {
                    // Đặt dropdown về trạng thái mặc định
                    select.selectedIndex = 0;

                    // Đặt lại giao diện
                    var cartTotalPrice = document.getElementById("cartTotalPrice");
                    var promoDiscount = document.getElementById("promoDiscount");
                    var expirationInfo = document.getElementById("expirationInfo");
                    var removePromoBtn = document.getElementById("removePromoBtn");

                    if (cartTotalPrice) {
                        cartTotalPrice.innerHTML = initialTotalPrice.toLocaleString('en-US', {maximumFractionDigits: 2});
                    }
                    if (promoDiscount) {
                        promoDiscount.innerHTML = "";
                    }
                    expirationInfo.textContent = "";
                    removePromoBtn.style.display = "none";
                }
            };
        </script>
    </body>
</html>