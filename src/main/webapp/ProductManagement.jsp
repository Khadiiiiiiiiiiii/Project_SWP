<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.mvc.model.User" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !("Admin".equals(user.getRole()) || "Store Manager".equalsIgnoreCase(user.getRole()))) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Product Management</title>
        <link rel="stylesheet" href="CSS/ManageProduct.css">
        
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <style>
            .error-message {
                color: red;
                font-weight: bold;
                margin-bottom: 15px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div class="sidebar">
            <div class="sidebar-header">
                <h2><%= "Admin".equals(user.getRole()) ? "Admin Dashboard" : "Store Manager Dashboard" %></h2>
            </div>
            <nav>
                <ul>
                    <% if ("Admin".equals(user.getRole())) { %>
                    <li>
                        <a href="admin" class="<%= "admin".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-users"></i> Staff Management
                        </a>
                    </li>
                    <li>
                        <a href="RevenueReport.jsp" class="<%= "RevenueReport.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-chart-line"></i> View Revenue Report
                        </a>
                    </li>
                    <li>
                        <a href="ProductManagement" class="active">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="CustomerManagement.jsp" class="<%= "CustomerManagement.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-user-friends"></i> Customer Management
                        </a>
                    </li>
                    <% } else if ("Store Manager".equalsIgnoreCase(user.getRole())) { %>
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
                    <% } %>
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
                <!-- Display error message if any -->
                <c:if test="${not empty err}">
                    <div class="error-message">
                        <p>${err}</p>
                    </div>
                </c:if>

                <a href="addProduct.jsp" class="add-new-btn">Create Product</a>

                <h2>Product List</h2>
                <!-- Hiển thị bảng -->
                <table border="1">
                    <thead>
                        <tr>
                            <th style="width: 115px;">Product ID</th>
                            <th>Product Name</th>
                            <th>Description</th>
                            <th>Price</th>
                            <th>Discount Price</th>
                            <th style="width: 115px;">Category ID</th>
                            <th>Stock Quantity</th>
                            <th>Image</th>
                            <th style="width: 115px;">Promotion ID</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${proList}" var="p">
                            <tr>
                                <td>${p.productId}</td>
                                <td>${p.name}</td>
                                <td>${p.description}</td>
                                <td>${p.price}</td>
                                <td>${p.discountPrice != null ? p.discountPrice : 'N/A'}</td>
                                <td>${p.categoryId}</td>
                                <td>${p.stockQuantity}</td>
                                <td>
                                    <c:if test="${not empty p.imageUrl}">
                                        <img src="${pageContext.request.contextPath}/${p.imageUrl}" style="width: 100px; height: auto;">
                                    </c:if>
                                </td>
                                <td>${p.promotionId != null ? p.promotionId : 'N/A'}</td>
                                <td>${p.isDeleted ? 'Inactive' : 'Active'}</td>
                                <td>
                                    <div class="button_gr">
                                        <a href="updateProduct?productId=${p.productId}" class="update_button">Update</a>
                                        <c:choose>
                                            <c:when test="${p.isDeleted}">
                                                <button onclick="showConfirmModal(${p.productId}, 'restore')" class="restore_button">Restore</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button onclick="showConfirmModal(${p.productId}, 'delete')" class="delete_button">Delete</button>
                                            </c:otherwise>
                                        </c:choose>
                                        <a href="viewProductDetailManagement?productId=${p.productId}" class="detail_button">Detail</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>           
            </div>
        </div>
        <!-- Modal xác nhận -->
        <div id="confirmModal" class="modal">
            <div class="modal-content">
                <p id="confirmMessage"></p>
                <button id="confirmBtn" class="btn-confirm">OK</button>
                <button onclick="closeModal()" class="btn-cancel">Back</button>
            </div>
        </div>

        <script>
            let productIdToProcess = null;
            let actionToProcess = null;

            function showConfirmModal(productId, action) {
                console.log("showConfirmModal called with productId: " + productId + ", action: " + action);
                productIdToProcess = productId;
                actionToProcess = action;
                const modal = document.getElementById("confirmModal");
                const message = document.getElementById("confirmMessage");
                message.textContent = action === 'delete' ? "Do you want to delete this product?" : "Do you want to restore this product?";
                modal.style.display = "block";

                const confirmBtn = document.getElementById("confirmBtn");
                confirmBtn.onclick = function () {
                    console.log("Confirm clicked with productId: " + productIdToProcess + ", action: " + actionToProcess);
                    if (productIdToProcess && actionToProcess) {
                        window.location.href = "ProductController?action=" + actionToProcess + "&productId=" + productIdToProcess;
                    } else {
                        alert("Error: Invalid product ID or action!");
                        closeModal();
                    }
                };
            }

            function closeModal() {
                const modal = document.getElementById("confirmModal");
                modal.style.display = "none";
            }
        </script>
    </body>
</html>