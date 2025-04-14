<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
    /* Container layout */
    .container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 20px;
    }

    /* Section title */
    .product-section h2 {
        text-align: center;
        margin-bottom: 30px;
        font-size: 24px;
        color: #333;
    }

    /* Grid layout for products */
    .grid-view {
        display: flex;
        justify-content: center;
        gap: 30px;
        flex-wrap: wrap;
    }

    /* Product card */
    .product-item {
        width: 250px;
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.06);
        text-align: center;
        padding: 15px;
        transition: transform 0.3s ease;
    }

    /* Hover effect */
    .product-item:hover {
        transform: translateY(-5px);
    }

    /* Product image */
    .product-item img {
        max-width: 100%;
        height: auto;
        border-radius: 8px;
    }

    /* Product name */
    .product-item h3 {
        margin: 10px 0;
        font-size: 16px;
        color: #222;
        text-decoration: none; /* Xoá gạch chân nếu có */
    }

    /* Link wrapper */
    .product-item a {
        text-decoration: none;  /* Xoá gạch chân */
        color: inherit;         /* Giữ màu chữ gốc */
    }

    /* Hover link giữ nguyên */
    .product-item a:hover {
        text-decoration: none;
        color: inherit;
    }

    /* Giá sản phẩm */
    .price {
        font-size: 16px;
        color: #000;
    }

    /* Giá gạch (giá gốc) */
    .price del {
        color: #999;
    }

    /* Discount badge */
    .discount-frame {
        background-color: #f44336; /* đỏ */
        color: #fff;
        padding: 2px 6px;
        margin-left: 5px;
        border-radius: 4px;
        font-size: 13px;
        font-weight: bold;
    }
</style>

<!-- Best Selling Products -->
<div class="container">
    <section class="product-section" id="best-selling-section">
        <h2>🔥 Best-Selling Products</h2>
        <c:if test="${empty bestSellingProducts}">
            <p class="not-found">⚠️ No best-selling products available.</p>
        </c:if>
        <div class="grid-view">
            <c:forEach var="product" items="${bestSellingProducts}">
                <div class="product-item">
                    <a href="productDetail?productId=${product.productId}">
                        <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                        <h3>${product.name}</h3>
                        <p class="price">
                            <c:choose>
                                <c:when test="${product.discountPrice != null && product.discountPrice > 0}">
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
    </section>
</div>

<!-- Top Rated Products -->
<div class="container">
    <section class="product-section">
        <h2>🌟 Top Rated Products</h2>
        <c:if test="${empty topRatedProducts}">
            <p class="not-found">⚠️ No top-rated products available.</p>
        </c:if>
        <div class="grid-view">
            <c:forEach var="product" items="${topRatedProducts}">
                <div class="product-item">
                    <a href="productDetail?productId=${product.productId}">
                        <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}">
                        <h3>${product.name}</h3>
                        <p class="price">
                            <c:choose>
                                <c:when test="${product.discountPrice != null && product.discountPrice > 0}">
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
    </section>
</div>