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

        <style>
            /* Wrapper cho toàn bộ grid */
            .product-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); /* responsive */
                gap: 20px;
                justify-content: center;   /* căn giữa các cột nếu chưa đủ hàng */
                padding: 40px 20px;
                max-width: 1200px;
                margin: 0 auto;  /* căn giữa toàn bộ grid trong trang */
            }

            /* Từng sản phẩm */
            .product-item {
                background-color: #fff;
                border: 1px solid #ddd;
                border-radius: 8px;
                overflow: hidden;
                text-align: center;
                padding: 15px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                transition: transform 0.2s ease;
            }

            .product-item:hover {
                transform: translateY(-5px);
            }

            /* Ảnh sản phẩm */
            .product-item img {
                max-width: 100%;
                height: auto;
                margin-bottom: 10px;
            }

            /* Tên sản phẩm */
            .product-item h3 {
                font-size: 16px;
                margin: 10px 0;
                color: #333;
            }

            /* Giá */
            .product-item .price {
                font-size: 14px;
                color: #555;
            }

            /* Discount style */
            .product-item del {
                font-size: 13px;
                color: #999;
            }

            .discount-frame {
                background-color: red;
                padding: 2px 6px;
                border-radius: 4px;
                margin-left: 5px;
                font-size: 12px;
            }

        </style>
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

                                    <div class="price">
                                        <c:choose>
                                            <c:when test="${product.discountPrice != null}">
                                                <del style="color: #000; font-size: 14px; font-weight: normal;">
                                                    ${product.formattedPrice}
                                                </del><br>
                                                <span style="color: #ff0000; font-weight: bold;">
                                                    ${product.formattedDiscountedPrice}
                                                </span>
                                                <span class="discount-frame" style="color: white; font-weight: bold;">
                                                    ${product.formattedDiscountPercentage}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #000;">${product.formattedPrice}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
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
