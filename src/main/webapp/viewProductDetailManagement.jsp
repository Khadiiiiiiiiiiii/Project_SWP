<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết sản phẩm</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-4">
        <div class="text-center">
            <img src="${product.imageUrl}" alt="Laptop" class="img-fluid" style="max-width: 300px;">
            <h2 class="mt-3">${product.name}</h2>
        </div>

        <table class="table table-bordered mt-4">
            <thead class="table-dark">
                <tr>
                    <th colspan="2" class="text-center">Product Information</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Product ID</strong></td>
                    <td>${product.productId}</td>
                </tr>
                <tr>
                    <td><strong>Name</strong></td>
                    <td>${product.name}</td>
                </tr>
                <tr>
                    <td><strong>Description</strong></td>
                    <td>${product.description}</td>
                </tr>
                <tr>
                    <td><strong>Price</strong></td>
                    <td>${product.price}</td>
                </tr>
                <tr>
                    <td><strong>Discount Price</strong></td>
                    <td>${product.discountPrice}</td>
                </tr>
                <tr>
                    <td><strong>Category ID</strong></td>
                    <td>${product.categoryId}</td>
                </tr>
                <tr>
                    <td><strong>Stock Quantity</strong></td>
                    <td>${product.stockQuantity}</td>
                </tr>
                <tr>
                    <td><strong>Created At</strong></td>
                    <td>${product.createdAt}</td>
                </tr>
                <tr>
                    <td><strong>Updated At</strong></td>
                    <td>${product.updatedAt}</td>
                </tr>
                <tr>
                    <td><strong>Promotion ID</strong></td>
                    <td>${product.promotionId}</td>
                </tr>
                <tr>
                    <td><strong>State</strong></td>
                    <td>${product.isDeleted ? 'Inactive' : 'Active'}</td>
                </tr>
            </tbody>
        </table>
        <a class="btn btn-secondary" href="ProductManagement">Back</a>
    </div>
</body>
</html>
