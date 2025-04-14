package com.mvc.controller;

import com.mvc.DAO.CustomerDAO;
import com.mvc.dal.DBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/LockCustomerServlet")
public class LockCustomerServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String action = request.getParameter("action");

        if (userIdStr == null || action == null) {
            response.sendRedirect("CustomerManagement.jsp?error=missing_params");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            Connection connection = DBContext.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);

            boolean success = false;
            if ("lock".equals(action)) {
                success = customerDAO.lockCustomer(userId);
            } else if ("unlock".equals(action)) {
                success = customerDAO.unlockCustomer(userId);
            }

            if (success) {
                response.sendRedirect("CustomerManagement.jsp?success=" + action + "_success");
            } else {
                response.sendRedirect("CustomerManagement.jsp?error=" + action + "_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("CustomerManagement.jsp?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("CustomerManagement.jsp?error=exception");
        }
    }
}
