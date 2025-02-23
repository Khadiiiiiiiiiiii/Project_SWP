<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Font Awesome CDN -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="CSS/navbar.css"> <!-- CSS tách riêng -->

<%
    // Lấy tên trang hiện tại từ URL
    String currentPage = request.getRequestURI();
%>

<nav class="navbar">
    <a href="Home.jsp" class="logo">TECH STORE</a>
    <div class="nav-container">
        <div class="nav-links">
            <a href="Home.jsp" class="<%= currentPage.contains("Home.jsp") ? "active" : "" %>">Home</a>
            <a href="Product.jsp" class="<%= currentPage.contains("Product.jsp") ? "active" : "" %>">Product</a>
            <a href="About.jsp" class="<%= currentPage.contains("About.jsp") ? "active" : "" %>">About</a>
            <a href="Contact.jsp" class="<%= currentPage.contains("Contact.jsp") ? "active" : "" %>">Contact</a>
        </div>

        <div class="search-bar">
            <input type="text" placeholder="Search product...">
            <button class="search-btn">
                <i class="fa-solid fa-magnifying-glass"></i>
            </button>
        </div>

        <div class="user-cart">
            <a href="login.jsp"><i class="fa-solid fa-user"></i></a>
            <a href="#"><i class="fa-solid fa-cart-shopping"></i></a>
        </div>
    </div>
</nav>
