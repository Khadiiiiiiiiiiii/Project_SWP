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

@WebServlet(name = "DiscountProductServlet", urlPatterns = {"/discount-products"})
public class DiscountProductServlet extends HttpServlet {
    
    private ProductDAO productDAO;

    public DiscountProductServlet() {
        productDAO = new ProductDAO();
        System.out.println("DiscountProductServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Log để kiểm tra
        System.out.println("DiscountProductServlet: Request URL: " + request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));

        // Lấy tham số minPrice và maxPrice để lọc giá (nếu có)
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = new BigDecimal(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = new BigDecimal(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            System.err.println("DiscountProductServlet: Invalid price format: " + e.getMessage());
        }

        // Lấy danh sách sản phẩm có giảm giá
        List<Product> discountedProducts;
        if (minPrice != null || maxPrice != null) {
            discountedProducts = productDAO.getDiscountedProductsByPriceRange(minPrice, maxPrice);
        } else {
            discountedProducts = productDAO.getDiscountedProducts();
        }

        // Log danh sách sản phẩm
        System.out.println("DiscountProductServlet: Discounted products retrieved: " + discountedProducts.size());
        for (Product product : discountedProducts) {
            System.out.println("DiscountProductServlet: Product: " + product.getName() + ", Price: " + product.getPrice() + ", Discount Price (%): " + product.getDiscountPrice());
        }

        // Nhóm sản phẩm theo danh mục
        Map<Integer, List<Product>> productsByCategory = new HashMap<>();
        for (Product product : discountedProducts) {
            int catId = product.getCategoryId();
            productsByCategory.computeIfAbsent(catId, k -> new ArrayList<>()).add(product);
        }

        // Log sau khi nhóm sản phẩm
        System.out.println("DiscountProductServlet: Products grouped by category: " + productsByCategory.size() + " categories");

        // Đặt các thuộc tính vào request
        request.setAttribute("isAllProducts", true); // Đặt isAllProducts là true để hiển thị productsByCategory
        request.setAttribute("productsByCategory", productsByCategory);
        request.setAttribute("categoryName", "Discount Product");

        // Log trước khi forward
        System.out.println("DiscountProductServlet: Forwarding to ViewDiscountProduct.jsp...");

        // Chuyển tiếp đến ViewDiscountProduct.jsp
        request.getRequestDispatcher("/ViewDiscountProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}