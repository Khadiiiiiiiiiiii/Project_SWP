package com.mvc.controller;

import com.mvc.DAO.UserDAO;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class UpdateProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp"); // Nếu chưa đăng nhập, chuyển về trang login
            return;
        }

        // Lấy dữ liệu từ form
        String fullName = request.getParameter("name").trim();
        String phone = request.getParameter("phone").trim();
        String address = request.getParameter("address").trim();

        // Tách fullName thành firstName và lastName
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        UserDAO userDAO = new UserDAO();
        try {
            boolean success = userDAO.updateProfile(user.getUserId(), firstName, lastName, phone, address);

            if (success) {
                // Cập nhật lại session với thông tin mới
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPhone(phone);
                user.setAddress(address);
                session.setAttribute("user", user);

                // Chuyển hướng về trang profile kèm thông báo thành công
                response.sendRedirect("viewProfile.jsp?success=true");
            } else {
                // Nếu cập nhật thất bại, chuyển hướng với thông báo lỗi
                response.sendRedirect("viewProfile.jsp?error=update_failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("viewProfile.jsp?error=server_error");
        }
    }
}
