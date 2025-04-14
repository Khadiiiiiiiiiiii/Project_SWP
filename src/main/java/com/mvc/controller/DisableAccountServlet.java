package com.mvc.controller;

import com.mvc.DAO.UserDAO;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/DisableAccountServlet")
public class DisableAccountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();
        if (userId <= 0) {
            response.sendRedirect("viewProfile.jsp?error=invalid_user");
            return;
        }

        System.out.println("Attempting to disable userId: " + userId);

        UserDAO userDAO = new UserDAO();
        try {
            boolean success = userDAO.disableUser(userId);

            if (success) {
                session.invalidate();
                System.out.println("User disabled successfully, redirecting to Home.jsp");
                response.sendRedirect("Home.jsp?disabled=true");
            } else {
                System.out.println("Failed to disable userId: " + userId);
                response.sendRedirect("viewProfile.jsp?error=disable_failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQLException during disable: " + e.getMessage());
            String errorMessage = e.getMessage().contains("foreign key constraint")
                    ? "delete_constraint_failed" : "server_error";
            response.sendRedirect("viewProfile.jsp?error=" + errorMessage);
        }

    }
}
