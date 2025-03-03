package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.dal.DBContext;
import com.mvc.model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/UpdateCustomerServlet")
public class UpdateCustomerServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect("staffDashboard.jsp?error=missing_id");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            String email = request.getParameter("email");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            Integer loyaltyPoints = Integer.parseInt(request.getParameter("loyaltyPoints"));
            String preferredPaymentMethod = request.getParameter("preferredPaymentMethod");

            Customer customer = new Customer();
            customer.setUserId(userId);
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setLoyaltyPoints(loyaltyPoints);
            customer.setPreferredPaymentMethod(preferredPaymentMethod);

            Connection connection = DBContext.getConnection();
            StaffDAO staffDAO = new StaffDAO(connection);
            boolean updated = staffDAO.updateCustomer(customer);

            if (updated) {
                response.sendRedirect("staffDashboard.jsp?success=updated");
            } else {
                response.sendRedirect("staffDashboard.jsp?error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("staffDashboard.jsp?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("staffDashboard.jsp?error=exception");
        }
    }
}
