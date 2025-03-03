<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Search Results</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/navbar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/products.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/search.css"> <!-- Đưa xuống cuối -->

    </head>
    <body>
        <%@ include file="navbar.jsp" %>

        <div class="container">
            <c:choose>
                <c:when test="${not empty productList}">
                    <div class="product-grid">
                        <c:forEach var="product" items="${productList}">
                            <div class="product-item">
                                <a href="productDetail?productId=${product.productId}">
                                    <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                                    <h3>${product.name}</h3>
                                    <p class="price">${product.price} VND</p>
                                    <c:if test="${product.discountPrice != null}">
                                        <p class="discount">-${product.discountPrice} VND</p>
                                    </c:if>
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="no-products">No products found matching your search!</p>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>
