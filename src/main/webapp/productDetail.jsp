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



        <div class="container">
            <c:choose>
                <c:when test="${not empty product}">
                    <div class="product-detail">
                        <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}" />
                        <div class="product-info">
                            <h3>${product.name}</h3>
                            <p class="price">
                                <strong>Price:</strong>
                                <c:choose>
                                    <c:when test="${product.discountPrice != null}">
                                        <del style="color: #000; font-size: 14px; font-weight: normal;">${product.formattedPrice}</del><br>
                                        <span id="displayPrice" style="color: #ff0000; font-weight: bold;">${product.formattedDiscountedPrice}</span>
                                        <span class="discount-frame" style="color: white; font-weight: bold;">${product.formattedDiscountPercentage}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span id="displayPrice" style="color: #000;">${product.formattedPrice}</span>
                                    </c:otherwise>
                                </c:choose>
                            </p>

                            <div class="product-actions">
                                <p><strong>Stock Quantity:</strong> ${product.stockQuantity}</p>
                                <form id="cartForm-${product.productId}" action="${pageContext.request.contextPath}/CartServlet" method="post">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="product_id" value="${product.productId}">
                                    <label for="quantity-${product.productId}">Quantity:</label>
                                    <c:if test="${sessionScope.quantityInCart != null}">
                                        <input type="number" id="quantity-${product.productId}" name="quantity" value="0" min="0" max="${sessionScope.quantityInCart}" oninput="validateQuantity(this, ${sessionScope.quantityInCart})">
                                    </c:if>
                                    <c:if test="${sessionScope.quantityInCart == null}">
                                        <input type="number" id="quantity-${product.productId}" name="quantity" value="1" min="1" max="${product.stockQuantity}" oninput="validateQuantity(this, ${product.stockQuantity})">
                                    </c:if>
                                    <button type="button" class="add-cart-btn" onclick="checkLoginAndSubmit('${product.productId}', 'add')">Add to Cart</button>
                                </form> 
                            </div>
                        </div>
                    </div>

                    <table>
                        <tr><th colspan="2">Product Information</th></tr>
                                <c:forEach var="detail" items="${fn:split(product.description, '|')}">
                                    <c:set var="keyValue" value="${fn:split(detail, ':')}" />
                            <tr><td>${keyValue[0]}</td><td>${keyValue[1]}</td></tr>
                        </c:forEach>
                    </table>
                    <%@ include file="productReviews.jsp" %>
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

            function validateQuantity(input, max) {
                if (input.value < 1)
                    input.value = 0;
                if (input.value > max)
                    input.value = max;
            }
        </script>
    </body>
</html>
