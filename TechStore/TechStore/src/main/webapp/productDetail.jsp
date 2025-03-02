<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} - Product Detail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/productDetail.css">
</head>
<body>
    <%@ include file="navbar.jsp" %>

    <img src="${pageContext.request.contextPath}/img/banner.jpg" alt="banner" class="banner">

    <div class="container">
        <c:choose>
            <c:when test="${not empty product}">
                <div class="product-detail">
                    <img src="${pageContext.request.contextPath}/${product.imageUrl}" 
                         alt="${product.name}" />
                    <div class="product-info">
                        <h3>${product.name}</h3>
                        <p class="price">
                            <strong>Price:</strong> 
                            <c:choose>
                                <c:when test="${product.discountPrice != null}">
                                    <del>${product.price} VND</del>
                                    <span style="color: #ff0000;"> ${product.price - product.discountPrice} VND</span>
                                    <span class="discount">(-${product.discountPrice} VND)</span>
                                </c:when>
                                <c:otherwise>
                                    ${product.price} VND
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <div class="product-actions">
                            <p><strong>Stock Quantity:</strong> ${product.stockQuantity}</p>
                            <form id="cartForm-${product.productId}" action="${pageContext.request.contextPath}/CartServlet" method="post">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="product_id" value="${product.productId}">
                                <label for="quantity-${product.productId}">Quantity:</label>
                                <input type="number" id="quantity-${product.productId}" name="quantity" value="1" min="1" max="${product.stockQuantity}"
                                       oninput="validateQuantity(this, ${product.stockQuantity})">
                                
                                <button type="button" class="add-cart-btn" onclick="checkLoginAndSubmit('${product.productId}', 'add')">Add to Cart</button>
                                <button type="button" onclick="checkLoginAndSubmit('${product.productId}', 'buyNow')">Buy Now</button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Bảng Product Information -->
                <table>
                    <tr>
                        <th colspan="2">Product Information</th>
                    </tr>
                    <c:forEach var="detail" items="${fn:split(product.description, '|')}">
                        <c:set var="keyValue" value="${fn:split(detail, ':')}" />
                        <tr>
                            <td>${keyValue[0]}</td>
                            <td>${keyValue[1]}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:when>
        </c:choose>
    </div>

    <script>
        function checkLoginAndSubmit(productId, action) {
            var isLoggedIn = ${not empty sessionScope.user};

            if (!isLoggedIn) {
                window.location.href = "login.jsp";
            } else {
                var form = document.getElementById("cartForm-" + productId);
                form.action = "${pageContext.request.contextPath}/CartServlet";
                form.elements["action"].value = action;
                form.submit();
            }
        }

        // Hàm validateQuantity (nếu cần)
        function validateQuantity(input, max) {
            if (input.value < 1) input.value = 1;
            if (input.value > max) input.value = max;
        }
    </script>
</body>
</html>