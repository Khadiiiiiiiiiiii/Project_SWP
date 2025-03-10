<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>
    <link rel="stylesheet" href="CSS/productManagement.css">
</head>
<body>
    <!-- Thanh bar trên cùng -->
    <header>
        <nav>
            <ul>
                <li onclick="showSection('ViewProductList')">View Product List</li>
                <li onclick="showSection('ViewProductDetail')">View Product Detail</li>
            </ul>
        </nav>
        <a class="btn logout" href="admin">Back</a>
    </header>

    <!-- Khu vực quản lý List -->
    <section id="ViewProductList">
        <h2>Product List</h2>
        <div class="container mt-4">
            <!-- Kiểm tra nếu danh sách rỗng -->
            <c:if test="${empty proList}">
                <p class="text-danger">No products found. Check database connection or data.</p>
            </c:if>
                <a href="addProduct.jsp" class="btn btn-danger btn-sm">Create</a>
            <!-- Hiển thị bảng -->
            <table border="1">
                <thead>
                    <tr>
                        <th>Product ID</th>
                        <th>Product Name</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Discount Price</th>
                        <th>Category ID</th>
                        <th>Stock Quantity</th>
                        <th>Image</th>
                        <th>Promotion ID</th>
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
                            <td>${p.isDeleted ? 'Deleted' : 'Active'}</td>
                            <td>
                                <a href="updateProduct?productId=${p.productId}" class="btn btn-success btn-sm">Update</a>
                                <a href="deleteProduct?productId=${p.productId}" class="btn btn-danger btn-sm">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>           
        </div>
    </section>

    <!-- Khu vực View Product Detail -->
    <section id="ViewProductDetail" style="display: none;">
        <h2>View Product Detail</h2>
        <p>Hello</p>
    </section>

    <script>
        function showSection(sectionId) {
            
            document.getElementById("ViewProductList").style.display = "none";
            document.getElementById("ViewProductDetail").style.display = "none";
        
            
            document.getElementById(sectionId).style.display = "block";
        }
    </script>
</body>
</html>