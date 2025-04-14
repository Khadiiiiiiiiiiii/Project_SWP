package com.mvc.controller;

import com.mvc.DAO.UserDAO;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UpdateProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy dữ liệu từ form
        String fullName = request.getParameter("name") != null ? request.getParameter("name").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim() : "";
        String address = request.getParameter("address") != null ? request.getParameter("address").trim() : "";
        String paymentMethod = request.getParameter("paymentMethod") != null ? request.getParameter("paymentMethod").trim() : "";

        // Lưu dữ liệu vào request attribute để giữ lại giá trị khi forward
        request.setAttribute("name", fullName);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);
        request.setAttribute("paymentMethod", paymentMethod);

        List<String> errors = new ArrayList<>();

        // Kiểm tra các trường bắt buộc
        if (fullName == null || fullName.trim().isEmpty() || fullName.matches("^\\s+$")) {
            errors.add("Full name cannot be empty or contain only spaces.");
        }

        if (phone == null || phone.trim().isEmpty() || phone.matches("^\\s+$")) {
            errors.add("Phone number cannot be empty or contain only spaces.");
        } else if (!Pattern.matches("^[0-9]{10}$", phone)) {
            errors.add("Phone number must be exactly 10 digits.");
        }

        if (address == null || address.trim().isEmpty() || address.matches("^\\s+$")) {
            errors.add("Address cannot be empty or contain only spaces.");
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty() || paymentMethod.matches("^\\s+$")) {
            errors.add("Payment method cannot be empty or contain only spaces.");
        }

        // Tách firstName và lastName từ fullName
        String firstName = "";
        String lastName = "";
        if (!fullName.isEmpty() && !fullName.matches("^\\s+$")) {
            String[] nameParts = fullName.split(" ", 2);
            firstName = nameParts.length > 0 ? nameParts[0] : "";
            lastName = nameParts.length > 1 ? nameParts[1] : "";
            if (firstName.isEmpty() || lastName.isEmpty()) {
                errors.add("Please enter both first name and last name!");
            }
        }

        // Nếu có lỗi, forward về editProfile.jsp với thông báo lỗi
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("editProfile.jsp").forward(request, response);
            return;
        }

        // Kiểm tra số điện thoại đã tồn tại chưa (loại trừ chính người dùng hiện tại)
        UserDAO userDAO = new UserDAO();
        try {
            if (!phone.equals(user.getPhone() != null ? user.getPhone() : "")) {
                if (userDAO.isPhoneExistForOtherUsers(phone, user.getUserId())) {
                    errors.add("Phone number already exists. Please use a different phone number!");
                    request.setAttribute("errors", errors);
                    request.getRequestDispatcher("editProfile.jsp").forward(request, response);
                    return;
                }
            }

            // Cập nhật thông tin người dùng
            boolean success = userDAO.updateProfile(user.getUserId(), firstName, lastName, phone, address, paymentMethod);

            if (success) {
                // Cập nhật thông tin trong session
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPhone(phone);
                user.setAddress(address);
                session.setAttribute("user", user);

                request.setAttribute("successMessage", "Profile updated successfully!");
            } else {
                errors.add("Update failed. Please try again.");
                request.setAttribute("errors", errors);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorDetails = e.getMessage() + " (SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode() + ")";
            System.err.println("SQLException Details: " + errorDetails);
            errors.add("Server error. Please contact support.");
            request.setAttribute("errors", errors);
            request.setAttribute("errorDetails", errorDetails);
        }

        // Forward lại về editProfile.jsp để hiển thị thông báo
        request.getRequestDispatcher("editProfile.jsp").forward(request, response);
    }
}
