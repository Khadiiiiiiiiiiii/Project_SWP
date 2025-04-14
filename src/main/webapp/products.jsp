<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.mvc.model.Product, com.mvc.DAO.PromotionDAO, com.mvc.model.Promotion, java.math.BigDecimal" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${categoryName}</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/navbar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/products.css">
        <!-- Bootstrap JS and Popper.js -->
        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.min.js"></script>
    </head>

    <body>
        <%@ include file="navbar.jsp" %>
        <!-- Banner Section with Carousel and Side Banners -->
        <div class="banner-section">
            <div class="container d-flex gap-3">
                <!-- Carousel -->
                <div id="carouselExample" class="carousel slide flex-grow-1" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        <div class="carousel-item active">
                            <img src="${pageContext.request.contextPath}/img/banner11.webp" class="d-block" alt="Banner 1">
                        </div>
                        <div class="carousel-item">
                            <img src="${pageContext.request.contextPath}/img/banner12.webp" class="d-block" alt="Banner 2">
                        </div>
                        <div class="carousel-item">
                            <img src="${pageContext.request.contextPath}/img/banner13.webp" class="d-block" alt="Banner 3">
                        </div>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#carouselExample" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#carouselExample" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
                <!-- Side Banners -->
                <div class="side-banners">
                    <div class="side-banner">
                        <img src="${pageContext.request.contextPath}/img/side-banner1.jpg" alt="Side Banner 1">
                    </div>
                    <div class="side-banner">
                        <img src="${pageContext.request.contextPath}/img/side-banner2.avif" alt="Side Banner 2">
                    </div>
                </div>
            </div>
            <!-- Bottom Banners -->
            <div class="container bottom-banners mt-3">
                <div class="bottom-banner">
                    <img src="${pageContext.request.contextPath}/img/bottom-banner1.webp" alt="Bottom Banner 1">
                </div>
                <div class="bottom-banner">
                    <img src="${pageContext.request.contextPath}/img/bottom-banner2.webp" alt="Bottom Banner 2">
                </div>
                <div class="bottom-banner">
                    <img src="${pageContext.request.contextPath}/img/bottom-banner3.webp" alt="Bottom Banner 3">
                </div>
                <div class="bottom-banner">
                    <img src="${pageContext.request.contextPath}/img/bottom-banner4.webp" alt="Bottom Banner 4">
                </div>
            </div>
        </div>

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
                    <li><a href="discount-products">Discount Product</a></li>
                </ul>
            </aside>

            <section class="product-section">
                <h2>${categoryName}</h2>

                <!-- Filter Button and Form -->
                <button class="filter-btn" id="filterToggleBtn"><i class="fa-solid fa-filter"></i> Filter</button>
                <div id="filterForm">
                    <form action="${pageContext.request.contextPath}/category" method="get">
                        <input type="hidden" name="category" value="${param.category != null ? param.category : 'all'}">
                        <label for="minPrice">Min Price:</label>
                        <input type="number" id="minPrice" name="minPrice" placeholder="0" value="${param.minPrice}" min="0">
                        <label for="maxPrice">Max Price:</label>
                        <input type="number" id="maxPrice" name="maxPrice" placeholder="50000000" value="${param.maxPrice}" min="0">
                        <button type="submit">Apply Filter</button>
                    </form>
                </div>

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
                                <div class="product-container position-relative">
                                    <button class="navigation-btn left-btn"><</button>
                                    <div class="product-grid">
                                        <c:forEach var="product" items="${entry.value}">
                                            <div class="product-item">
                                                <a href="productDetail?productId=${product.productId}">
                                                    <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                                                    <h3>${product.name}</h3>
                                                    <p class="price">
                                                        <c:choose>
                                                            <c:when test="${product.discountPrice != null}">
                                                                <del>${product.formattedPrice}</del><br>
                                                                <span>${product.formattedDiscountedPrice}</span>
                                                                <span class="discount-frame">${product.formattedDiscountPercentage}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span>${product.formattedPrice}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </p>
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
                                        <p class="price">
                                            <c:choose>
                                                <c:when test="${product.discountPrice != null}">
                                                    <del>${product.formattedPrice}</del><br>
                                                    <span>${product.formattedDiscountedPrice}</span>
                                                    <span class="discount-frame">${product.formattedDiscountPercentage}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span>${product.formattedPrice}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </a>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

                <%-- Sửa điều kiện hiển thị thông báo "No products found" --%>
                <c:if test="${(isAllProducts && empty productsByCategory) || (!isAllProducts && empty productList)}">
                    <p class="not-found">No products found in this category.</p>
                </c:if>
            </section>
        </div>

        <!-- Footer Banner -->
        <div class="container footer-banner">
            <img src="${pageContext.request.contextPath}/img/footer-banner.webp" alt="Footer Advertisement">
        </div>

        <script src="${pageContext.request.contextPath}/carousel.js"></script>
        <script>
            document.getElementById("filterToggleBtn").addEventListener("click", function () {
                const filterForm = document.getElementById("filterForm");
                const btnRect = this.getBoundingClientRect();
                const scrollY = window.scrollY || window.pageYOffset;
                filterForm.style.top = (btnRect.bottom + scrollY) + "px";
                filterForm.style.left = btnRect.left + window.scrollX + "px";
                filterForm.classList.toggle("show");
            });

            document.querySelectorAll("#minPrice, #maxPrice").forEach(input => {
                input.addEventListener("input", function () {
                    if (this.value < 0)
                        this.value = "";
                });
            });

            document.addEventListener("click", function (event) {
                const filterForm = document.getElementById("filterForm");
                if (!filterForm.contains(event.target) && !event.target.matches("#filterToggleBtn")) {
                    filterForm.classList.remove("show");
                }
            });
        </script>
        <%@ include file="footer.jsp" %>
    </body>
</html>