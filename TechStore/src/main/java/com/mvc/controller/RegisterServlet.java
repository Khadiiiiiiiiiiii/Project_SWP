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
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Kiểm tra định dạng email
        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Invalid email format. Please enter a valid email.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Kiểm tra số điện thoại (chỉ chứa số và đúng 10 số)
        if (!isValidPhone(phone)) {
            request.setAttribute("errorMessage", "Phone number must be exactly 10 digits and contain only numbers.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();

        try {
            boolean isRegistered = userDAO.register(email, password, firstName, lastName, phone, address);

            if (isRegistered) {
                request.setAttribute("successMessage", "Registration successful! Please go back to login.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Registration failed. Email may already exist.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error occurred.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    // ✅ Kiểm tra định dạng email hợp lệ
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    // ✅ Kiểm tra số điện thoại hợp lệ (chỉ chứa 10 số)
    private boolean isValidPhone(String phone) {
        String phoneRegex = "^[0-9]{10}$";  // Chỉ chứa 10 chữ số
        return Pattern.matches(phoneRegex, phone);
    }
}
