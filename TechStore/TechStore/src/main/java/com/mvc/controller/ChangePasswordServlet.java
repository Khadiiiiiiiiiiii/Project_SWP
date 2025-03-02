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

public class ChangePasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");

        try {
            if (!userDAO.validatePassword(user.getEmail(), oldPassword)) {
                request.setAttribute("errorMessage", "Old password is incorrect.");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                request.setAttribute("errorMessage", "New passwords do not match.");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                return;
            }

            boolean isUpdated = userDAO.updatePassword(user.getEmail(), newPassword);
            if (isUpdated) {
                User updatedUser = userDAO.getUserByEmail(user.getEmail());
                session.setAttribute("loggedUser", updatedUser);

                request.setAttribute("successMessage", "Password updated successfully!");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Failed to update password. Please try again.");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unexpected error: " + e.getMessage());
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
        }
    }
}
