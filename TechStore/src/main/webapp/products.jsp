<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.DAO.ProductDAO, com.mvc.model.Product, java.util.List" %>

<%
    if (request.getAttribute("productList") == null) {
        ProductDAO productDAO = new ProductDAO();
        List<Product> productList = productDAO.getAllProducts();
        request.setAttribute("productList", productList);
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tech Store - Products</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/products.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script defer src="${pageContext.request.contextPath}/JS/products.js"></script>
</head>
<body>

    <%@ include file="navbar.jsp" %>
    <div class="banner-container">
        <img src="img/banner.jpg" alt="banner">
    </div>
    <div class="container">
        <!-- Sidebar Categories -->
        <aside class="category-sidebar">
            <h3>Categories</h3>
            <ul class="category-list">
                <li><a href="#">Laptop</a></li>
                <li><a href="#">Mouse</a></li>
                <li><a href="#">Keyboard</a></li>
                <li><a href="#">Screen</a></li>
                <li><a href="#">Headphone</a></li>
            </ul>
        </aside>

        <main class="product-section">
            <!-- Dòng sản phẩm Laptop -->
            <section class="product-row">
                <h2>Laptop</h2>
                <div class="product-carousel-container">
                    <button class="carousel-btn left-btn"><i class="fas fa-chevron-left"></i></button>
                    <div class="product-carousel">
                        <c:forEach var="product" items="${productList}" varStatus="loop">
                            <c:if test="${loop.index < 4}">
                                <div class="product-item">
                                    <!-- Link đến trang chi tiết sản phẩm -->
                                    <a href="productDetail.jsp?productId=${product.productId}">
                                        <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                                        <h3>${product.name}</h3>
                                        <p class="price">${product.price} VND</p>
                                        <c:if test="${not empty product.discountPrice}">
                                            <p class="discount">-${product.discountPrice}%</p>
                                        </c:if>
                                    </a>
                                    
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                    <button class="carousel-btn right-btn"><i class="fas fa-chevron-right"></i></button>
                </div>
            </section>

            <!-- Dòng sản phẩm Mouse -->
            <section class="product-row">
                <h2>Mouse</h2>
                <div class="product-carousel-container">
                    <button class="carousel-btn left-btn"><i class="fas fa-chevron-left"></i></button>
                    <div class="product-carousel">
                        <c:forEach var="product" items="${productList}" varStatus="loop">
                            <c:if test="${loop.index >= 4}">
                                <div class="product-item">
                                    <!-- Link đến trang chi tiết sản phẩm -->
                                    <a href="productDetail.jsp?productId=${product.productId}">
                                        <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                                        <h3>${product.name}</h3>
                                        <p class="price">${product.price} VND</p>
                                        <c:if test="${not empty product.discountPrice}">
                                            <p class="discount">-${product.discountPrice}%</p>
                                        </c:if>
                                    </a>
                                   
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                    <button class="carousel-btn right-btn"><i class="fas fa-chevron-right"></i></button>
                </div>
            </section>
        </main>
    </div>

</body>
</html>
