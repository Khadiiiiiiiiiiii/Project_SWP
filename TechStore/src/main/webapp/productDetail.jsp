<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.DAO.ProductDAO, com.mvc.model.Product" %>

<%
    // Lấy productId từ URL
    String productId = request.getParameter("productId");
    System.out.println("Received productId: " + productId); // Debug log
    Product product = null;

    if (productId != null && !productId.isEmpty()) {
        ProductDAO productDAO = new ProductDAO();
        product = productDAO.getProductById(Integer.parseInt(productId));
        
        if (product != null) {
            System.out.println("Loaded Product: " + product.getName());
            System.out.println("Price: " + product.getPrice());
            System.out.println("Discount Price: " + product.getDiscountPrice());
            System.out.println("Image URL: " + product.getImageUrl());
        } else {
            System.out.println("Product not found.");
        }
    }
%>

<% if (product != null) { %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= product.getName() %> - Product Detail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/style.css">
</head>
<body>
    <%@ include file="navbar.jsp" %>
    <h1><%= product.getName() %></h1>
    <div class="product-detail">
        <!-- Hiển thị ảnh sản phẩm -->
        <img src="${pageContext.request.contextPath}/<%= product.getImageUrl() %>" 
             alt="<%= product.getName() %>" width="300" height="300"/>

        <h3><%= product.getName() %></h3>

        <!-- Hiển thị giá tiền -->
        <p class="price">
            <strong>Price:</strong> 
            <% if (product.getDiscountPrice() != null) { %>
                <del><%= product.getPrice() %> VND</del> <!-- Giá gốc bị gạch ngang -->
                <span style="color:red;"> <%= product.getPrice().subtract(product.getDiscountPrice()) %> VND</span> 
                <span class="discount">(-<%= product.getDiscountPrice() %> VND)</span>
            <% } else { %>
                <%= product.getPrice() %> VND
            <% } %>
        </p>

        <p><strong>Description:</strong> <%= product.getDescription() != null ? product.getDescription() : "Updating..." %></p>
        <p><strong>Stock Quantity:</strong> <%= product.getStockQuantity() %></p>
        <p><strong>Category ID:</strong> <%= product.getCategoryId() %></p>

        <!-- Form chọn số lượng và thêm vào giỏ hàng -->
        <form action="addToCart.jsp" method="POST">
            <input type="hidden" name="productId" value="<%= product.getProductId() %>">
            <label for="quantity">Quantity: </label> 
            <input type="number" id="quantity" name="quantity" min="1" max="<%= product.getStockQuantity() %>" value="1">
            <br><br>
            <button type="submit" name="action" value="addToCart">Add to Cart</button>
            <button type="submit" name="action" value="buyNow">Buy Now</button>
        </form>

        <br><br>
        <a href="products.jsp">Back to Products List</a>
    </div>
</body>
</html>
<% } else { %>
    <p>Product not found!</p>
<% } %>
