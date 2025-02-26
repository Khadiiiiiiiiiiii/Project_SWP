package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Staff_ViewCustomerListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Staff_ViewCustomerListServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("Staff_ViewCustomerListServlet: doGet method called");

        try {
            StaffDAO staffDAO = new StaffDAO();
            List<User> customers = staffDAO.getAllCustomers();

            if (customers == null || customers.isEmpty()) {
                LOGGER.warning("No customers found!");
            } else {
                LOGGER.info("Total customers retrieved: " + customers.size());
            }

            request.setAttribute("customers", customers);
            request.getRequestDispatcher("./staff-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Staff_ViewCustomerListServlet: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving customer data");
        }
    }
}
