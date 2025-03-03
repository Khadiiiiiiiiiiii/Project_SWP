package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.dal.DBContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

public class DeleteCustomerServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect("staffDashboard.jsp?error=missing_id");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            Connection connection = DBContext.getConnection();
            StaffDAO staffDAO = new StaffDAO(connection);

            boolean deleted = staffDAO.deleteCustomer(userId);

            if (deleted) {
                response.sendRedirect("staffDashboard.jsp?success=deleted");
            } else {
                response.sendRedirect("staffDashboard.jsp?error=delete_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("staffDashboard.jsp?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("staffDashboard.jsp?error=exception");
        }
    }
}
