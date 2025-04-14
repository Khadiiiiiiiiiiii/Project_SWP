package com.mvc.controller;

import com.mvc.DAO.OrderDAO;
import com.mvc.model.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.mvc.model.User;

public class ListOrderManagement extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDAO = new OrderDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy thông tin user từ session
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Lấy danh sách đơn hàng
            List<Order> orderList = orderDAO.getInfoOrderList();
            request.setAttribute("orderList", orderList);

            // Quyết định forward tới trang nào dựa trên vai trò
            String role = user.getRole();
            if ("Admin".equals(role)) {
                request.getRequestDispatcher("ListOrderManagement.jsp").forward(request, response);
            } else if ("STAFF".equals(role) || "Store Manager".equalsIgnoreCase(role)) {
                // Cả Staff và Store Manager đều sử dụng staffDashboard.jsp
                request.getRequestDispatcher("staffDashboard.jsp").forward(request, response);
            } else {
                response.sendRedirect("login.jsp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("err", "Database error: " + e.getMessage());
            request.getRequestDispatcher("staffDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("err", "Unexpected error: " + e.getMessage());
            request.getRequestDispatcher("staffDashboard.jsp").forward(request, response);
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
        String action = request.getParameter("action");
        if ("updateStatus".equals(action)) {
            try {
                int orderId = Integer.parseInt(request.getParameter("order_id"));
                String status = request.getParameter("status");

                // Kiểm tra giá trị status hợp lệ
                if (!status.equals("Processing") && !status.equals("Pending") && 
                    !status.equals("completed") && !status.equals("Cancel")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid status value");
                    return;
                }

                boolean updated = orderDAO.updateOrderStatus(orderId, status);
                if (updated) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Success");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Order not found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Database error: " + e.getMessage());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid order ID");
            }
        } else {
            processRequest(request, response);
        }
    }
}