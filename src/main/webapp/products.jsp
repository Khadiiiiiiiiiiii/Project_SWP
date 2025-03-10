<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${categoryName}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/products.css">
</head>

<body>
    <%@ include file="navbar.jsp" %>

    <div class="container">
        <aside class="category-sidebar">
            <h3>Categories</h3>
            <ul class="category-list">
                <li><a href="category?category=all">All Products</a></li>
                <li><a href="category?category=1">Laptop</a></li>
                <li><a href="category?category=2">Mouse</a></li>
                <li><a href="category?category=3">Keyboard</a></li>
                <li><a href="category?category=4">Screen</a></li>
                <li><a href="category?category=5">Headphone</a></li>
            </ul>
        </aside>

        <section class="product-section">
            <h2>${categoryName}</h2>


            <c:choose>
                <c:when test="${isAllProducts}">
                    <c:forEach var="entry" items="${productsByCategory}">
                        <div class="category-block">
                            <h2>
                                <c:choose>
                                    <c:when test="${entry.key == 1}">Laptop</c:when>
                                    <c:when test="${entry.key == 2}">Mouse</c:when>
                                    <c:when test="${entry.key == 3}">Keyboard</c:when>
                                    <c:when test="${entry.key == 4}">Screen</c:when>
                                    <c:when test="${entry.key == 5}">Headphone</c:when>
                                    <c:otherwise>Other</c:otherwise>
                                </c:choose>
                            </h2>
                            <div class="product-container">
                                <button class="navigation-btn left-btn"><</button>
                                <div class="product-grid">
                                    <c:forEach var="product" items="${entry.value}">
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
                                <button class="navigation-btn right-btn">></button>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="grid-view">
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
                </c:otherwise>
            </c:choose>

            <c:if test="${empty productList}">
                <p class="not-found">No products found in this category.</p>
            </c:if>
        </section>
    </div>

    <script src="${pageContext.request.contextPath}/carousel.js"></script>
</body>
</html>
