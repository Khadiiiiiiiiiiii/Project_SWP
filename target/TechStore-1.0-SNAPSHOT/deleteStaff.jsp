<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Delete Staff</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4 body-content">
        <h2>Delete Staff</h2>

        <!-- Thông báo xác nhận xuất hiện ngay dưới tiêu đề -->
        <h3>Are you sure you want to delete this staff member?</h3>

        <div class="form-horizontal">
            <hr />

            <c:if test="${not empty err}">
                <p class="text-danger">${err}</p>
            </c:if>
            <c:if test="${not empty staff}">
                <form action="deleteStaff" method="POST" id="staffForm">
                    <!-- Hidden Field for Staff ID -->
                    <input type="hidden" name="staffId" value="${staff.staff_id}">

                    <!-- Staff ID (Disabled) -->
                    <div class="form-group row mb-3">
                        <label for="txtStaffId" class="control-label col-md-2 text-end">Staff ID</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control" id="txtStaffId" value="${staff.staff_id}" disabled>
                        </div>
                    </div>

                    <!-- First Name (Disabled) -->
                    <div class="form-group row mb-3">
                        <label for="txtFirstName" class="control-label col-md-2 text-end">First Name</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control" id="txtFirstName" value="${staff.first_name}" disabled>
                        </div>
                    </div>

                    <!-- Last Name (Disabled) -->
                    <div class="form-group row mb-3">
                        <label for="txtLastName" class="control-label col-md-2 text-end">Last Name</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control" id="txtLastName" value="${staff.last_name}" disabled>
                        </div>
                    </div>

                    <!-- Email (Disabled) -->
                    <div class="form-group row mb-3">
                        <label for="txtEmail" class="control-label col-md-2 text-end">Email</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control" id="txtEmail" value="${staff.email}" disabled>
                        </div>
                    </div>

                    <!-- Phone (Disabled) -->
                    <div class="form-group row mb-3">
                        <label for="txtPhone" class="control-label col-md-2 text-end">Phone</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control" id="txtPhone" value="${staff.phone}" disabled>
                        </div>
                    </div>

                    <!-- Address (Disabled) -->
                    <div class="form-group row mb-3">
                        <label for="txtAddress" class="control-label col-md-2 text-end">Address</label>
                        <div class="col-md-10">
                            <input type="text" class="form-control" id="txtAddress" value="${staff.address}" disabled>
                        </div>
                    </div>

                    <!-- Delete Button to submit the form -->
                    <div class="form-group row mb-3">
                        <div class="col-md-10 offset-md-2">
                            <button type="submit" name="btnDelete" class="btn btn-danger">Delete</button>
                            <a href="admin" class="btn btn-secondary">Back to list</a>
                        </div>
                    </div>
                </form>
            </c:if>
            <c:if test="${empty staff}">
                <p class="text-danger">Staff information not found. Please check Staff ID again.</p>
            </c:if>
        </div>
    </div>
</body>
</html>