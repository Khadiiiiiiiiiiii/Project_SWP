<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <body>
        <!-- Thanh bar trên cùng -->
        <header>
            <nav>
                <ul>
                    <li onclick="showStaffManagement()">Staff Management</li>
                    <li onclick="showRevenueReport()" >View Revenue Report</li>
                </ul>
            </nav>
             <a class="btn logout" href="logout">Logout</a>
        </header>
        <!-- Khu vực quản lý Staff -->
        <section id="staffManagement" style="display: none;">
            <h2>Staff Management</h2>
            <table border="1">
                <thead>
                    <tr>
                        <th>Staff ID</th>
                        <th>User ID</th>
                        <th>Role</th>
                        <th>Hired Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Dữ liệu staff sẽ được đổ vào đây -->
                </tbody>
            </table>
            <button onclick="createStaff()">Create Staff</button>
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
