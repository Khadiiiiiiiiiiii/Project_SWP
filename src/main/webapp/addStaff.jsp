<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add New Staff</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="CSS/addStaff.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    </head>
    <body>
        <div class="container mt-5">
            <h2>Add New Staff</h2>
            <hr />
            <c:if test="${not empty success}">
                <p class="text-success">${success}</p>
            </c:if>
            <c:if test="${not empty err}">
                <p class="text-danger">${err}</p>
            </c:if>
            <form action="addStaff" method="POST" class="needs-validation" novalidate>
                <!-- First Name -->
                <div class="form-group row">
                    <label for="txtfirstname" class="col-md-2 col-form-label">First Name</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control" placeholder="Enter First Name..." id="txtfirstname" name="txtfirstname" value="${param.txtfirstname}"  />
                        <c:if test="${not empty errFirstName}">
                            <div class="invalid-feedback">${errFirstName}</div>
                        </c:if>
                        <c:if test="${empty errFirstName && not empty param.txtfirstname}">
                            <script>document.getElementById('txtfirstname').classList.add('is-valid');</script>
                        </c:if>
                    </div>
                </div>

                <!-- Last Name -->
                <div class="form-group row">
                    <label for="txtlastname" class="col-md-2 col-form-label">Last Name</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control" placeholder="Enter Last Name..."  id="txtlastname" name="txtlastname" value="${param.txtlastname}" />
                        <c:if test="${not empty errLastName}">
                            <div class="invalid-feedback">${errLastName}</div>
                        </c:if>
                        <c:if test="${empty errLastName && not empty param.txtlastname}">
                            <script>document.getElementById('txtlastname').classList.add('is-valid');</script>
                        </c:if>
                    </div>
                </div>

                <!-- Email -->
                <div class="form-group row">
                    <label for="txtemail" class="col-md-2 col-form-label">Email</label>
                    <div class="col-md-10">
                        <input type="email" class="form-control ${not empty errEmail ? 'is-invalid' : ''}"placeholder="Enter Email..."   id="txtemail" name="txtemail" value="${param.txtemail}" />
                        <c:if test="${not empty errEmail}">
                            <div class="invalid-feedback">${errEmail}</div>
                        </c:if>
                        <c:if test="${empty errEmail && not empty param.txtemail}">
                            <script>document.getElementById('txtemail').classList.add('is-valid');</script>
                        </c:if>
                    </div>
                </div>

                <!-- Password -->
                <div class="form-group row">
                    <label for="txtpassword" class="col-md-2 col-form-label">Password</label>
                    <div class="col-md-10 position-relative">
                        <input type="password" class="form-control ${not empty errPassword ? 'is-invalid' : ''}" 
                               placeholder="Enter Password..."  id="txtpassword" name="txtpassword" value="${param.txtpassword}"/>
                        <span class="toggle-password position-absolute" onclick="togglePassword()" 
                              style="right: 20px; top: 50%; transform: translateY(-50%); cursor: pointer;">
                            <i class="fas fa-eye"></i>
                        </span>
                        <c:if test="${not empty errPassword}">
                            <div class="invalid-feedback">${errPassword}</div>
                        </c:if>
                        <c:if test="${not empty errPassword && not empty param.txtpassword}">
                            <script>document.getElementById('txtpassword').classList.add('is-valid');</script>
                        </c:if>
                    </div>
                </div>


                <!-- Phone -->
                <div class="form-group row">
                    <label for="txtphone" class="col-md-2 col-form-label">Phone</label>
                    <div class="col-md-10">
                        <input type="tel" class="form-control"placeholder="Enter Phone..."   id="txtphone" name="txtphone" value="${param.txtphone}" />
                        <c:if test="${not empty errPhone}">
                            <div class="invalid-feedback">${errPhone}</div>
                        </c:if>
                        <c:if test="${empty errPhone && not empty param.txtphone}">
                            <script>document.getElementById('txtphone').classList.add('is-valid');</script>
                        </c:if>
                    </div>
                </div>

                <!-- Address -->
                <div class="form-group row">
                    <label for="txtaddress" class="col-md-2 col-form-label">Address</label>
                    <div class="col-md-10">
                        <input type="text" class="form-control" placeholder="Enter Address..."  id="txtaddress" name="txtaddress" value="${param.txtaddress}"/>
                        <c:if test="${not empty errAddress}">
                            <div class="invalid-feedback">${errAddress}</div>
                        </c:if>
                        <c:if test="${empty errAddress && not empty param.txtaddress}">
                            <script>document.getElementById('txtaddress').classList.add('is-valid');</script>
                        </c:if>
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
                    }, false);

                    // Đánh dấu màu đỏ cho các trường có lỗi từ server
                    form.querySelectorAll('.form-control').forEach(function (input) {
                        if (input.nextElementSibling && input.nextElementSibling.classList.contains('invalid-feedback')) {
                            input.classList.add('is-invalid');
                        }
                    });
                });
            })();
            function togglePassword() {
                let passwordInput = document.getElementById("txtpassword");
                let icon = document.querySelector(".toggle-password i");

                if (passwordInput.type === "password") {
                    passwordInput.type = "text";
                    icon.classList.remove("fa-eye");
                    icon.classList.add("fa-eye-slash");
                } else {
                    passwordInput.type = "password";
                    icon.classList.remove("fa-eye-slash");
                    icon.classList.add("fa-eye");
                }
            }

        </script>
    </body>
</html>