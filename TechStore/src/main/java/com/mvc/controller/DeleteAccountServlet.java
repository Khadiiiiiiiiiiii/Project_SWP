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

public class DeleteAccountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy user từ session
        User user = (User) session.getAttribute("user");

        // Gọi DAO để xóa tài khoản
        UserDAO userDAO = new UserDAO();
        try {
            boolean success = userDAO.deleteUser(user.getUserId());

            if (success) {
                session.invalidate(); // Xóa session
                response.sendRedirect("index.jsp?deleted=true"); // Chuyển về trang chủ
            } else {
                response.sendRedirect("viewProfile.jsp?error=delete_failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("viewProfile.jsp?error=server_error");
        }
    }
}
