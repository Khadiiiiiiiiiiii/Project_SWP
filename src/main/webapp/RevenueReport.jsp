<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Revenue Reports | Admin Dashboard</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="CSS/revenueReport.css"/>
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap');
            body {
                font-family: 'Poppins', sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f4f4f4;
                display: flex;
            }

            /* Sidebar Styles */
            .sidebar {
                width: 250px;
                background-color: #1e1e2f;
                color: white;
                height: 100vh;
                position: fixed;
                top: 0;
                left: 0;
                box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
                display: flex;
                flex-direction: column;
            }

            .sidebar-header {
                padding: 20px;
                text-align: center;
                border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            }

            .sidebar-header h2 {
                font-size: 20px;
                margin: 0;
                color: white;
            }

            .sidebar nav {
                flex: 1;
            }

            .sidebar nav ul {
                list-style: none;
                padding: 0;
                margin: 0;
            }

            .sidebar nav ul li {
                margin: 10px 0;
            }

            .sidebar nav ul li a {
                display: flex;
                align-items: center;
                padding: 15px 20px;
                color: white;
                text-decoration: none;
                font-weight: 500;
                transition: background-color 0.3s ease;
            }

            .sidebar nav ul li a i {
                margin-right: 10px;
                font-size: 18px;
            }

            .sidebar nav ul li a:hover {
                background-color: #007bff;
            }

            .sidebar nav ul li a.active {
                background-color: #0056b3;
            }

            .sidebar nav ul li a.logout {
                background-color: #ff4d4d;
                margin: 20px;
                border-radius: 5px;
            }

            .sidebar nav ul li a.logout:hover {
                background-color: #cc0000;
            }
        </style>
    </head>
    <body>
        <!-- Sidebar -->
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
                        <a href="revenue?action=list" class="<%= "RevenueReport.jsp".equals(request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1)) ? "active" : "" %>">
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

        <!-- Main Content -->
        <div class="content">
            <div class="container-fluid py-4">
                <div class="row mb-4">
                    <div class="col">
                        <h2><i class="fas fa-chart-line"></i> Revenue Reports</h2>
                    </div>
                </div>

                <!-- Notification Messages -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Summary Cards -->
                <div class="row mb-4">
                    <div class="col-md-4">
                        <div class="card summary-card revenue-card">
                            <div class="card-body">
                                <h5 class="card-title">Total Revenue</h5>
                                <h2 class="card-text">
                                    <c:choose>
                                        <c:when test="${'generate' == param.action && summary != null}">
                                            <fmt:formatNumber value="${summary.totalRevenue}" type="currency" currencySymbol="VND: " />
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${summary != null ? summary.totalRevenue : 0}" type="currency" currencySymbol="VND: " />
                                        </c:otherwise>
                                    </c:choose>
                                </h2>
                                <p class="card-text text-muted">For selected period</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card summary-card orders-card">
                            <div class="card-body">
                                <h5 class="card-title">Total Orders</h5>
                                <h2 class="card-text">${summary != null ? summary.totalOrders : 0}</h2>
                                <p class="card-text text-muted">For selected period</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card summary-card sales-card">
                            <div class="card-body">
                                <h5 class="card-title">Total Items Sold</h5>
                                <h2 class="card-text">${summary != null ? summary.totalSales : 0}</h2>
                                <p class="card-text text-muted">For selected period</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Filter Section -->
                <div class="filter-section">
                    <form action="revenue" method="post" class="row g-3">
                        <input type="hidden" name="action" value="filter">
                        <div class="col-md-3">
                            <label for="startDate" class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="startDate" name="startDate" value="${startDate}" required>
                        </div>
                        <div class="col-md-3">
                            <label for="endDate" class="form-label">End Date</label>
                            <input type="date" class="form-control" id="endDate" name="endDate" value="${endDate}" required>
                        </div>
                        <div class="col-md-3">
                            <label for="storeId" class="form-label">Store</label>
                            <select class="form-select" id="storeId" name="storeId">
                                <option value="0">All Stores</option>
                                <c:forEach items="${stores}" var="store">
                                    <option value="${store.storeId}" ${store.storeId == selectedStoreId ? 'selected' : ''}>${store.storeName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary me-2">
                                <i class="fas fa-filter"></i> Filter
                            </button>
                            <a href="revenue?action=generate" class="btn btn-success">
                                <i class="fas fa-sync-alt"></i> Generate Today's Report
                            </a>
                        </div>
                    </form>
                </div>

                <!-- Reports Table -->
                <div class="card">
                    <div class="card-header bg-white">
                        <h5 class="mb-0">Revenue Reports</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover table-striped">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Date</th>
                                        <th>Store</th>
                                        <th>Revenue</th>
                                        <th>Orders</th>
                                        <th>Items Sold</th>
                                        <th>Average Order Value</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${reports}" var="report">
                                        <tr>
                                            <td>${report.reportId}</td>
                                            <td><fmt:formatDate value="${report.reportDate}" pattern="yyyy-MM-dd" /></td>
                                            <td>${report.storeName}</td>
                                            <td>
                                                <fmt:formatNumber value="${report.totalRevenue}" type="currency" currencySymbol="VND: " />
                                            </td>
                                            <td>${report.totalOrders}</td>
                                            <td>${report.totalSales}</td>
                                            <td>
                                                <c:if test="${report.totalOrders > 0}">
                                                    <fmt:formatNumber value="${report.totalRevenue / report.totalOrders}" type="currency" currencySymbol="VND: " />
                                                </c:if>
                                                <c:if test="${report.totalOrders == 0}">
                                                    N/A
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty reports}">
                                        <tr>
                                            <td colspan="8" class="text-center">No revenue reports found for the selected criteria</td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                if (!document.getElementById('startDate').value) {
                    const today = new Date();
                    const thirtyDaysAgo = new Date();
                    thirtyDaysAgo.setDate(today.getDate() - 30);

                    document.getElementById('endDate').value = formatDate(today);
                    document.getElementById('startDate').value = formatDate(thirtyDaysAgo);
                }
            });

            function formatDate(date) {
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
                    }
        </script>
    </body>
</html>