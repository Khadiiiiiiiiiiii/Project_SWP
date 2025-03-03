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

@WebServlet(name = "updateStaffController", urlPatterns = {"/updateStaff"})
public class updateStaffController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = "updateStaff.jsp";
        Staff staff = null;

        try {
            String method = request.getMethod();
            if (method.equalsIgnoreCase("GET")) {
                // Lấy staffId từ query parameter
                String staffIdRaw = request.getParameter("staffId");
                System.out.println("updateStaffController: GET - staffIdRaw=" + staffIdRaw);

                if (staffIdRaw == null || staffIdRaw.trim().isEmpty()) {
                    System.out.println("updateStaffController: GET - Invalid staffIdRaw");
                    request.setAttribute("err", "Staff ID không hợp lệ. Vui lòng kiểm tra lại.");
                    url = "admin-dashboard.jsp";
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
                                System.out.println("updateStaffController: GET - Staff not found for staffId=" + staffId);
                                request.setAttribute("err", "Không tìm thấy nhân viên với ID: " + staffId);
                                url = "admin-dashboard.jsp";
                            } else {
                                System.out.println("updateStaffController: GET - Staff found: " + staff.getFirst_name() + " " + staff.getLast_name());
                                request.setAttribute("staff", staff);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("updateStaffController: GET - Invalid staffId format - " + e.getMessage());
                        request.setAttribute("err", "Staff ID không phải là một số hợp lệ.");
                        url = "admin-dashboard.jsp";
                    } catch (SQLException e) {
                        System.out.println("updateStaffController: GET - Database error - " + e.getMessage());
                        request.setAttribute("err", "Lỗi khi lấy thông tin nhân viên: " + e.getMessage());
                        url = "admin-dashboard.jsp";
                    }
                }
            } else if (method.equalsIgnoreCase("POST")) {
                // Lấy tham số từ form
                String staffIdRaw = request.getParameter("staffId");
                String firstName = request.getParameter("txtfirstname");
                String lastName = request.getParameter("txtlastname");
                String email = request.getParameter("txtemail");
                String password = request.getParameter("txtpassword");
                String phone = request.getParameter("txtphone");
                String address = request.getParameter("txtaddress");

                // Log chi tiết từng tham số
                System.out.println("updateStaffController: POST - Form data:");
                System.out.println("staffIdRaw=" + staffIdRaw);
                System.out.println("firstName=" + firstName);
                System.out.println("lastName=" + lastName);
                System.out.println("email=" + email);
                System.out.println("password=" + password);
                System.out.println("phone=" + phone);
                System.out.println("address=" + address);

                // Kiểm tra staffIdRaw trước
                if (staffIdRaw == null || staffIdRaw.trim().isEmpty()) {
                    System.out.println("updateStaffController: POST - Invalid input - staffIdRaw is null or empty");
                    request.setAttribute("err", "Staff ID không hợp lệ. Vui lòng kiểm tra lại.");
                    request.getRequestDispatcher(url).forward(request, response);
                    return;
                }

                int staffId;
                try {
                    staffId = Integer.parseInt(staffIdRaw);
                } catch (NumberFormatException e) {
                    System.out.println("updateStaffController: POST - Invalid staffId format - " + e.getMessage());
                    request.setAttribute("err", "Staff ID không phải là một số hợp lệ.");
                    request.getRequestDispatcher(url).forward(request, response);
                    return;
                }

                // Kiểm tra các trường bắt buộc khác
                if (firstName == null || firstName.trim().isEmpty() ||
                    lastName == null || lastName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    phone == null || phone.trim().isEmpty() ||
                    address == null || address.trim().isEmpty()) {
                    System.out.println("updateStaffController: POST - Invalid input - some required fields are empty");

                    // Lấy lại thông tin nhân viên để hiển thị form
                    try (Connection conn = DBContext.getConnection()) {
                        if (conn == null) {
                            throw new SQLException("Không thể kết nối đến database: Kết nối trả về null");
                        }
                        StaffDAO staffDAO = new StaffDAO(conn);
                        staff = staffDAO.getStaffById(staffId);
                        request.setAttribute("staff", staff);
                    } catch (SQLException e) {
                        System.out.println("updateStaffController: POST - Error fetching staff for retry - " + e.getMessage());
                    }

                    request.setAttribute("err", "Vui lòng nhập đầy đủ các trường bắt buộc.");
                    request.getRequestDispatcher(url).forward(request, response);
                    return;
                }

                try {
                    boolean updatePassword = password != null && !password.trim().isEmpty();

                    // Tạo đối tượng Staff để cập nhật
                    Staff staffUpdate = new Staff();
                    staffUpdate.setStaff_id(String.valueOf(staffId)); // Đảm bảo staff_id là String
                    staffUpdate.setEmail(email.trim());
                    staffUpdate.setPassword(updatePassword ? password.trim() : null);
                    staffUpdate.setFirst_name(firstName.trim());
                    staffUpdate.setLast_name(lastName.trim());
                    staffUpdate.setPhone(phone.trim());
                    staffUpdate.setAddress(address.trim());

                    // Gọi StaffDAO để cập nhật
                    try (Connection conn = DBContext.getConnection()) {
                        if (conn == null) {
                            throw new Exception("Không thể kết nối đến database: Kết nối trả về null");
                        }
                        System.out.println("updateStaffController: Database connection established");
                        StaffDAO staffDAO = new StaffDAO(conn);
                        staffDAO.updateStaff(staffUpdate, updatePassword);

                        // Redirect về danh sách sau khi cập nhật thành công
                        request.setAttribute("success", "Cập nhật thông tin nhân viên thành công!");
                        url = "admin";
                    } catch (SQLException e) {
                        System.out.println("updateStaffController: Error updating staff - " + e.getMessage());
                        e.printStackTrace();

                        // Lấy lại thông tin nhân viên để hiển thị form
                        try (Connection conn = DBContext.getConnection()) {
                            if (conn == null) {
                                throw new SQLException("Không thể kết nối đến database: Kết nối trả về null");
                            }
                            StaffDAO staffDAO = new StaffDAO(conn);
                            staff = staffDAO.getStaffById(staffId);
                            request.setAttribute("staff", staff);
                        } catch (SQLException ex) {
                            System.out.println("updateStaffController: Error fetching staff for retry - " + ex.getMessage());
                        }

                        request.setAttribute("err", "Lỗi khi cập nhật thông tin nhân viên: " + e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("updateStaffController: Invalid staffId - " + e.getMessage());
                    request.setAttribute("err", "Staff ID không hợp lệ.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error at updateStaffController: " + e.toString());
            e.printStackTrace();
            request.setAttribute("err", "Lỗi hệ thống: " + e.getMessage());
        } finally {
            System.out.println("updateStaffController: Forwarding to " + url);
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