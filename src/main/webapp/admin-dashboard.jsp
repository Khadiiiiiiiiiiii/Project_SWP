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
                <ul>  <!-- Goi ham -->
                    <li onclick="showSection('staffManagement')">Staff Management</li>
                    <li onclick="showSection('revenueReport')">View Revenue Report</li>
                    <li onclick="showSection('ProductManagement')">Product Management</li>

                </ul>
            </nav>
            <a class="btn logout" href="logout">Logout</a>
        </header>

        <!-- Khu vực quản lý Staff -->
        <section id="staffManagement">
            <h2>Staff Management</h2>
            <div class="container mt-4">

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

        <!-- Chế độ quản lý nhân cửa hàng -->
        <section id="ProductManagement" style="display: none;">
            <a href="ProductManagement"></a>
        </section>

        <!-- Khu vực View Revenue Report -->
        <section id="revenueReport" style="display: none;">
            <h2>Revenue Report</h2>
            <p>Hello user</p>
        </section>

        <script>
            function showSection(sectionId) {
                // Ẩn tất cả các section
                document.getElementById("staffManagement").style.display = "none";
                document.getElementById("revenueReport").style.display = "none";
                document.getElementById("ProductManagement").style.display = "none";

                // Nếu là Store Mode thì chuyển trang
                if (sectionId === "ProductManagement") {
                    window.location.href = "ProductManagement";
                } else {
                    // Hiển thị section được chọn
                    document.getElementById(sectionId).style.display = "block";
                }
            }


        </script>
    </body>
</html>