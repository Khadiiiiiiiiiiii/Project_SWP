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

public class ProductServlet extends HttpServlet {
    
    private ProductDAO productDAO;

    public ProductServlet() {
        productDAO = new ProductDAO();
        System.out.println("ProductServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy giá trị category từ request
        String category = request.getParameter("category");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");

        // Thêm log để kiểm tra giá trị của category và URL
        System.out.println("ProductServlet: Request URL: " + request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        System.out.println("ProductServlet: Category parameter: " + category);

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        // Chuyển đổi giá trị minPrice và maxPrice từ String sang BigDecimal
        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = new BigDecimal(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = new BigDecimal(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            System.err.println("ProductServlet: Invalid price format: " + e.getMessage());
        }

        List<Product> productList = new ArrayList<>();
        Map<Integer, List<Product>> productsByCategory = new HashMap<>();
        String categoryName = "All Products"; // Giá trị mặc định
        boolean isAllProducts = false;

        System.out.println("ProductServlet: Starting category logic check...");
        if (category == null || "all".equals(category)) {
            System.out.println("ProductServlet: Category is null or 'all', fetching all products...");
            // Hiển thị tất cả sản phẩm, nhóm theo danh mục
            isAllProducts = true;
            categoryName = "All Products";
            List<Product> allProducts = productDAO.getAllProducts();
            for (Product product : allProducts) {
                int catId = product.getCategoryId();
                productsByCategory.computeIfAbsent(catId, k -> new ArrayList<>()).add(product);
            }
            System.out.println("ProductServlet: Retrieved " + allProducts.size() + " products for 'All Products'");
        } else if ("discount".equals(category)) {
            System.out.println("ProductServlet: Category is 'discount', fetching discounted products...");
            // Hiển thị sản phẩm có giảm giá (Discount Product), nhóm theo danh mục
            isAllProducts = true;
            categoryName = "Discount Product"; // Đảm bảo tiêu đề là "Discount Product"
            List<Product> discountedProducts;

            // Lấy danh sách sản phẩm có giảm giá, áp dụng bộ lọc giá nếu có
            if (minPrice != null || maxPrice != null) {
                discountedProducts = productDAO.getDiscountedProductsByPriceRange(minPrice, maxPrice);
            } else {
                discountedProducts = productDAO.getDiscountedProducts();
            }

            // Thêm log để kiểm tra danh sách sản phẩm
            System.out.println("ProductServlet: Discounted products retrieved: " + discountedProducts.size());
            for (Product product : discountedProducts) {
                System.out.println("ProductServlet: Product: " + product.getName() + ", Price: " + product.getPrice() + ", Discount Price (%): " + product.getDiscountPrice() + ", Discounted Price: " + product.getDiscountedPrice());
            }

            // Nhóm sản phẩm theo danh mục
            for (Product product : discountedProducts) {
                int catId = product.getCategoryId();
                productsByCategory.computeIfAbsent(catId, k -> new ArrayList<>()).add(product);
            }
            System.out.println("ProductServlet: Retrieved " + discountedProducts.size() + " discounted products for 'Discount Product'");
        } else {
            System.out.println("ProductServlet: Category is specific ID, fetching products by category ID...");
            // Hiển thị sản phẩm theo danh mục cụ thể
            try {
                int categoryId = Integer.parseInt(category);
                productList = productDAO.getProductsByCategoryId(categoryId);
                if (minPrice != null || maxPrice != null) {
                    productList = productDAO.getProductsByPriceRange(categoryId, minPrice, maxPrice);
                }
                switch (categoryId) {
                    case 1: categoryName = "Laptop"; break;
                    case 2: categoryName = "Mouse"; break;
                    case 3: categoryName = "Keyboard"; break;
                    case 4: categoryName = "Screen"; break;
                    case 5: categoryName = "Headphone"; break;
                    default: categoryName = "Other"; break;
                }
                System.out.println("ProductServlet: Retrieved " + productList.size() + " products for category " + categoryName);
            } catch (NumberFormatException e) {
                System.err.println("ProductServlet: Invalid category ID: " + category);
                productList = new ArrayList<>();
            }
        }

        // Đặt các thuộc tính vào request để hiển thị trên JSP
        request.setAttribute("isAllProducts", isAllProducts);
        request.setAttribute("productsByCategory", productsByCategory);
        request.setAttribute("productList", productList);
        request.setAttribute("categoryName", categoryName);

        // Thêm log để kiểm tra giá trị categoryName trước khi forward
        System.out.println("ProductServlet: Category name set to: " + categoryName);

        // Chuyển tiếp đến JSP
        System.out.println("ProductServlet: Forwarding to products.jsp...");
        request.getRequestDispatcher("/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}