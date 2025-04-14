package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.model.Product;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ListProductManagement extends HttpServlet {
    private ProductDAO productDAO;

    public ListProductManagement() {
        this.productDAO = new ProductDAO();
        System.out.println("ListProductManagement initialized with ProductDAO.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy thông tin user từ session
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Chỉ cho phép Admin hoặc Store Manager truy cập
            String role = user.getRole();
            if (!("Admin".equals(role) || "Store Manager".equalsIgnoreCase(role))) {
                response.sendRedirect("accessDenied.jsp");
                return;
            }

            // Lấy danh sách sản phẩm
            List<Product> productList = productDAO.getAll();

            if (productList.isEmpty()) {
                System.out.println("Servlet: Product list is empty. Check database connection or query.");
                request.setAttribute("err", "No products found. Check database connection or data.");
            }

            request.setAttribute("proList", productList);
            request.getRequestDispatcher("ProductManagement.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("err", "Unexpected error: " + e.getMessage());
            request.getRequestDispatcher("ProductManagement.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}