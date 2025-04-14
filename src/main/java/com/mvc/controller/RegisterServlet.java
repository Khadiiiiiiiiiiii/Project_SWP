package com.mvc.controller;

import com.mvc.DAO.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Kiểm tra dữ liệu đầu vào
        String errorMessage = validateInput(email, password, confirmPassword, firstName, lastName, phone, address); // Cập nhật tham số
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();

        try {
            // Kiểm tra xem số điện thoại đã tồn tại chưa
            if (userDAO.isPhoneExist(phone)) {
                request.setAttribute("errorMessage", "Phone number already exists. Please use a different phone number.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            boolean isRegistered = userDAO.register(email, password, firstName, lastName, phone, address);

            if (isRegistered) {
                request.setAttribute("successMessage", "Registration successful! Please go back to login.");
            } else {
                request.setAttribute("errorMessage", "Registration failed. Email may already exist.");
            }
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error occurred. Please try again later.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    // ✅ Phương thức kiểm tra dữ liệu đầu vào
    private String validateInput(String email, String password, String confirmPassword, String firstName, String lastName, String phone, String address) {
        // Kiểm tra các trường không được để trống hoặc chỉ chứa dấu cách
        if (isEmptyOrOnlySpaces(email)) {
            return "Email cannot be empty or contain only spaces.";
        }
        if (isEmptyOrOnlySpaces(password)) {
            return "Password cannot be empty or contain only spaces.";
        }
        if (isEmptyOrOnlySpaces(confirmPassword)) {
            return "Confirm Password cannot be empty or contain only spaces.";
        }
        if (isEmptyOrOnlySpaces(firstName)) {
            return "First Name cannot be empty or contain only spaces.";
        }
        if (isEmptyOrOnlySpaces(lastName)) {
            return "Last Name cannot be empty or contain only spaces.";
        }
        if (isEmptyOrOnlySpaces(phone)) {
            return "Phone number cannot be empty or contain only spaces.";
        }
        if (isEmptyOrOnlySpaces(address)) {
            return "Address cannot be empty or contain only spaces.";
        }

        // Kiểm tra định dạng email
        if (!isValidEmail(email)) {
            return "Invalid email format. Please enter a valid email.";
        }

        // Kiểm tra mật khẩu
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters and contain at least one letter.";
        }

        // Kiểm tra password và confirmPassword trùng nhau
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match. Please ensure both passwords are the same.";
        }

        // Kiểm tra số điện thoại
        if (!isValidPhone(phone)) {
            return "Phone number must be exactly 10 digits and contain only numbers.";
        }

        return null; // Không có lỗi
    }

    // ✅ Kiểm tra trường có trống hoặc chỉ chứa dấu cách không
    private boolean isEmptyOrOnlySpaces(String input) {
        if (input == null) {
            return true;
        }
        return input.trim().isEmpty();
    }

    // ✅ Kiểm tra định dạng email hợp lệ
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    // ✅ Kiểm tra mật khẩu hợp lệ (tối thiểu 6 ký tự, ít nhất 1 chữ cái)
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z]).{6,}$";
        return Pattern.matches(passwordRegex, password);
    }

    // ✅ Kiểm tra số điện thoại hợp lệ (chỉ chứa 10 số)
    private boolean isValidPhone(String phone) {
        String phoneRegex = "^[0-9]{10}$";
        return Pattern.matches(phoneRegex, phone);
    }
}
