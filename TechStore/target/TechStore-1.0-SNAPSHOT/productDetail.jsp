<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} - Product Detail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/products.css">
    <script>
        function validateQuantity(input, maxStock) {
            if (input.value > maxStock) {
                alert("The quantity exceeds available stock!");
                input.value = maxStock;  // set quantity to max stock if input exceeds stock quantity
            } else if (input.value < 1) {
                alert("Quantity cannot be less than 1!");
                input.value = 1;  // set quantity to 1 if less than 1
            }
        }
    </script>
</head>
<body>
    <%@ include file="navbar.jsp" %>

    <div class="container">
        <c:choose>
            <c:when test="${not empty product}">
                <h1>${product.name}</h1>
                <div class="product-detail">
                    <img src="${pageContext.request.contextPath}/${product.imageUrl}" 
                         alt="${product.name}" width="300" height="300"/>
                    <h3>${product.name}</h3>
                    <p class="price">
                        <strong>Price:</strong> 
                        <c:choose>
                            <c:when test="${product.discountPrice != null}">
                                <del>${product.price} VND</del>
                                <span style="color:red;"> ${product.price - product.discountPrice} VND</span>
                                <span class="discount">(-${product.discountPrice} VND)</span>
                            </c:when>
                            <c:otherwise>
                                ${product.price} VND
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <!-- Product Information -->
                    <table border="1">
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

                    <p><strong>Stock Quantity:</strong> ${product.stockQuantity}</p>

                    <!-- Kiểm tra nếu chưa đăng nhập -->
                        <c:if test="${empty sessionScope.user}">
                            <p><a href="login.jsp" class="login-required">Login to add to cart</a></p>
                        </c:if>

                        <!-- Nếu đăng nhập, hiển thị nút Add to Cart -->
                        <c:if test="${not empty sessionScope.user}">
                            <form action="${pageContext.request.contextPath}/CartServlet" method="post">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="product_id" value="${product.productId}">
                                <label for="quantity-${product.productId}">Quantity:</label>
                                <input type="number" id="quantity-${product.productId}" name="quantity" value="1" min="1" max="${product.stockQuantity}"
                                       oninput="validateQuantity(this, ${product.stockQuantity})">
                                <button type="submit" class="add-cart-btn">Add to Cart</button>
                            </form>
                        </c:if>
                      
            </c:when>
        </c:choose>
    </div>
</body>
</html>
