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

        String fullName = request.getParameter("name").trim();
        String phone = request.getParameter("phone").trim();
        String address = request.getParameter("address").trim();

        request.setAttribute("name", fullName);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);

        if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required!");
            request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
            return;
        }

        String phoneRegex = "^[0-9]{10}$";
        if (!Pattern.matches(phoneRegex, phone)) {
            request.setAttribute("errorMessage", "Phone number must be exactly 10 digits!");
            request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
            return;
        }

        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        UserDAO userDAO = new UserDAO();
        try {
            boolean success = userDAO.updateProfile(user.getUserId(), firstName, lastName, phone, address);

            if (success) {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPhone(phone);
                user.setAddress(address);
                session.setAttribute("user", user);

                request.setAttribute("successMessage", "Profile updated successfully!");
                request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Update failed. Please try again.");
                request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Server error. Please contact support.");
            request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
        }
    }

}
