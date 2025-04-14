<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mvc.model.Customer, com.mvc.DAO.CustomerDAO, com.mvc.dal.DBContext, java.sql.Connection" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String userIdStr = request.getParameter("userId");
    Customer customer = null;
    boolean isEditMode = false;

    if (userIdStr != null && !userIdStr.isEmpty()) {
        try {
            int userId = Integer.parseInt(userIdStr);
            Connection connection = DBContext.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);
            customer = customerDAO.getCustomerByID(userId);
            if (customer != null) {
                isEditMode = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String errorMessage = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><%= isEditMode ? "Edit Customer" : "Add Customer" %></title>
        <link rel="stylesheet" href="CSS/editCustomer.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                let form = document.getElementById("customer-form");
                form.addEventListener("submit", function (event) {
                    let isValid = true;
                    let email = document.getElementById("email");
                    let firstName = document.getElementById("firstName");
                    let lastName = document.getElementById("lastName");
                    let phone = document.getElementById("phone");
                    let loyaltyPoints = document.getElementById("loyaltyPoints");

                    // Xóa lỗi cũ
                    document.querySelectorAll(".error-message").forEach(el => el.style.display = "none");
                    document.querySelectorAll("input, textarea, select").forEach(el => el.classList.remove("error-input"));

                    // Kiểm tra email hợp lệ
                    let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
                    if (!emailPattern.test(email.value.trim())) {
                        showError(email, "Please provide a valid email address.");
                        isValid = false;
                    }

                    // Kiểm tra họ và tên (không được để trống)
                    if (firstName.value.trim() === "") {
                        showError(firstName, "First name is mandatory and cannot be empty.");
                        isValid = false;
                    }
                    if (lastName.value.trim() === "") {
                        showError(lastName, "Last name is mandatory and cannot be empty.");
                        isValid = false;
                    }

                    // Kiểm tra số điện thoại (phải là số)
                    let phonePattern = /^[0-9]{10,15}$/;
                    if (phone.value.trim() !== "" && !phonePattern.test(phone.value.trim())) {
                        showError(phone, "Please enter a valid phone number (must be 10-15 digits).");
                        isValid = false;
                    }

                    // Kiểm tra loyalty points (không được âm)
                    if (loyaltyPoints.value < 0) {
                        showError(loyaltyPoints, "Loyalty points must be a non-negative number.");
                        isValid = false;
                    }

                    if (!isValid) {
                        event.preventDefault();
                    }
                });
            });

            function showError(input, message) {
                let errorElement = document.createElement("div");
                errorElement.className = "error-message";
                errorElement.innerText = message;
                input.classList.add("error-input");
                input.parentNode.appendChild(errorElement);
            }
        </script>
    </head>
    <body>
        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Admin Dashboard</h2>
            </div>
            <nav>
                <ul>
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
                        <a href="ProductManagement" class="<%= "ProductManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
                            <i class="fas fa-box"></i> Product Management
                        </a>
                    </li>
                    <li>
                        <a href="CustomerManagement.jsp" class="active">
                            <i class="fas fa-user-friends"></i> Customer Management
                        </a>
                    </li>
                    <li>
                        <a href="ListOrderManagement" class="<%= "ListOrderManagement".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
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
            <div class="container" id="customer-container">
                <h2 class="title"><%= isEditMode ? "Edit Customer" : "Add Customer" %></h2>

                <%-- Hiển thị danh sách lỗi từ Servlet --%>
                <c:if test="${not empty errors}">
                    <div class="error-box">
                        <ul class="error-list">
                            <c:forEach var="error" items="${errors}">
                                <li><strong>${error}</strong></li>
                                    </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <%-- Hiển thị thông báo lỗi từ tham số error --%>
                <% if (errorMessage != null) { %>
                <div class="error-box">
                    <p><strong>
                            <% if ("missing_id".equals(errorMessage)) { %>
                            Customer ID is missing. Please ensure it is provided and try again.
                            <% } else if ("invalid_input".equals(errorMessage)) { %>
                            Invalid input detected. Please review all fields and correct any errors before submitting.
                            <% } else if ("invalid_phone".equals(errorMessage)) { %>
                            The phone number provided is invalid. Please ensure it contains only 10-15 digits.
                            <% } else if ("phone_already_exists".equals(errorMessage)) { %>
                            The phone number is already in use by another customer. Please provide a different number.
                            <% } else if ("email_already_exists".equals(errorMessage)) { %>
                            The email address is already in use by another customer. Please provide a different email.
                            <% } else if ("invalid_loyalty_points".equals(errorMessage)) { %>
                            Loyalty points must be a non-negative value. Please correct this field.
                            <% } else if ("invalid_id".equals(errorMessage)) { %>
                            The customer ID format is incorrect. Please verify and try again.
                            <% } else if ("update_failed".equals(errorMessage)) { %>
                            Failed to update customer information. Please try again later or contact support.
                            <% } else if ("exception".equals(errorMessage)) { %>
                            An unexpected error occurred. If the issue persists, please contact technical support.
                            <% } %>
                        </strong></p>
                </div>
                <% } %>

                <form action="<%= isEditMode ? "UpdateCustomerServlet" : "AddCustomerServlet" %>" method="post" class="customer-form" id="customer-form">
                    <% if (isEditMode) { %>
                    <input type="hidden" name="userId" value="<%= customer.getUserId() %>">
                    <% } %>

                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" value="<%= isEditMode ? customer.getEmail() : "" %>" required class="input-field">

                    <label for="firstName">First Name:</label>
                    <input type="text" id="firstName" name="firstName" value="<%= isEditMode ? customer.getFirstName() : "" %>" required class="input-field">

                    <label for="lastName">Last Name:</label>
                    <input type="text" id="lastName" name="lastName" value="<%= isEditMode ? customer.getLastName() : "" %>" required class="input-field">

                    <label for="phone">Phone:</label>
                    <input type="text" id="phone" name="phone" value="<%= isEditMode ? customer.getPhone() : "" %>" class="input-field">

                    <label for="address">Address:</label>
                    <textarea id="address" name="address" class="input-field textarea"><%= isEditMode ? customer.getAddress() : "" %></textarea>

                    <label for="loyaltyPoints">Loyalty Points:</label>
                    <input type="number" id="loyaltyPoints" name="loyaltyPoints" value="<%= isEditMode ? (customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0) : 0 %>" class="input-field">

                    <label for="preferredPaymentMethod">Preferred Payment Method:</label>
                    <select id="preferredPaymentMethod" name="preferredPaymentMethod" class="input-field">
                        <option value="Credit Card" <%= isEditMode && "Credit Card".equals(customer.getPreferredPaymentMethod()) ? "selected" : "" %>>Credit Card</option>
                        <option value="PayPal" <%= isEditMode && "PayPal".equals(customer.getPreferredPaymentMethod()) ? "selected" : "" %>>PayPal</option>
                        <option value="Cash on Delivery" <%= isEditMode && "Cash on Delivery".equals(customer.getPreferredPaymentMethod()) ? "selected" : "" %>>Cash on Delivery</option>
                        <option value="Other" <%= isEditMode && "Other".equals(customer.getPreferredPaymentMethod()) ? "selected" : "" %>>Other</option>
                    </select>

                    <input type="submit" value="<%= isEditMode ? "Update Customer" : "Add Customer" %>" class="submit-btn">
                </form>
                <a href="CustomerManagement.jsp" class="back-btn">Back to Dashboard</a>
            </div>
        </div>
    </body>
</html>