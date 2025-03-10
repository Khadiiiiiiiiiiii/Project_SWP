<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Add New Staff</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .form-group { margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h2>Add New Staff</h2>
        <hr />
        <c:if test="${not empty err}">
            <p class="text-danger">${err}</p>
        </c:if>
        <form action="addStaff" method="POST" class="needs-validation" novalidate>
            <!-- First Name -->
            <div class="form-group row">
                <label for="txtfirstname" class="col-md-2 col-form-label">First Name</label>
                <div class="col-md-10">
                    <input type="text" class="form-control" id="txtfirstname" name="txtfirstname" value="${param.txtfirstname}" required />
                    <div class="invalid-feedback">Please enter the first name.</div>
                </div>
            </div>

            <!-- Last Name -->
            <div class="form-group row">
                <label for="txtlastname" class="col-md-2 col-form-label">Last Name</label>
                <div class="col-md-10">
                    <input type="text" class="form-control" id="txtlastname" name="txtlastname" value="${param.txtlastname}" required />
                    <div class="invalid-feedback">Please enter the last name.</div>
                </div>
            </div>

            <!-- Email -->
            <div class="form-group row">
                <label for="txtemail" class="col-md-2 col-form-label">Email</label>
                <div class="col-md-10">
                    <input type="email" class="form-control" id="txtemail" name="txtemail" value="${param.txtemail}" required />
                    <div class="invalid-feedback">Please enter a valid email address.</div>
                </div>
            </div>

            <!-- Password -->
            <div class="form-group row">
                <label for="txtpassword" class="col-md-2 col-form-label">Password</label>
                <div class="col-md-10">
                    <input type="password" class="form-control" id="txtpassword" name="txtpassword" required />
                    <div class="invalid-feedback">Please enter a password.</div>
                </div>
            </div>

            <!-- Phone -->
            <div class="form-group row">
                <label for="txtphone" class="col-md-2 col-form-label">Phone</label>
                <div class="col-md-10">
                    <input type="tel" class="form-control" id="txtphone" name="txtphone" value="${param.txtphone}" required />
                    <div class="invalid-feedback">Please enter a phone number.</div>
                </div>
            </div>

            <!-- Address -->
            <div class="form-group row">
                <label for="txtaddress" class="col-md-2 col-form-label">Address</label>
                <div class="col-md-10">
                    <input type="text" class="form-control" id="txtaddress" name="txtaddress" value="${param.txtaddress}" required />
                    <div class="invalid-feedback">Please enter an address.</div>
                </div>
            </div>

            <!-- Submit and Back Buttons -->
            <div class="form-group row mb-3">
                <div class="col-md-10 offset-md-2">
                    <button type="submit" class="btn btn-primary" name="btnCreate">Add</button>
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
    </script>
</body>
</html>