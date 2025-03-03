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

        <!-- Search Bar -->
        <div class="search-bar">
            <form action="search" method="GET">
                <input type="text" name="searchQuery" placeholder="Search product..." required>
                <button class="search-btn"><i class="fa-solid fa-magnifying-glass"></i></button>
            </form>
        </div>

        <!-- User & Cart Icons -->
        <div class="user-cart">
            <a href="viewProfile.jsp"><i class="fa-solid fa-user"></i></a>
            <a href="cart.jsp"><i class="fa-solid fa-cart-shopping"></i></a>
        </div>

       <!-- Filter Button -->
<button class="filter-btn" id="filterToggleBtn"><i class="fa-solid fa-filter"></i> Filter</button>

<!-- Filter Form (Popup) -->
<div id="filterForm">
    <form action="/category" method="get"> <!-- Sử dụng đường dẫn tuyệt đối /category -->
        <input type="hidden" name="category" value="${param.category != null ? param.category : 'all'}">
        <label for="minPrice">Min Price:</label>
        <input type="number" id="minPrice" name="minPrice" placeholder="0" value="${param.minPrice}" min="0">

        <label for="maxPrice">Max Price:</label>
        <input type="number" id="maxPrice" name="maxPrice" placeholder="50000000" value="${param.maxPrice}" min="0">

        <button type="submit">Apply Filter</button>
    </form>
</div>
</nav>

<script>
    document.getElementById("filterToggleBtn").addEventListener("click", function () {
        const filterForm = document.getElementById("filterForm");
        filterForm.classList.toggle("show");
    });

    // Ngăn nhập số âm hoặc chữ vào input giá
    document.querySelectorAll("#minPrice, #maxPrice").forEach(input => {
        input.addEventListener("input", function () {
            if (this.value < 0) {
                this.value = "";
            }
        });
    });

    document.addEventListener("click", function (event) {
        const filterForm = document.getElementById("filterForm");
        if (!filterForm.contains(event.target) && !event.target.matches("#filterToggleBtn")) {
            filterForm.classList.remove("show");
        }
    });
</script>
