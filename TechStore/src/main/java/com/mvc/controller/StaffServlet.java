package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.dal.DBContext;
import com.mvc.model.Customer;
import com.mvc.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/StaffServlet")
public class StaffServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        connection = DBContext.getConnection();
        if (connection == null) {
            throw new ServletException("Không thể kết nối đến cơ sở dữ liệu!");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StaffDAO staffDAO = new StaffDAO(connection);
            List<Customer> customers = staffDAO.getAllCustomers();
            request.setAttribute("customers", customers);
            RequestDispatcher dispatcher = request.getRequestDispatcher("staffDashboard.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(StaffServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
