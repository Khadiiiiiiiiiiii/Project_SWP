<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/navbar.css">

<%
    String currentPage = request.getRequestURI();
%>

<nav class="navbar">
    <a href="Home.jsp" class="logo">TECH STORE</a>
    <div class="nav-container">
        <div class="nav-links">
            <a href="Home.jsp" class="<%= currentPage.contains("Home.jsp") ? "active" : "" %>">Home</a>
            <a href="category?category=all" class="<%= currentPage.contains("category?category=all") ? "active" : "" %>">Product</a>
            <a href="About.jsp" class="<%= currentPage.contains("About.jsp") ? "active" : "" %>">About</a>
            <a href="Contact.jsp" class="<%= currentPage.contains("Contact.jsp") ? "active" : "" %>">Contact</a>
        </div>
        <div class="search-bar">
            <form action="search" method="GET">
                <input type="text" name="searchQuery" placeholder="Search product..." required>
                <button class="search-btn"><i class="fa-solid fa-magnifying-glass"></i></button>
            </form>
        </div>
        <div class="user-cart">
            <a href="viewProfile.jsp"><i class="fa-solid fa-user"></i></a>
            <a href="cart.jsp"><i class="fa-solid fa-cart-shopping"></i></a>
        </div>
    </div>
</nav>
