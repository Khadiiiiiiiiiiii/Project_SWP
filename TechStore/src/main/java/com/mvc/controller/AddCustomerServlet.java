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

@WebServlet("/AddCustomerServlet")
public class AddCustomerServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            Integer loyaltyPoints = request.getParameter("loyaltyPoints") != null && !request.getParameter("loyaltyPoints").isEmpty()
                    ? Integer.parseInt(request.getParameter("loyaltyPoints")) : 0;
            String preferredPaymentMethod = request.getParameter("preferredPaymentMethod");

            Customer customer = new Customer();
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setLoyaltyPoints(loyaltyPoints);
            customer.setPreferredPaymentMethod(preferredPaymentMethod);

            Connection connection = DBContext.getConnection();
            StaffDAO staffDAO = new StaffDAO(connection);
            boolean added = staffDAO.addCustomer(customer, password);

            if (added) {
                response.sendRedirect("staffDashboard.jsp?success=added");
            } else {
                response.sendRedirect("staffDashboard.jsp?error=add_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("staffDashboard.jsp?error=exception");
        }
    }
}
