package com.mvc.controller;

import com.mvc.DAO.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ForgotPasswordServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Kiểm tra định dạng email hợp lệ
        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Invalid email format. Please enter a valid email.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        // Kiểm tra mật khẩu nhập lại có giống không
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match. Please try again.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        // Kiểm tra độ mạnh của mật khẩu
        if (!isValidPassword(newPassword)) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters long and contain at least one letter.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        try {
            // Kiểm tra email có tồn tại không
            if (!userDAO.isEmailExist(email)) {
                request.setAttribute("errorMessage", "Email does not exist in our system.");
                request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
                return;
            }

            // Cập nhật mật khẩu nếu tài khoản không bị vô hiệu hóa
            boolean updated = userDAO.updatePasswordByEmail(email, newPassword);
            if (updated) {
                request.getSession().setAttribute("successMessage", "Password has been reset successfully. You can now log in.");
                response.sendRedirect("forgotPassword.jsp");
            } else {
                request.setAttribute("errorMessage", "Your account is disabled. Password reset is not allowed.");
                request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

// Hàm kiểm tra mật khẩu hợp lệ (ít nhất 6 ký tự, chứa ít nhất 1 chữ cái)
    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[a-zA-Z].*");
    }

}
