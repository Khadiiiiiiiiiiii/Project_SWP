package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.dal.DBContext;
import com.mvc.model.Staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "deleteStaffController", urlPatterns = {"/deleteStaff"})
public class deleteStaffController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = "deleteStaff.jsp";
        Staff staff = null;

        try {
            String method = request.getMethod();
            if (method.equalsIgnoreCase("GET")) {
                // Lấy staffId từ query parameter
                String staffIdRaw = request.getParameter("staffId");
                System.out.println("deleteStaffController: GET - staffIdRaw=" + staffIdRaw);

                if (staffIdRaw == null || staffIdRaw.trim().isEmpty()) {
                    System.out.println("deleteStaffController: GET - Invalid staffIdRaw");
                    request.setAttribute("err", "Staff ID không hợp lệ. Vui lòng kiểm tra lại.");
                    url = "admin";
                } else {
                    try {
                        int staffId = Integer.parseInt(staffIdRaw);
                        try (Connection conn = DBContext.getConnection()) {
                            if (conn == null) {
                                throw new SQLException("Không thể kết nối đến database: Kết nối trả về null");
                            }
                            StaffDAO staffDAO = new StaffDAO(conn);
                            staff = staffDAO.getStaffById(staffId);
                            if (staff == null) {
                                System.out.println("deleteStaffController: GET - Staff not found for staffId=" + staffId);
                                request.setAttribute("err", "Không tìm thấy nhân viên với ID: " + staffId);
                                url = "admin";
                            } else {
                                System.out.println("deleteStaffController: GET - Staff found: " + staff.getFirst_name() + " " + staff.getLast_name());
                                request.setAttribute("staff", staff);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("deleteStaffController: GET - Invalid staffId format - " + e.getMessage());
                        request.setAttribute("err", "Staff ID không phải là một số hợp lệ.");
                        url = "admin";
                    } catch (SQLException e) {
                        System.out.println("deleteStaffController: GET - Database error - " + e.getMessage());
                        request.setAttribute("err", "Lỗi khi lấy thông tin nhân viên: " + e.getMessage());
                        url = "admin";
                    }
                }
            } else if (method.equalsIgnoreCase("POST")) {
                // Lấy staffId từ form
                String staffIdRaw = request.getParameter("staffId");
                System.out.println("deleteStaffController: POST - staffIdRaw=" + staffIdRaw);

                if (staffIdRaw == null || staffIdRaw.trim().isEmpty()) {
                    System.out.println("deleteStaffController: POST - Invalid input - staffIdRaw is null or empty");
                    request.setAttribute("err", "Staff ID không hợp lệ. Vui lòng kiểm tra lại.");
                    request.getRequestDispatcher(url).forward(request, response);
                    return;
                }

                int staffId;
                try {
                    staffId = Integer.parseInt(staffIdRaw);
                } catch (NumberFormatException e) {
                    System.out.println("deleteStaffController: POST - Invalid staffId format - " + e.getMessage());
                    request.setAttribute("err", "Staff ID không phải là một số hợp lệ.");
                    request.getRequestDispatcher(url).forward(request, response);
                    return;
                }

                try (Connection conn = DBContext.getConnection()) {
                    if (conn == null) {
                        throw new SQLException("Không thể kết nối đến database: Kết nối trả về null");
                    }
                    StaffDAO staffDAO = new StaffDAO(conn);
                    staffDAO.deleteStaff(staffId);

                    // Redirect về danh sách sau khi xóa thành công
                    request.setAttribute("success", "Xóa nhân viên thành công!");
                    url = "admin";
                } catch (SQLException e) {
                    System.out.println("deleteStaffController: POST - Error deleting staff - " + e.getMessage());
                    e.printStackTrace();
                    request.setAttribute("err", "Lỗi khi xóa nhân viên: " + e.getMessage());

                    // Lấy lại thông tin nhân viên để hiển thị form
                    try (Connection conn = DBContext.getConnection()) {
                        if (conn == null) {
                            throw new SQLException("Không thể kết nối đến database: Kết nối trả về null");
                        }
                        StaffDAO staffDAO = new StaffDAO(conn);
                        staff = staffDAO.getStaffById(staffId);
                        request.setAttribute("staff", staff);
                    } catch (SQLException ex) {
                        System.out.println("deleteStaffController: POST - Error fetching staff for retry - " + ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error at deleteStaffController: " + e.toString());
            e.printStackTrace();
            request.setAttribute("err", "Lỗi hệ thống: " + e.getMessage());
        } finally {
            System.out.println("deleteStaffController: Forwarding to " + url);
            request.getRequestDispatcher(url).forward(request, response);
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