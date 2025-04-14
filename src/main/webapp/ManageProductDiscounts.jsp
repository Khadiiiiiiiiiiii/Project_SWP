<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.model.User, com.mvc.DAO.UserDAO, com.mvc.DAO.ProductDAO, com.mvc.model.Product, java.util.List, java.math.BigDecimal" %>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");
    if (!("Store Manager".equals(user.getRole()) || "store manager".equals(user.getRole()))) {
        response.sendRedirect("login.jsp");
        return;
    }

    ProductDAO productDAO = new ProductDAO();
    List<Product> products = productDAO.getAllProducts();
%>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Manage Product Discounts</title>
        <link rel="stylesheet" href="CSS/ManagePromotion.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <style>
            .success-message {
                background-color: #d4edda;
                color: #155724;
                padding: 10px;
                margin-bottom: 15px;
                border: 1px solid #c3e6cb;
                border-radius: 4px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Store Manager Dashboard</h2>
            </div>
            <nav>
                <ul>
                    <li>
                        <a href="storeManagerDashboard.jsp" class="<%= "storeManagerDashboard.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-tags"></i> Manage Promotions
                        </a>
                    </li>
                    <li>
                        <a href="ManageProductDiscounts.jsp" class="<%= "ManageProductDiscounts.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-percent"></i> Manage Product Discounts
                        </a>
                    </li>
                    <li>
                        <a href="reviewsManagement"
                           class="<%= request.getRequestURI().contains("reviewsManagement") ? "active" : "" %>">
                            <i class="fas fa-comment-dots"></i> Manage Reviews
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement" class="<%= "ProductManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="ListOrderManagement" class="<%= request.getRequestURI().contains("ListOrderManagement") ? "active" : "" %>">
                            <i class="fas fa-shopping-cart"></i> Order Management
                        </a>
                    </li>
                    <li>
                        <a href="logout" class="logout">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="main-content">
            <div class="container">
                <h2>Manage Product Discounts</h2>

                <!-- Display success message if present -->
                <%
                    String message = (String) session.getAttribute("message");
                    if (message != null) {
                %>
                <div class="success-message">
                    <%= message %>
                </div>
                <%
                        session.removeAttribute("message"); // Remove message after displaying
                    }
                %>

                <!-- Form to Update Product Discount -->
                <form action="${pageContext.request.contextPath}/ProductDiscountServlet" method="post">
                    <input type="hidden" name="action" value="updateDiscount">
                    <label for="productId">Product Name:</label>
                    <select id="productId" name="productId" required>
                        <%
                            for (Product product : products) {
                        %>
                        <option value="<%= product.getProductId() %>"><%= product.getName() %></option>
                        <%
                            }
                        %>
                    </select>
                    <label for="discountPercentage">Discount (%):</label>
                    <input type="number" id="discountPercentage" name="discountPercentage" min="0" max="100" required>
                    <input type="submit" value="Update Discount" class="update-btn">
                </form>

                <!-- List of Products with Discounts -->
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Product Name</th>
                                <th>Original Price</th>
                                <th>Discount (%)</th>
                                <th>Discounted Price</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (Product product : products) {
                                    if (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {
                            %>
                            <tr>
                                <td><%= product.getName() %></td>
                                <td><%= product.getFormattedPrice() %></td>
                                <td><%= product.getFormattedDiscountPercentage() %></td>
                                <td><%= product.getFormattedDiscountedPrice(null) %></td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>