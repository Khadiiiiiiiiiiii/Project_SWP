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

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate email format
        if (email == null || !email.contains("@")) {
            request.setAttribute("errorMessage", "Invalid email format! Please enter a valid email.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.login(email, password);

            if (user == null) {
                // Kiểm tra xem email có tồn tại không để phân biệt lý do lỗi
                User existingUser = userDAO.findUserByEmail(email);
                if (existingUser == null) {
                    request.setAttribute("errorMessage", "Account does not exist! Please register if you don't have an account.");
                } else {
                    // Email tồn tại, kiểm tra trạng thái hoặc mật khẩu sai
                    if ("disable".equalsIgnoreCase(existingUser.getStatus())) {
                        request.setAttribute("errorMessage", "Account is locked!!!.");
                    } else {
                        request.setAttribute("errorMessage", "Incorrect password! Please try again.");
                    }
                }
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            } else {
                // Đăng nhập thành công
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // Kiểm tra redirectUrl và chuyển hướng
                String redirectUrl = (String) session.getAttribute("redirectUrl");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    session.removeAttribute("redirectUrl");
                    response.sendRedirect(redirectUrl);
                } else {
                    // Chuyển hướng dựa trên vai trò
                    String role = user.getRole().toLowerCase();
                    if ("admin".equals(role)) {
                        response.sendRedirect(request.getContextPath() + "/admin");
                    } else if ("staff".equals(role)) {
                        response.sendRedirect(request.getContextPath() + "/staffDashboard.jsp");
                    } else if ("store manager".equals(role)) {
                        int storeId = userDAO.getStoreIdByUserId(user.getUserId());
                        if (storeId != -1) {
                            session.setAttribute("storeId", storeId);
                            response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeId);
                        } else {
                            response.sendRedirect("error.jsp?message=Store ID not found");
                        }
                    } else {
                        response.sendRedirect(request.getContextPath() + "/Home.jsp");
                    }
                }
                return;
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            request.setAttribute("errorMessage", "System error! Please try again later.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
    }
}
