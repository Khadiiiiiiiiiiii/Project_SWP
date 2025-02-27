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

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate email format
        if (email == null || !email.contains("@")) {
            request.setAttribute("errorMessage", "Invalid email format! Please enter a valid email.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findUserByEmail(email); // Check if the account exists

            if (user == null) {
                // Show error message if account does not exist
                request.setAttribute("errorMessage", "Account does not exist! Please register if you don't have an account.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else if (!userDAO.validatePassword(email, password)) {
                request.setAttribute("errorMessage", "Incorrect password! Please try again.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                // Successful login
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // Redirect based on role
                if ("Admin".equalsIgnoreCase(user.getRole()) || "admin".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect("admin-dashboard.jsp");
                } else if ("staff".equalsIgnoreCase(user.getRole()) || "Staff".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect("staffDashboard.jsp");
                } else if ("sm".equalsIgnoreCase(user.getRole()) || "SM".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect("sm-dashboard.jsp");
                } else {
                    response.sendRedirect("Home.jsp");
                }
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            request.setAttribute("errorMessage", "System error! Please try again later.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
