package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.dal.DBContext;
import com.mvc.model.Staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "deleteStaffController", urlPatterns = {"/deleteStaff"})
public class deleteStaffController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String method = request.getMethod();
        
        if (method.equalsIgnoreCase("GET")) {
            // Lấy staffId từ query parameter
            String staffIdRaw = request.getParameter("staffId");
            System.out.println("deleteStaffController: GET - staffIdRaw=" + staffIdRaw);

            if (staffIdRaw == null || staffIdRaw.trim().isEmpty()) {
                System.out.println("deleteStaffController: GET - Invalid staffIdRaw");
                request.setAttribute("err", "Invalid!");
                request.getRequestDispatcher("admin").forward(request, response);
                return;
            }

            try {
                int staffId = Integer.parseInt(staffIdRaw);
                try (Connection conn = DBContext.getConnection()) {
                    StaffDAO staffDAO = new StaffDAO(conn);
                    Staff staff = staffDAO.getStaffById(staffId);

                    if (staff == null) {
                        System.out.println("deleteStaffController: GET - Staff not found for staffId=" + staffId);
                        request.setAttribute("err", "Not found with ID: " + staffId);
                        request.getRequestDispatcher("admin").forward(request, response);
                    } else {
                        System.out.println("deleteStaffController: GET - Staff found: " + staff.getFirst_name() + " " + staff.getLast_name());
                        request.setAttribute("staff", staff);
                        request.getRequestDispatcher("deleteStaff.jsp").forward(request, response);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("deleteStaffController: GET - Invalid staffId format - " + e.getMessage());
                request.setAttribute("err", "Invalid ID format!");
                request.getRequestDispatcher("admin").forward(request, response);
            } catch (SQLException e) {
                System.out.println("deleteStaffController: GET - Database error - " + e.getMessage());
                request.setAttribute("err", "Database error: " + e.getMessage());
                request.getRequestDispatcher("admin").forward(request, response);
            }
        } 
        
        else if (method.equalsIgnoreCase("POST")) {
            // Lấy staffId từ form
            String staffIdRaw = request.getParameter("staffId");
            System.out.println("deleteStaffController: POST - staffIdRaw=" + staffIdRaw);

            if (staffIdRaw == null || staffIdRaw.trim().isEmpty()) {
                System.out.println("deleteStaffController: POST - Invalid input - staffIdRaw is null or empty");
                request.setAttribute("err", "Invalid!");
                request.getRequestDispatcher("deleteStaff.jsp").forward(request, response);
                return;
            }

            int staffId;
            try {
                staffId = Integer.parseInt(staffIdRaw);
            } catch (NumberFormatException e) {
                System.out.println("deleteStaffController: POST - Invalid staffId format - " + e.getMessage());
                request.setAttribute("err", "Invalid ID format!");
                request.getRequestDispatcher("deleteStaff.jsp").forward(request, response);
                return;
            }

            try (Connection conn = DBContext.getConnection()) {
                StaffDAO staffDAO = new StaffDAO(conn);
                staffDAO.deleteStaff(staffId);
                // Redirect về trang admin
                response.sendRedirect("admin");
            } catch (SQLException e) {
                System.out.println("deleteStaffController: POST - Error deleting staff - " + e.getMessage());
                request.setAttribute("err", "Database error: " + e.getMessage());
                request.getRequestDispatcher("deleteStaff.jsp").forward(request, response);
            }
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
