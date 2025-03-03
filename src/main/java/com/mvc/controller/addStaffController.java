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

                System.out.println("Form data: firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + 
                                 ", password=" + password + ", phone=" + phone + ", address=" + address);

                // Gọi StaffDAO để thêm nhân viên
                try (Connection conn = DBContext.getConnection()) {
                    if (conn == null) {
                        throw new Exception("Không thể kết nối đến database: Kết nối trả về null");
                    }
                    StaffDAO staffDAO = new StaffDAO(conn);
                    boolean success = staffDAO.addStaff(email, password, firstName, lastName, phone, address);

                    if (success) {
                        System.out.println("addStaffController: Staff added successfully");
                        // Nếu thêm thành công, redirect về danh sách staff
                        request.setAttribute("success", "Nhân viên đã được thêm thành công!");
                        url = "admin";
                    } else {
                        System.out.println("addStaffController: Failed to add staff (email exists or invalid data)");
                        // Nếu thất bại (email đã tồn tại hoặc dữ liệu không hợp lệ), hiển thị lỗi
                        request.setAttribute("err", "Không thể thêm nhân viên. Email đã tồn tại hoặc dữ liệu không hợp lệ.");
                    }
                } catch (Exception e) {
                    System.out.println("addStaffController: Error adding staff - " + e.getMessage());
                    e.printStackTrace();
                    request.setAttribute("err", "Lỗi khi thêm nhân viên: " + e.getMessage());
                }
            } else {
                System.out.println("addStaffController: Processing GET request");
            }
        } catch (Exception e) {
            System.out.println("Error at addStaffController: " + e.toString());
            e.printStackTrace();
            request.setAttribute("err", "Lỗi hệ thống: " + e.getMessage());
        } finally {
            System.out.println("addStaffController: Forwarding to " + url);
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