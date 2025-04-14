package com.mvc.controller;

import com.mvc.DAO.StaffDAO;
import com.mvc.dal.DBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "addStaffController", urlPatterns = {"/addStaff"})
public class addStaffController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = "addStaff.jsp"; // Trang mặc định nếu không phải POST hoặc có lỗi
        try {
            System.out.println("addStaffController: Method = " + request.getMethod());
            String method = request.getMethod();
            if (method.equalsIgnoreCase("POST")) {
                System.out.println("addStaffController: Processing POST request");

                // Lấy tham số từ form
                String firstName = request.getParameter("txtfirstname");
                String lastName = request.getParameter("txtlastname");
                String email = request.getParameter("txtemail");
                String password = request.getParameter("txtpassword");
                String phone = request.getParameter("txtphone");
                String address = request.getParameter("txtaddress");

                // Biến để theo dõi lỗi
                boolean hasError = false;

                // Kiểm tra các trường và thu thập tất cả lỗi
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

                if (password == null || !password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!]{6,}$")) {
                    request.setAttribute("errPassword", "Invalid! Password must be at least 6 characters, including at least one letter and one number.");
                    hasError = true;
                }

                if (phone == null || !phone.matches("^0\\d{9}$")) {
                    request.setAttribute("errPhone", "Invalid! Phone number must be 10 digits and start with '0'.");
                    hasError = true;
                }

                if (address == null || address.length() < 6 || address.length() > 200 || address.matches("^(?!.*\\s{2})")) {
                    request.setAttribute("errAddress", "Invalid! Address must be between 6 and 200 characters.");
                    hasError = true;
                }

                // Kiểm tra email và phone tồn tại ngay cả khi có lỗi khác
                try (Connection conn = DBContext.getConnection()) {
                    StaffDAO staffDAO = new StaffDAO(conn);

                    // Kiểm tra email
                    boolean emailExists = false;
                    if (email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")) {
                        emailExists = staffDAO.isEmailExists(email);
                        if (emailExists) {
                            request.setAttribute("errEmail", "Email already exists.");
                            hasError = true;
                        }
                    }

                    // Kiểm tra số điện thoại
                    boolean phoneExists = false;
                    if (phone != null && phone.matches("^0\\d{9}$")) {
                        phoneExists = staffDAO.isPhoneExists(phone);
                        if (phoneExists) {
                            request.setAttribute("errPhone", "Phone number already exists.");
                            hasError = true;
                        }
                    }

                    // Nếu không có lỗi, tiến hành thêm nhân viên
                    if (!hasError) {
                        boolean success = staffDAO.addStaff(email, password, firstName, lastName, phone, address);
                        if (success) {
                            request.setAttribute("success", "Staff added successfully!");
                            url = "admin";
                        } else {
                            request.setAttribute("err", "Failed to add staff (invalid data).");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("addStaffController: SQL Error - " + e.getMessage());
                    e.printStackTrace();
                    request.setAttribute("err", "Database error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("addStaffController: Error adding staff - " + e.getMessage());
                    e.printStackTrace();
                    request.setAttribute("err", "Unexpected error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("addStaffController: Unexpected error - " + e.toString());
            e.printStackTrace();
            request.setAttribute("err", "Unexpected error: " + e.getMessage());
        } finally {
            System.out.println("Forwarding to: " + url);
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