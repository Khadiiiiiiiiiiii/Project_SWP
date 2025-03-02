package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.model.Product;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/category"})
public class CategoryServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryIdStr = request.getParameter("category");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");

        // Lọc giá
        BigDecimal minPrice = minPriceStr != null && !minPriceStr.isEmpty() ? new BigDecimal(minPriceStr) : null;
        BigDecimal maxPrice = maxPriceStr != null && !maxPriceStr.isEmpty() ? new BigDecimal(maxPriceStr) : null;

        // Lọc theo danh mục
        List<Product> productList;
        String categoryName = "All Products";
        boolean isAllProducts = false;

        if (categoryIdStr == null || categoryIdStr.equals("all")) {
            isAllProducts = true;
            productList = productDAO.getProductsByPriceRange(null, minPrice, maxPrice);
        } else {
            int categoryId = Integer.parseInt(categoryIdStr);
            // Lọc sản phẩm theo danh mục và giá
            productList = productDAO.getProductsByPriceRange(categoryId, minPrice, maxPrice);

            // Đặt tên danh mục tương ứng
            switch (categoryId) {
                case 1: categoryName = "Laptop"; break;
                case 2: categoryName = "Mouse"; break;
                case 3: categoryName = "Keyboard"; break;
                case 4: categoryName = "Screen"; break;
                case 5: categoryName = "Headphone"; break;
                default: categoryName = "Other"; break;
            }
        }

        // Chia sản phẩm theo danh mục
        Map<Integer, List<Product>> productsByCategory = new HashMap<>();
        for (Product product : productList) {
            int categoryId = product.getCategoryId();
            productsByCategory.computeIfAbsent(categoryId, k -> new ArrayList<>()).add(product);
        }

        // Gửi dữ liệu sang trang JSP
        request.setAttribute("productList", productList);
        request.setAttribute("categoryName", categoryName);
        request.setAttribute("isAllProducts", isAllProducts);
        request.setAttribute("productsByCategory", productsByCategory);
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice);

        // Forward tới trang JSP
        request.getRequestDispatcher("/products.jsp").forward(request, response);
    }
}
