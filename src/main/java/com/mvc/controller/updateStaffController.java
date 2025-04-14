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
                // Xử lý GET request (giữ nguyên như cũ)
                String staffIdRaw = request.getParameter("staffId");
                System.out.println("updateStaffController: GET - staffIdRaw=" + staffIdRaw);

                if (staffIdRaw == null || staffIdRaw.trim().isEmpty()) {
                    request.setAttribute("err", "Invalid Staff ID.");
                    url = "admin-dashboard.jsp";
                } else {
                    try {
                        int staffId = Integer.parseInt(staffIdRaw);
                        try (Connection conn = DBContext.getConnection()) {
                            StaffDAO staffDAO = new StaffDAO(conn);
                            staff = staffDAO.getStaffById(staffId);
                            if (staff == null) {
                                request.setAttribute("err", "Staff not found with ID: " + staffId);
                                url = "admin-dashboard.jsp";
                            } else {
                                request.setAttribute("staff", staff);
                            }
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("err", "Staff ID must be a valid number.");
                        url = "admin-dashboard.jsp";
                    } catch (SQLException e) {
                        request.setAttribute("err", "Database error: " + e.getMessage());
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

                // Log thông tin
                System.out.println("updateStaffController: POST - Form data:");
                System.out.println("staffIdRaw=" + staffIdRaw);
                System.out.println("firstName=" + firstName);
                System.out.println("lastName=" + lastName);
                System.out.println("email=" + email);
                System.out.println("password=" + password);
                System.out.println("phone=" + phone);
                System.out.println("address=" + address);

                // Kiểm tra staffId
                int staffId;
                try {
                    staffId = Integer.parseInt(staffIdRaw);
                } catch (NumberFormatException e) {
                    request.setAttribute("err", "Invalid Staff ID format.");
                    request.getRequestDispatcher(url).forward(request, response);
                    return;
                }

                // Biến để theo dõi lỗi
                boolean hasError = false;

                // Kiểm tra các trường
                if (firstName == null || !firstName.matches("^(?!.*\\s{2})[a-zA-Z\\s]{3,50}$")) {
                    request.setAttribute("errFirstName", "Invalid! First name must be between 3 and 50 characters long and only contain letters.");
                    hasError = true;
                }

                if (lastName == null || !lastName.matches("^(?!.*\\s{2})[a-zA-Z\\s]{3,50}$")) {
                    request.setAttribute("errLastName", "Invalid! Last name must be between 3 and 50 characters long and only contain letters.");
                    hasError = true;
                }

                if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")) {
                    request.setAttribute("errEmail", "Invalid email format.");
                    hasError = true;
                }

                if (password != null && !password.isEmpty() && !password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!]{6,}$")) {
                    request.setAttribute("errPassword", "Invalid! Password must be at least 6 characters, including at least one letter and one number.");
                    hasError = true;
                }

                if (phone == null || !phone.matches("^0\\d{9}$")) {
                    request.setAttribute("errPhone", "Invalid! Phone number must be 10 digits and start with '0'.");
                    hasError = true;
                }

                if (address == null || address.length() < 6 || address.length() > 200) {
                    request.setAttribute("errAddress", "Invalid! Address must be between 6 and 200 characters.");
                    hasError = true;
                }

                // Kiểm tra email và phone tồn tại
                try (Connection conn = DBContext.getConnection()) {
                    StaffDAO staffDAO = new StaffDAO(conn);
                    Staff currentStaff = staffDAO.getStaffById(staffId);

                    // Kiểm tra email nếu thay đổi
                    if (email != null && !email.equals(currentStaff.getEmail())) {
                        if (staffDAO.isEmailExists(email)) {
                            request.setAttribute("errEmail", "Email already exists.");
                            hasError = true;
                        }
                    }

                    // Kiểm tra số điện thoại nếu thay đổi
                    if (phone != null && !phone.equals(currentStaff.getPhone())) {
                        if (staffDAO.isPhoneExists(phone)) {
                            request.setAttribute("errPhone", "Phone number already exists.");
                            hasError = true;
                        }
                    }

                    // Nếu có lỗi, lấy lại thông tin nhân viên để hiển thị form
                    if (hasError) {
                        staff = staffDAO.getStaffById(staffId);
                        request.setAttribute("staff", staff);
                    } else {
                        // Nếu không có lỗi, tiến hành cập nhật
                        Staff staffUpdate = new Staff();
                        staffUpdate.setStaff_id(String.valueOf(staffId));
                        staffUpdate.setEmail(email.trim());
                        staffUpdate.setPassword(password != null && !password.trim().isEmpty() ? password.trim() : null);
                        staffUpdate.setFirst_name(firstName.trim());
                        staffUpdate.setLast_name(lastName.trim());
                        staffUpdate.setPhone(phone.trim());
                        staffUpdate.setAddress(address.trim());

                        boolean updatePassword = password != null && !password.trim().isEmpty();
                        staffDAO.updateStaff(staffUpdate, updatePassword);

                        request.setAttribute("success", "Staff updated successfully!");
                        url = "admin";
                    }
                } catch (SQLException e) {
                    System.out.println("updateStaffController: Database error - " + e.getMessage());
                    request.setAttribute("err", "Database error: " + e.getMessage());
                    try (Connection conn = DBContext.getConnection()) {
                        StaffDAO staffDAO = new StaffDAO(conn);
                        staff = staffDAO.getStaffById(staffId);
                        request.setAttribute("staff", staff);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error at updateStaffController: " + e.toString());
            request.setAttribute("err", "System error: " + e.getMessage());
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