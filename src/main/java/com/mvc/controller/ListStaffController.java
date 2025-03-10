package com.mvc.controller;

import com.mvc.dal.DBContext;
import com.mvc.model.Staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ListStaffController", urlPatterns = {"/admin"})
public class ListStaffController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Connection conn = DBContext.getConnection();
            String sql = "SELECT s.staff_id, u.first_name, u.last_name, u.email, u.phone, u.address, s.role, s.hired_date FROM Staff s JOIN Users u ON s.user_id = u.user_id;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            List<Staff> list = new ArrayList<>();
            while (rs.next()) {
                Staff staff = new Staff();
                staff.setStaff_id(rs.getString("staff_id"));
                staff.setFirst_name(rs.getString("first_name"));
                staff.setLast_name(rs.getString("last_name"));
                staff.setEmail(rs.getString("email"));
                staff.setPhone(rs.getString("phone"));
                staff.setAddress(rs.getString("address"));
                staff.setRole(rs.getString("role"));
                staff.setHired_date(rs.getDate("hired_date")); 
                list.add(staff);
            }
            rs.close();
            stmt.close();
            conn.close();
            request.setAttribute("staffList", list);
            request.getRequestDispatcher("admin-dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            log("Error at ListStaffController: " + e.toString());
            request.setAttribute("err", "An error occurred while loading the staff list: " + e.getMessage());
            request.getRequestDispatcher("admin-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}