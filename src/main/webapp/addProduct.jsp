<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add New Product</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="container mt-5">
            <h2>Add New Product</h2>
            <c:if test="${not empty errors['general']}">
                <div class="alert alert-danger" role="alert">
                    ${errors['general']}
                </div>
            </c:if>
            <hr />
            <form action="addProduct" method="POST" enctype="multipart/form-data" class="needs-validation" novalidate>
                <!-- Product Name -->
                <div class="form-group row mb-3">
                    <label for="productName" class="col-md-2 col-form-label">Product Name</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control ${not empty errors['productName'] ? 'is-invalid' : ''}" 
                               id="productName" name="productName" value="${param.productName}" 
                               required minlength="6" maxlength="100" pattern="^[a-zA-Z0-9\s\-\(\)']+$" />
                        <div class="invalid-feedback">
                            ${not empty errors['productName'] ? errors['productName'] : 'Product name must be 6-100 characters, only letters, numbers, spaces, -allowed.'}
                        </div>
                    </div>
                </div>

                <!-- Description -->
                <div class="form-group row mb-3">
                    <label for="description" class="col-md-2 col-form-label">Description</label>
                    <div class="col-md-10">
                        <textarea class="form-control ${not empty errors['description'] ? 'is-invalid' : ''}" 
                                  id="description" name="description" required minlength="6" maxlength="500">${param.description}</textarea>
                        <div class="invalid-feedback">
                            ${not empty errors['description'] ? errors['description'] : 'Description must be 6-500 characters.'}
                        </div>
                    </div>
                </div>

                <!-- Price -->
                <div class="form-group row mb-3">
                    <label for="price" class="col-md-2 col-form-label">Price</label>
                    <div class="col-md-10">
                        <input type="number" step="1" class="form-control ${not empty errors['price'] ? 'is-invalid' : ''}" 
                               id="price" name="price" value="${param.price}" required min="1" />
                        <div class="invalid-feedback">
                            ${not empty errors['price'] ? errors['price'] : 'Price must be a positive integer.'}
                        </div>
                    </div>
                </div>

                <!-- Discount Price -->
                <div class="form-group row mb-3">
                    <label for="discountPrice" class="col-md-2 col-form-label">Discount Price </label>
                    <div class="col-md-10">
                        <input type="number" step="1" placeholder="Leave blank if no discount...." class="form-control ${not empty errors['discountPrice'] ? 'is-invalid' : ''}" 
                               id="discountPrice" name="discountPrice" value="${param.discountPrice}" min="0" max="100" />
                        <div class="invalid-feedback">
                            ${not empty errors['discountPrice'] ? errors['discountPrice'] : 'Discount price must be an integer between 0 and 100.'}
                        </div>
                    </div>
                </div>

                <!-- Category -->
                <div class="form-group row mb-3">
                    <label for="categoryId" class="col-md-2 col-form-label">Category</label>
                    <div class="col-md-10">
                        <select class="form-control ${not empty errors['categoryId'] ? 'is-invalid' : ''}" 
                                id="categoryId" name="categoryId" required>
                            <option value="">Select Category</option>
                            <option value="1" ${param.categoryId == '1' ? 'selected' : ''}>Laptop</option>
                            <option value="2" ${param.categoryId == '2' ? 'selected' : ''}>Mouse</option>
                            <option value="3" ${param.categoryId == '3' ? 'selected' : ''}>Keyboard</option>
                            <option value="4" ${param.categoryId == '4' ? 'selected' : ''}>Screen</option>
                            <option value="5" ${param.categoryId == '5' ? 'selected' : ''}>Headphone</option>
                        </select>
                        <div class="invalid-feedback">
                            ${not empty errors['categoryId'] ? errors['categoryId'] : 'Please select a category.'}
                        </div>
                    </div>
                </div>

                <!-- Stock Quantity -->
                <div class="form-group row mb-3">
                    <label for="stockQuantity" class="col-md-2 col-form-label">Stock Quantity</label>
                    <div class="col-md-10">
                        <input type="number" step="1" class="form-control ${not empty errors['stockQuantity'] ? 'is-invalid' : ''}" 
                               id="stockQuantity" name="stockQuantity" value="${param.stockQuantity}" required min="1" max="10000" />
                        <div class="invalid-feedback">
                            ${not empty errors['stockQuantity'] ? errors['stockQuantity'] : 'Stock quantity must be an integer between 1 and 10,000.'}
                        </div>
                    </div>
                </div>

                <!-- Image Upload -->
                <div class="form-group row mb-3">
                    <label for="imageFile" class="col-md-2 col-form-label">Upload Image</label>
                    <div class="col-md-10">
                        <div class="custom-file-upload">
                            <button type="button" class="btn btn-secondary" onclick="document.getElementById('imageFile').click()">Choose File</button>
                            <span id="fileName">No file selected</span>
                        </div>
                        <input type="file" id="imageFile" name="imageFile" accept="image/*" 
                               class="${not empty errors['imageFile'] ? 'is-invalid' : ''}" required style="display: none;" />
                        <div class="invalid-feedback">
                            ${not empty errors['imageFile'] ? errors['imageFile'] : 'Please upload an image (max 5MB, JPG/PNG/GIF).'}
                        </div>
                    </div>
                </div>

                <!-- Promotion ID -->
                <div class="form-group row mb-3">
                    <label for="promotionId" class="col-md-2 col-form-label">Promotion ID</label>
                    <div class="col-md-10">
                        <input type="number" step="1" class="form-control ${not empty errors['promotionId'] ? 'is-invalid' : ''}" 
                               id="promotionId" name="promotionId" value="${param.promotionId}" min="0" />
                        <div class="invalid-feedback">
                            ${not empty errors['promotionId'] ? errors['promotionId'] : 'Promotion ID must be a non-negative integer.'}
                        </div>
                    </div>
                </div>

                <!-- Submit and Back Buttons -->
                <div class="form-group row mb-3">
                    <div class="col-md-10 offset-md-2">
                        <button type="submit" class="btn btn-primary" name="btnCreate">Add Product</button>
                        <a href="ProductManagement" class="btn btn-danger">Back to list</a>
                    </div>
                </div>
            </form>
        </div>

        <script>
            // Hiển thị tên file khi chọn
            document.getElementById("imageFile").addEventListener("change", function () {
                let fileName = this.files.length > 0 ? this.files[0].name : "No file selected";
                document.getElementById("fileName").textContent = fileName;
            });
        </script>
    </body>
</html>