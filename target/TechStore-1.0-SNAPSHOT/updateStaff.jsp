<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Staff</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="CSS/updateStaff.css">
    </head>
    <body>
        <div class="container mt-4 body-content">
            <h2>Update Staff</h2>
            <div class="form-horizontal">
                <hr />
                <c:if test="${not empty err}">
                    <p class="text-danger">${err}</p>
                </c:if>
                <c:if test="${not empty success}">
                    <p class="text-success">${success}</p>
                </c:if>
                <c:if test="${empty staff}">
                    <p class="text-danger">Không tìm thấy thông tin nhân viên. Vui lòng kiểm tra lại Staff ID.</p>
                </c:if>
                <c:if test="${not empty staff}">
                    <form action="updateStaff" method="POST" id="staffForm" class="needs-validation form-horizontal" novalidate>
                        <!-- Hidden field để lưu staff_id -->
                        <input type="hidden" name="staffId" value="${staff.staff_id}" />

                        <!-- First Name -->
                        <div class="form-group row">
                            <label for="txtfirstname" class="col-md-2 col-form-label">First Name</label>
                            <div class="col-md-10">
                                <input type="text" class="form-control" id="txtfirstname" name="txtfirstname" 
                                       value="${not empty param.txtfirstname ? param.txtfirstname : staff.first_name}" required />
                                <c:if test="${not empty errFirstName}">
                                    <div class="text-danger">${errFirstName}</div>
                                </c:if>
                                <div class="invalid-feedback">Please enter the first name.</div>
                            </div>
                        </div>

                        <!-- Last Name -->
                        <div class="form-group row">
                            <label for="txtlastname" class="col-md-2 col-form-label">Last Name</label>
                            <div class="col-md-10">
                                <input type="text" class="form-control" id="txtlastname" name="txtlastname" 
                                       value="${not empty param.txtlastname ? param.txtlastname : staff.last_name}" required />
                                <c:if test="${not empty errLastName}">
                                    <div class="text-danger">${errLastName}</div>
                                </c:if>
                                <div class="invalid-feedback">Please enter the last name.</div>
                            </div>
                        </div>

                        <!-- Email -->
                        <div class="form-group row">
                            <label for="txtemail" class="col-md-2 col-form-label">Email</label>
                            <div class="col-md-10">
                                <input type="email" class="form-control" id="txtemail" name="txtemail" 
                                       value="${not empty param.txtemail ? param.txtemail : staff.email}" required />
                                <c:if test="${not empty errEmail}">
                                    <div class="text-danger">${errEmail}</div>
                                </c:if>
                                <div class="invalid-feedback">Please enter a valid email address.</div>
                            </div>
                        </div>

                        <!-- Password -->
                        <div class="form-group row">
                            <label for="txtpassword" class="col-md-2 col-form-label">Password</label>
                            <div class="col-md-10 position-relative">
                                <input type="password" class="form-control" id="txtpassword" name="txtpassword" 
                                       placeholder="Leave blank to keep current password" />
                                <span class="toggle-password position-absolute" onclick="togglePassword()" 
                                      style="right: 20px; top: 30%; transform: translateY(-50%); cursor: pointer;">
                                    <i class="fas fa-eye"></i>
                                </span>
                                <c:if test="${not empty errPassword}">
                                    <div class="text-danger">${errPassword}</div>
                                </c:if>
                                <small class="form-text text-muted">Leave blank if you don't want to change the password.</small>
                            </div>
                        </div>

                        <!-- Phone -->
                        <div class="form-group row">
                            <label for="txtphone" class="col-md-2 col-form-label">Phone</label>
                            <div class="col-md-10">
                                <input type="tel" class="form-control" id="txtphone" name="txtphone" 
                                       value="${not empty param.txtphone ? param.txtphone : staff.phone}" required />
                                <c:if test="${not empty errPhone}">
                                    <div class="text-danger">${errPhone}</div>
                                </c:if>
                                <div class="invalid-feedback">Please enter a phone number.</div>
                            </div>
                        </div>

                        <!-- Address -->
                        <div class="form-group row">
                            <label for="txtaddress" class="col-md-2 col-form-label">Address</label>
                            <div class="col-md-10">
                                <input type="text" class="form-control" id="txtaddress" name="txtaddress" 
                                       value="${not empty param.txtaddress ? param.txtaddress : staff.address}" required />
                                <c:if test="${not empty errAddress}">
                                    <div class="text-danger">${errAddress}</div>
                                </c:if>
                                <div class="invalid-feedback">Please enter an address.</div>
                            </div>
                        </div>

                        <!-- Submit and Back Buttons -->
                        <div class="form-group row mb-3">
                            <div class="col-md-10 offset-md-2">
                                <button type="submit" name="btnSave" class="btn btn-primary">Save</button>
                                <a href="admin" class="btn btn-danger">Back to list</a>
                            </div>
                        </div>
                    </form>
                </c:if>
            </div>
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

            function togglePassword() {
                var passwordInput = document.getElementById("txtpassword");
                var icon = document.querySelector(".toggle-password i");

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