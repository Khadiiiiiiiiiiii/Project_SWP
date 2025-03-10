<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add New Product</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .form-group {
                margin-bottom: 15px;
            }
        </style>
    </head>
    <body>
        <div class="container mt-5">
            <h2>Add New Product</h2>
            <hr />
            <c:if test="${not empty err}">
                <p class="text-danger">${err}</p>
            </c:if>
            <form action="addProduct" method="POST" class="needs-validation" novalidate>
                <!-- Product Name -->
                <div class="form-group row">
                    <label for="productName" class="col-md-2 col-form-label">Product Name</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control" id="productName" name="productName" value="${param.productName}" required />
                        <div class="invalid-feedback">Please enter the product name.</div>
                    </div>
                </div>

                <!-- Description -->
                <div class="form-group row">
                    <label for="description" class="col-md-2 col-form-label">Description</label>
                    <div class="col-md-10">
                        <textarea class="form-control" id="description" name="description" required>${param.description}</textarea>
                        <div class="invalid-feedback">Please enter a description.</div>
                    </div>
                </div>

                <!-- Price -->
                <div class="form-group row">
                    <label for="price" class="col-md-2 col-form-label">Price</label>
                    <div class="col-md-10">
                        <input type="number" step="0.01" class="form-control" id="price" name="price" value="${param.price}" required />
                        <div class="invalid-feedback">Please enter a valid price.</div>
                    </div>
                </div>

                <!-- Discount Price -->
                <div class="form-group row">
                    <label for="discountPrice" class="col-md-2 col-form-label">Discount Price</label>
                    <div class="col-md-10">
                        <input type="number" step="0.01" class="form-control" id="discountPrice" name="discountPrice" value="${param.discountPrice}" />
                    </div>
                </div>

                <!-- Category ID -->
                <div class="form-group row">
                    <label for="categoryId" class="col-md-2 col-form-label">Category ID</label>
                    <div class="col-md-10">
                        <input type="number" class="form-control" id="categoryId" name="categoryId" value="${param.categoryId}" required />
                        <div class="invalid-feedback">Please enter a category ID.</div>
                    </div>
                </div>

                <!-- Stock Quantity -->
                <div class="form-group row">
                    <label for="stockQuantity" class="col-md-2 col-form-label">Stock Quantity</label>
                    <div class="col-md-10">
                        <input type="number" class="form-control" id="stockQuantity" name="stockQuantity" value="${param.stockQuantity}" required />
                        <div class="invalid-feedback">Please enter the stock quantity.</div>
                    </div>
                </div>

                <!-- Image Upload -->
                <div class="form-group row">
                    <label for="imageFile" class="col-md-2 col-form-label">Upload Image</label>
                    <div class="col-md-10">
                        <input type="file" id="imageFile" name="imageFile" accept="image/*" style="display: none;" />
                        <label for="imageFile" class="btn btn-primary">Upload</label>
                        <span id="fileName">No file selected</span>
                    </div>
                </div>

                <!-- Promotion ID -->
                <div class="form-group row">
                    <label for="promotionId" class="col-md-2 col-form-label">Promotion ID</label>
                    <div class="col-md-10">
                        <input type="number" class="form-control" id="promotionId" name="promotionId" value="${param.promotionId}" />
                    </div>
                </div>

                <!-- Submit and Back Buttons -->
                <div class="form-group row mb-3">
                    <div class="col-md-10 offset-md-2">
                        <button type="submit" class="btn btn-primary" name="btnCreate">Add Product</button>
                        <a href="admin" class="btn btn-danger">Back to list</a>
                    </div>
                </div>
            </form>
        </div>

        <script>
            // Form validation
            (function () {
                'use strict';
                var forms = document.querySelectorAll('.needs-validation');
                Array.prototype.slice.call(forms).forEach(function (form) {
                    form.addEventListener('submit', function (event) {
                        if (!form.checkValidity()) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);
                });
            })();
            
                document.getElementById("imageFile").addEventListener("change", function () {
        let fileName = this.files.length > 0 ? this.files[0].name : "No file selected";
        document.getElementById("fileName").textContent = fileName;
    });
        </script>
    </body>
</html>
