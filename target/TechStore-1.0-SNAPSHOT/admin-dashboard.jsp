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
    <link rel="stylesheet" href="CSS/admin.css">
</head>
<body onload="showStaffManagement()">
    <!-- Thanh bar trên cùng -->
    <header>
        <nav>
            <ul>
                <li onclick="showStaffManagement()">Staff Management</li>
                <li onclick="showRevenueReport()">View Revenue Report</li>
            </ul>
        </nav>
        <a class="btn logout" href="logout">Logout</a>
    </header>

    <!-- Khu vực quản lý Staff -->
    <section id="staffManagement">
        <h2>Staff Management</h2>
        <div class="container mt-4">

            <!-- Kiểm tra nếu danh sách rỗng -->
            <c:if test="${empty staffList}">
                <p>Không có nhân viên nào để hiển thị.</p>
            </c:if>

            <!-- Hiển thị bảng nếu danh sách không rỗng -->
            <c:if test="${not empty staffList}">
                <table border="1">
                    <thead>
                        <tr>
                            <th>Staff ID</th>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Address</th>
                            <th>Role</th>
                            <th>Hired Date</th>
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
                                    <a href="updateStaff?staffId=${s.staff_id}" class="btn btn-success btn-sm">Update</a>
                                    <a href="deleteStaff?staffId=${s.staff_id}" class="btn btn-danger btn-sm">Delete</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <a href="addStaff.jsp" class="btn btn-danger btn-sm">Create</a>
        </div>
    </section>

    <!-- Khu vực View Revenue Report -->
    <section id="revenueReport" style="display: none;">
        <h2>Revenue Report</h2>
        <p>Hello user</p>
    </section>

    <script>
        function showStaffManagement() {
            document.getElementById("staffManagement").style.display = "block";
            document.getElementById("revenueReport").style.display = "none";
        }

        function showRevenueReport() {
            document.getElementById("staffManagement").style.display = "none";
            document.getElementById("revenueReport").style.display = "block";
        }

        function createStaff() {
            alert("Create Staff clicked!");
            // Ở đây có thể gọi API hoặc chuyển hướng sang trang tạo staff
        }
    </script>
</body>
</html>