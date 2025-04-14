<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.model.User, com.mvc.DAO.UserDAO, com.mvc.DAO.PromotionDAO, com.mvc.model.Promotion, java.util.List, java.time.LocalDate, java.util.ArrayList" %>

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

    UserDAO userDAO = new UserDAO();
    PromotionDAO promotionDAO = new PromotionDAO();

    int userId = user.getUserId();
    int storeId = userDAO.getStoreIdByUserId(userId);
    List<Promotion> promotions;

    // Check storeId
    if (storeId <= 0) {
        System.out.println("No store found for userId: " + userId);
        promotions = new ArrayList<>(); // Empty list if no storeId is found
    } else {
        promotions = promotionDAO.getPromotionsByStoreId(storeId);
    }

    LocalDate today = LocalDate.now();
    System.out.println("Number of promotions retrieved: " + (promotions != null ? promotions.size() : 0));
%>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Manage Promotions</title>
        <link rel="stylesheet" href="CSS/ManagePromotion.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <style>
            .success-message {
                color: green;
                font-weight: bold;
                margin-bottom: 15px;
                text-align: center;
            }
            .error-message {
                color: red;
                font-weight: bold;
                margin-bottom: 15px;
                text-align: center;
            }
        </style>
        <script>
            // Kiểm tra query parameter 'error' và hiển thị alert nếu có
            window.onload = function() {
                const urlParams = new URLSearchParams(window.location.search);
                const errorMessage = urlParams.get('error');
                if (errorMessage) {
                    alert(errorMessage);
                    // Xóa query parameter 'error' sau khi hiển thị để tránh hiển thị lại khi làm mới trang
                    window.history.replaceState({}, document.title, window.location.pathname + "?storeId=<%= storeId %>");
                }

                const successMessage = urlParams.get('message');
                if (successMessage) {
                    alert(successMessage);
                    // Xóa query parameter 'message' sau khi hiển thị
                    window.history.replaceState({}, document.title, window.location.pathname + "?storeId=<%= storeId %>");
                }
            };

            function validateExpirationDate() {
                var expirationDate = document.getElementById("expirationDate").value;
                var today = new Date();
                today.setHours(0, 0, 0, 0);
                var selectedDate = new Date(expirationDate);
                selectedDate.setHours(0, 0, 0, 0); // Normalize selectedDate

                // Allow expiration date to be today
                if (selectedDate < today) {
                    alert("Expiration date must be today or in the future!");
                    return false;
                }
                return true;
            }
        </script>
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
                        <a href="reviewsManagement" class="<%= request.getRequestURI().contains("reviewsManagement") ? "active" : "" %>">
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
                <h2>Manage Promotions</h2>

                <!-- Display success or error message from query parameters -->
                <c:if test="${not empty param.message}">
                    <div class="success-message">
                        <p>${param.message}</p>
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="error-message">
                        <p>${param.error}</p>
                    </div>
                </c:if>

                <!-- Display error message if no storeId is found -->
                <% if (storeId <= 0) { %>
                <div class="error-message">
                    <p>Error: No store found for your account. Please contact the administrator to assign a store.</p>
                </div>
                <% } %>

                <!-- Form to Create New Promotion -->
                <form action="${pageContext.request.contextPath}/DiscountServlet" method="post" onsubmit="return validateExpirationDate()">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="storeId" value="<%= storeId %>">
                    <label for="code">Promo Code:</label>
                    <input type="text" id="code" name="code" required>
                    <label for="discountPercentage">Discount (%):</label>
                    <input type="number" id="discountPercentage" name="discountPercentage" min="0" max="100" required>
                    <label for="expirationDate">Expiration Date:</label>
                    <input type="date" id="expirationDate" name="expirationDate" required>
                    <label for="status">Status:</label>
                    <select id="status" name="status" required>
                        <option value="Active">Active</option>
                        <option value="Inactive">Inactive</option>
                    </select>
                    <input type="submit" value="Create Promotion" class="create-btn">
                </form>

                <!-- List of Promotions -->
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Promo Code</th>
                                <th>Discount (%)</th>
                                <th>Expiration Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (promotions != null && !promotions.isEmpty()) {
                                    for (Promotion promotion : promotions) {
                                        try {
                                            LocalDate expirationDate = promotion.getExpirationDate().toLocalDate();
                                            System.out.println("Promotion: " + promotion.getCode() + ", Expiration: " + expirationDate + ", Status: " + promotion.getStatus() + ", Today: " + today);
                                            // Display Inactive codes or Active codes that are not expired
                                            if ("Inactive".equals(promotion.getStatus()) || ("Active".equals(promotion.getStatus()) && !expirationDate.isBefore(today))) {
                            %>
                            <tr>
                                <td><%= promotion.getCode() %></td>
                                <td><%= promotion.getDiscountPercentage() %></td>
                                <td><%= promotion.getExpirationDate() %></td>
                                <td><%= promotion.getStatus() %></td>
                                <td>
                                    <div class="button_gr">
                                        <form action="${pageContext.request.contextPath}/DiscountServlet" method="get">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="promotionId" value="<%= promotion.getPromotionId() %>">
                                            <input type="hidden" name="storeId" value="<%= storeId %>">
                                            <input type="submit" value="Update" class="update_button">
                                        </form>
                                        <form action="${pageContext.request.contextPath}/DiscountServlet" method="get" onsubmit="return confirm('Are you sure you want to delete this discount code?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="promotionId" value="<%= promotion.getPromotionId() %>">
                                            <input type="hidden" name="storeId" value="<%= storeId %>">
                                            <input type="submit" value="Delete" class="delete_button">
                                        </form>
                                    </div>
                                </td>
                            </tr>
                            <%
                                            }
                                        } catch (Exception e) {
                                            System.out.println("Error processing promotion: " + promotion.getCode() + ", Error: " + e.getMessage());
                                        }
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="5">No promotions available.</td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>