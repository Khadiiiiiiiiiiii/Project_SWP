<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.model.Promotion, com.mvc.model.User, java.time.LocalDate" %>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");
    if (!("Store Manager".equals(user.getRole()) || "store manager".equals(user.getRole()) || "Admin".equals(user.getRole()))) {
        response.sendRedirect("login.jsp");
        return;
    }

    Promotion promotion = (Promotion) request.getAttribute("promotion");
    String storeId = (String) request.getAttribute("storeId");
    if (promotion == null) {
        response.sendRedirect("storeManagerDashboard.jsp?storeId=" + storeId);
        return;
    }

    LocalDate today = LocalDate.now();
%>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Update Promotion</title>
        <link rel="stylesheet" href="CSS/UpdateDiscount.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <style>
            .error-message {
                color: red;
                font-weight: bold;
                margin-bottom: 15px;
                text-align: center;
            }
            .success-message {
                color: green;
                font-weight: bold;
                margin-bottom: 15px;
                text-align: center;
            }
        </style>
        <script>
            function validateUpdateForm() {
                var status = document.getElementById("status").value;
                var expirationDate = document.getElementById("expirationDate").value;
                var today = new Date();
                today.setHours(0, 0, 0, 0);
                var selectedDate = new Date(expirationDate);

                // 47.0.E3: Kiểm tra nếu trạng thái là Active và ngày hết hạn trước ngày hiện tại
                if (status === "Active" && selectedDate < today) {
                    alert("Please update the expiration date to today or a future date before activating the discount code.");
                    return false;
                }

                // 47.0.E2: Kiểm tra phần trăm giảm giá
                var discountPercentage = document.getElementById("discountPercentage").value;
                if (discountPercentage < 0 || discountPercentage > 100) {
                    alert("Discount percentage must be between 0 and 100.");
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
                        <a href="storeManagerDashboard.jsp" class="active">
                            <i class="fas fa-tags"></i> Manage Promotions
                        </a>
                    </li>
                    <li>
                        <a href="ManageProductDiscounts.jsp" class="<%= "ManageProductDiscounts.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-percent"></i> Manage Product Discounts
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
                <h2>Update Promotion</h2>

                <!-- Hiển thị thông báo lỗi hoặc thành công -->
                <c:if test="${not empty error}">
                    <div class="error-message">
                        <p>${error}</p>
                    </div>
                </c:if>
                <c:if test="${not empty message}">
                    <div class="success-message">
                        <p>${message}</p>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/DiscountServlet" method="post" onsubmit="return validateUpdateForm()">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="promotionId" value="<%= promotion.getPromotionId() %>">
                    <input type="hidden" name="storeId" value="<%= storeId %>">

                    <label for="code">Promo Code:</label>
                    <input type="text" id="code" name="code" value="<%= promotion.getCode() %>" required>

                    <label for="discountPercentage">Discount Percentage (%):</label>
                    <input type="number" id="discountPercentage" name="discountPercentage" value="<%= promotion.getDiscountPercentage() %>" required>

                    <label for="expirationDate">Expiration Date:</label>
                    <input type="date" id="expirationDate" name="expirationDate" value="<%= promotion.getExpirationDate() %>" required>

                    <label for="status">Status:</label>
                    <select id="status" name="status" required>
                        <option value="Active" <%= "Active".equals(promotion.getStatus()) ? "selected" : "" %>>Active</option>
                        <option value="Inactive" <%= "Inactive".equals(promotion.getStatus()) ? "selected" : "" %>>Inactive</option>
                    </select>

                    <input type="submit" value="Update Promotion" class="update-btn">
                </form>

                <a href="${pageContext.request.contextPath}/storeManagerDashboard.jsp?storeId=<%= storeId %>" class="back-btn">Back to Dashboard</a>
            </div>
        </div>
    </body>
</html>