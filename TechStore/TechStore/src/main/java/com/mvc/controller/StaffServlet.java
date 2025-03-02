package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.dal.DBContext;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

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
        StaffDAO staffDAO = new StaffDAO(connection);
        List<User> customers = staffDAO.getAllCustomers();
        
        request.setAttribute("customers", customers);
        request.getRequestDispatcher("staffDashboard.jsp").forward(request, response);
    }
}
