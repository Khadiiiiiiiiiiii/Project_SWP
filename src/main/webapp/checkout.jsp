<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.mvc.DAO.ProductDAO, com.mvc.model.Product" %>
<% pageContext.setAttribute("productDAO", new ProductDAO()); %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Shopping Cart</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8f8f8; margin: 0; padding: 0; }
        .cart-container { width: 80%; margin: 30px auto; background-color: white; border-radius: 10px; padding: 20px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }
        .cart-container table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        .cart-container th, .cart-container td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
        .cart-container th { background-color: #f1f1f1; }
        .cart-container tr:hover { background-color: #f9f9f9; }
        .cart-container .price-column { width: 15%; }
        .cart-container .actions-column { width: 20%; }
        input[type="number"] { padding: 5px; width: 50px; margin-right: 10px; }
        input[type="submit"], .cart-container .btn { background-color: #4CAF50; color: white; border: none; padding: 10px 20px; cursor: pointer; border-radius: 5px; }
        input[type="submit"]:hover, .cart-container .btn:hover { background-color: #45a049; }
        .continue-shopping { text-align: center; margin-top: 20px; }
        .continue-shopping input { background-color: #007bff; padding: 10px 20px; color: white; border: none; border-radius: 5px; cursor: pointer; }
        .continue-shopping input:hover { background-color: #0056b3; }
        .total-price { font-size: 18px; font-weight: bold; text-align: right; margin-top: 20px; }
        .discounted-price { font-size: 16px; color: #ff0000; }
        .checkout-btn { display: block; width: 100%; background-color: #28a745; color: white; padding: 12px; border: none; cursor: pointer; font-size: 16px; margin-top: 20px; border-radius: 5px; }
        .checkout-btn:hover { background-color: #218838; }
        .empty-cart-message { text-align: center; font-size: 18px; }
        .discount-section { margin-top: 20px; text-align: right; }
        .discount-section select { padding: 5px; font-size: 16px; margin-right: 10px; }
        .discount-section .expiration-info { font-size: 14px; color: #666; margin-left: 10px; }
        .discount-section .remove-promo { background-color: #ff4444; color: white; border: none; padding: 2px 6px; border-radius: 3px; cursor: pointer; margin-left: 5px; font-size: 14px; }
        .discount-section .remove-promo:hover { background-color: #cc0000; }
        #confirmationDialog { display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background-color: white; padding: 20px; border: 1px solid #ccc; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); z-index: 1000; text-align: center; }
        #confirmationDialog button { margin: 0 10px; padding: 5px 10px; cursor: pointer; }
        #overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 999; }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="cart-container">
    <h2>Your Cart</h2>

    <c:choose>
        <c:when test="${empty cartItems or emptyCart}">
            <p class="empty-cart-message">Your cart is empty or no items fetched. Please add items or check database.</p>
            <form action="${pageContext.request.contextPath}/products.jsp" class="continue-shopping">
                <input type="submit" value="Continue Shopping">
            </form>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Product</th>
                        <th class="price-column">Price</th>
                        <th>Stock</th>
                        <th>Quantity</th>
                        <th class="price-column">Total</th>
                        <th class="actions-column">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${cartItems}">
                        <c:set var="product" value="${productDAO.getProductById(item.productId)}" />
                        <c:if test="${not empty product}">
                            <tr>
                                <td>${product.name}</td>
                                <td class="price-column"><fmt:formatNumber value="${product.price}" type="number" maxFractionDigits="2"/></td>
                                <td>${product.stockQuantity}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/CartServlet" method="post">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="product_id" value="${product.productId}">
    <input type="hidden" name="cart_item_id" value="${item.cartItemId}">
    <input type="number" name="quantity" value="${item.quantity}" min="1" max="${product.stockQuantity}">
    <input type="submit" value="Update">
</form>

                                </td>
                                <td class="price-column"><fmt:formatNumber value="${item.totalPrice}" type="number" maxFractionDigits="2"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/CartServlet" method="post">
    <input type="hidden" name="action" value="remove">
    <input type="hidden" name="cart_item_id" value="${item.cartItemId}">
    <input type="submit" value="Remove" class="btn">
</form>

                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>

            <div class="total-price">Total: <fmt:formatNumber value="${totalPrice != null ? totalPrice : 0}" type="number" maxFractionDigits="2"/></div>

            <form action="${pageContext.request.contextPath}/checkout.jsp">
                <input type="submit" value="Proceed to Checkout" class="checkout-btn">
            </form>
        </c:otherwise>
    </c:choose>

    <form action="${pageContext.request.contextPath}/category?category=all" class="continue-shopping">
        <input type="submit" value="Continue Shopping">
    </form>

    <c:if test="${not empty sessionScope.user}">
        <div class="discount-section">
            <label for="cartPromoCode"></label>
            <span style="margin-right: 5px;">🎟️</span>
            <select id="cartPromoCode" name="cartPromoCode">
                <option value="">Sử dụng mã giảm giá</option>
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
            <p>Giá sau giảm (nếu có): <span id="cartTotalPrice" class="discounted-price">
                <fmt:formatNumber value="${totalPrice != null ? totalPrice : 0}" type="number" maxFractionDigits="2"/>
            </span></p>
        </div>
    </c:if>
</div>

<div id="overlay"></div>
<div id="confirmationDialog">
    <p>Bạn có chắc muốn xóa mã giảm giá không?</p>
    <button onclick="confirmRemove()">Yes</button>
    <button onclick="hideConfirmation()">No</button>
</div>

<script>
    const initialTotalPrice = ${totalPrice != null ? totalPrice : 0};
    let currentPromoCode = "";
    let currentDiscountedTotal = 0;

    function applyPromotion() {
        var select = document.getElementById("cartPromoCode");
        var selectedOption = select.options[select.selectedIndex];
        var code = selectedOption.value;
        var discountPercentage = parseFloat(selectedOption.getAttribute("data-discount")) || 0;
        var expirationDate = selectedOption.getAttribute("data-expiration") || "";
        var cartTotalPrice = document.getElementById("cartTotalPrice");
        var expirationInfo = document.getElementById("expirationInfo");
        var removePromoBtn = document.getElementById("removePromoBtn");

        if (code && discountPercentage > 0) {
            currentPromoCode = code;

            // Gọi API để áp mã giảm giá và lấy giá trị từ server
            fetch('${pageContext.request.contextPath}/CartServlet', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'action=applyCartPromo&promoCode=' + encodeURIComponent(code)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log("Applied promo successfully, discountedTotal: " + data.discountedTotal);
                    currentDiscountedTotal = data.discountedTotal;

                    // Cập nhật giao diện ngay lập tức với giá trị từ server
                    if (cartTotalPrice) {
                        cartTotalPrice.innerHTML = currentDiscountedTotal.toLocaleString('en-US', { maximumFractionDigits: 2 });
                    }
                    expirationInfo.textContent = expirationDate ? "Hết hạn: " + expirationDate : "";
                    removePromoBtn.style.display = "inline";
                } else {
                    alert(data.error || "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
                    resetPrice();
                }
            })
            .catch(error => {
                console.error('Fetch error:', error);
                alert("Lỗi khi áp dụng mã giảm giá.");
                resetPrice();
            });
        } else {
            resetPrice();
        }
    }

    function resetPrice() {
        var cartTotalPrice = document.getElementById("cartTotalPrice");
        var expirationInfo = document.getElementById("expirationInfo");
        var removePromoBtn = document.getElementById("removePromoBtn");

        if (cartTotalPrice) {
            cartTotalPrice.innerHTML = initialTotalPrice.toLocaleString('en-US', { maximumFractionDigits: 2 });
        }
        expirationInfo.textContent = "";
        removePromoBtn.style.display = "none";
        currentPromoCode = "";
        currentDiscountedTotal = 0;
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
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'action=removeCartPromo&promoCode=' + encodeURIComponent(currentPromoCode)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    resetPrice();
                    hideConfirmation();
                } else {
                    alert(data.message || "Lỗi khi xóa mã giảm giá.");
                    hideConfirmation();
                }
            })
            .catch(error => {
                console.error('Error removing promo:', error);
                alert("Có lỗi xảy ra khi xóa mã giảm giá.");
                hideConfirmation();
            });
        } else {
            alert("Không có mã giảm giá để xóa.");
            hideConfirmation();
        }
    }

    window.onload = function() {
        fetch('${pageContext.request.contextPath}/DiscountServlet?action=getAvailablePromotions')
            .then(response => response.json())
            .then(data => {
                const select = document.getElementById("cartPromoCode");
                if (select) {
                    select.innerHTML = '<option value="">Sử dụng mã giảm giá</option>';
                    data.forEach(promotion => {
                        const option = document.createElement("option");
                        option.value = promotion.code;
                        option.setAttribute("data-discount", promotion.discountPercentage);
                        option.setAttribute("data-expiration", promotion.expirationDate);
                        option.text = `${promotion.code} (${promotion.discountPercentage}%)`;
                        select.appendChild(option);
                    });
                }
            })
            .catch(error => console.error('Fetch error:', error));
    };
</script>
</body>
</html>