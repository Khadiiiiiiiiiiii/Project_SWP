<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mvc.model.User" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Admin".equals(user.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
    <head>
        <title>Admin Dashboard</title>
        <link rel="stylesheet" href="CSS/StaffManagement.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        
    </head>
    <body>
        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Admin Dashboard</h2>
            </div>
            <nav>
                <ul>
                    <li>
                        <a href="admin" class="active">
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
                        <a href="CustomerManagement.jsp" class="<%= "CustomerManagement.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
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
            <div class="container">

                <!-- Nút Create -->
                <a href="addStaff.jsp" class="add-new-btn">Create Staff</a>

                <h2>Staff List</h2>
                <!-- Hiển thị bảng -->
                <c:if test="${not empty staffList}">
                    <table border="1">
                        <thead>
                            <tr>
                                <th>Staff ID</th>
                                <th>First Name</th>
                                <th>Last Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th style="width: 180px;">Address</th>
                                <th>Role</th>
                                <th style="width: 180px;">Hired Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${staffList}" var="s">
                                <tr>
                                    <td>${s.staff_id}</td>
                                    <td>${s.first_name}</td>
                                    <td>${s.last_name}</td>
                                    <td>${s.email}</td>
                                    <td>${s.phone}</td>
                                    <td>${s.address}</td>
                                    <td>${s.role}</td>
                                    <td>${s.hired_date}</td>
                                    <td>
                                        <div class="button_gr">
                                            <a href="updateStaff?staffId=${s.staff_id}" class="update_button">Update</a>
                                            <a href="deleteStaff?staffId=${s.staff_id}" class="delete_button">Delete</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </body>
</html>