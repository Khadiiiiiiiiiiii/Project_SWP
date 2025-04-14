package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.DAO.PromotionDAO;
import com.mvc.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet xử lý các yêu cầu liên quan đến danh sách sản phẩm theo danh mục (category).
 * Hỗ trợ Discount Management với hiển thị giá đã giảm (giảm giá trực tiếp và mã giảm giá).
 */
@WebServlet("/category")
public class CategoryServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private PromotionDAO promotionDAO = new PromotionDAO(); // Thêm để hỗ trợ mã giảm giá, nếu cần

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String category = request.getParameter("category");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");

        Integer categoryId = null;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        // Chuyển đổi category từ String sang Integer (nếu cần)
        if (category != null && !category.equals("all")) {
            try {
                categoryId = Integer.parseInt(category);
            } catch (NumberFormatException e) {
                categoryId = null; // Xử lý lỗi nếu category không hợp lệ
            }
        }

        // Chuyển đổi minPrice và maxPrice từ String sang BigDecimal
        if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
            try {
                minPrice = new BigDecimal(minPriceStr);
            } catch (NumberFormatException e) {
                minPrice = BigDecimal.ZERO; // Giá trị mặc định nếu không hợp lệ
            }
        }

        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                maxPrice = new BigDecimal(maxPriceStr);
            } catch (NumberFormatException e) {
                maxPrice = new BigDecimal("50000000"); // Giá trị mặc định nếu không hợp lệ (theo placeholder)
            }
        }

        Map<Integer, List<Product>> productsByCategory = new HashMap<>();
        boolean isAllProducts = "all".equals(category);

        if (isAllProducts) {
            // Lấy tất cả sản phẩm và lọc theo giá
            List<Product> allProducts = productDAO.getProductsByPriceRange(null, minPrice, maxPrice);
            // Phân loại sản phẩm theo category_id
            for (Product product : allProducts) {
                int catId = product.getCategoryId();
                productsByCategory.putIfAbsent(catId, new ArrayList<>());
                productsByCategory.get(catId).add(product);
            }
            request.setAttribute("productsByCategory", productsByCategory);
            request.setAttribute("isAllProducts", true);
        } else {
            // Lấy sản phẩm theo category_id và lọc theo giá
            List<Product> productList = productDAO.getProductsByPriceRange(categoryId, minPrice, maxPrice);
            request.setAttribute("productList", productList);
            request.setAttribute("isAllProducts", false);
        }

        // Thêm thông tin category name để hiển thị trên products.jsp
        String categoryName = getCategoryName(categoryId);
        request.setAttribute("categoryName", categoryName != null ? categoryName : "All Products");

        request.getRequestDispatcher("/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); // Xử lý POST giống GET cho form lọc
    }

    /**
     * Lấy tên danh mục dựa trên categoryId.
     * @param categoryId ID của danh mục
     * @return Tên danh mục hoặc null nếu không tìm thấy
     */
    private String getCategoryName(Integer categoryId) {
        if (categoryId == null) return null;
        switch (categoryId) {
            case 1: return "Laptop";
            case 2: return "Mouse";
            case 3: return "Keyboard";
            case 4: return "Screen";
            case 5: return "Headphone";
            default: return "Other";
        }
    }
}