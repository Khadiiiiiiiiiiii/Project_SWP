/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.model.Product;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hoang
 */

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class updateProductManagement extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads"; // Thư mục lưu file ảnh
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        Product product = productDAO.getProductManagementById(productId);
        if (product != null) {
            request.setAttribute("product", product);
            request.getRequestDispatcher("/updateProduct.jsp").forward(request, response);
        } else {
            response.sendRedirect("ProductManagement?error=ProductNotFound");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isValid = true;
        Map<String, String> errors = new HashMap<>(); // Lưu lỗi riêng cho từng trường

        // Lấy dữ liệu từ form
        int productId = Integer.parseInt(request.getParameter("productId"));
        String name = request.getParameter("productName");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String discountPriceStr = request.getParameter("discountPrice");
        String categoryIdStr = request.getParameter("categoryId");
        String stockQuantityStr = request.getParameter("stockQuantity");
        String promotionIdStr = request.getParameter("promotionId");

        // 1. Validate Product Name
        if (name == null || name.trim().isEmpty()) {
            isValid = false;
            errors.put("productName", "Product name is required.");
        } else if (name.length() < 6 || name.length() > 100 || !name.matches("^[a-zA-Z0-9\\s\\-]+$")) {
            isValid = false;
            errors.put("productName", "Product name must be 6-100 characters, only letters, numbers, spaces, - allowed.");
        } else if (name.matches(".*\\s{2,}.*")) {
            isValid = false;
            errors.put("productName", "Product name must not contain consecutive spaces.");
        } else if (name.matches(".*[^\\w\\s]{2,}.*")) {
            isValid = false;
            errors.put("productName", "Product name must not contain consecutive special characters.");
        }

        // 2. Validate Description
        if (description == null || description.trim().isEmpty()) {
            isValid = false;
            errors.put("description", "Description is required.");
        } else if (description.length() < 6 || description.length() > 500) {
            isValid = false;
            errors.put("description", "Description must be 6-500 characters.");
        } else if (description.matches(".*\\s{2,}.*")) {
            isValid = false;
            errors.put("description", "Description must not contain consecutive spaces.");
        } else if (description.matches(".*[^\\w\\s]{2,}.*")) {
            isValid = false;
            errors.put("description", "Description must not contain consecutive special characters.");
        }

        // 3. Validate Price
        BigDecimal priceValue = null;
        if (priceStr == null || priceStr.trim().isEmpty()) {
            isValid = false;
            errors.put("price", "Price is required.");
        } else {
            try {
                priceValue = new BigDecimal(priceStr);
                if (priceValue.compareTo(BigDecimal.ZERO) <= 0) {
                    isValid = false;
                    errors.put("price", "Price must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("price", "Price must be a valid number.");
            }
        }

        // 4. Validate Discount Price
        BigDecimal discountPriceValue = null;
        if (discountPriceStr != null && !discountPriceStr.trim().isEmpty()) {
            try {
                discountPriceValue = new BigDecimal(discountPriceStr);
                if (discountPriceValue.compareTo(BigDecimal.ZERO) < 0) {
                    isValid = false;
                    errors.put("discountPrice", "Discount price cannot be negative.");
                } else if (discountPriceValue.compareTo(new BigDecimal("100")) > 0) {
                    isValid = false;
                    errors.put("discountPrice", "Discount price must be between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("discountPrice", "Discount price must be a valid number.");
            }
        }

        // 5. Validate Category ID
        int categoryId = 0;
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            isValid = false;
            errors.put("categoryId", "Category ID is required.");
        } else {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
                if (categoryId < 1 || categoryId > 5) {
                    isValid = false;
                    errors.put("categoryId", "Category ID must be between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("categoryId", "Category ID must be a valid integer.");
            }
        }

        // 6. Validate Stock Quantity
        int stockQuantity = 0;
        if (stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
            isValid = false;
            errors.put("stockQuantity", "Stock quantity is required.");
        } else {
            try {
                stockQuantity = Integer.parseInt(stockQuantityStr);
                if (stockQuantity <= 0) {
                    isValid = false;
                    errors.put("stockQuantity", "Stock quantity must be greater than 0.");
                } else if (stockQuantity > 10000) {
                    isValid = false;
                    errors.put("stockQuantity", "Stock quantity cannot exceed 10,000.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("stockQuantity", "Stock quantity must be a valid integer.");
            }
        }

        // 7. Validate Image Upload
        String imageUrl = null;
        Part filePart = request.getPart("imageFile");
        if (filePart != null && filePart.getSize() > 0) {
            if (filePart.getSize() > 10 * 1024 * 1024) { // 10MB
                isValid = false;
                errors.put("imageFile", "Image file size must not exceed 10MB.");
            } else {
                String fileName = extractFileName(filePart);
                String fileExt = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                if (!fileExt.matches("\\.(jpg|jpeg|png|gif)$")) {
                    isValid = false;
                    errors.put("imageFile", "Image must be JPG, JPEG, PNG, or GIF.");
                } else {
                    String applicationPath = request.getServletContext().getRealPath("");
                    String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }
                    imageUrl = UPLOAD_DIR + File.separator + fileName;
                    filePart.write(uploadPath + File.separator + fileName);
                }
            }
        } else {
            // Nếu không upload ảnh mới, giữ nguyên ảnh cũ
            Product existingProduct = productDAO.getProductManagementById(productId);
            imageUrl = existingProduct.getImageUrl();
        }

        // 8. Validate Promotion ID
        Integer promotionId = null;
        if (promotionIdStr != null && !promotionIdStr.trim().isEmpty()) {
            try {
                promotionId = Integer.parseInt(promotionIdStr);
                if (promotionId < 0) {
                    isValid = false;
                    errors.put("promotionId", "Promotion ID cannot be negative.");
                } else if (productDAO.isPromotionIdExists(promotionId)) {
                    Product existingProduct = productDAO.getProductManagementById(productId);
                    if (existingProduct.getPromotionId() == null || !existingProduct.getPromotionId().equals(promotionId)) {
                        isValid = false;
                        errors.put("promotionId", "Promotion ID already exists in the database.");
                    }
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("promotionId", "Promotion ID must be a valid integer.");
            }
        }

        // Nếu hợp lệ, cập nhật sản phẩm
        if (isValid) {
            Product product = new Product(productId, name, description, priceValue, discountPriceValue, categoryId, stockQuantity, imageUrl, promotionId, false);
            boolean updated = productDAO.updateProduct(product);
            if (updated) {
                response.sendRedirect("ProductManagement");
                return;
            } else {
                errors.put("general", "Failed to update product.");
                isValid = false;
            }
        }

        // Nếu không hợp lệ, trả về trang JSP với các lỗi riêng
        Product product = new Product(productId, name, description, priceValue, discountPriceValue, categoryId, stockQuantity, imageUrl, promotionId, false);
        request.setAttribute("product", product);
        request.setAttribute("errors", errors);
        request.getRequestDispatcher("/updateProduct.jsp").forward(request, response);
    }

    // Hàm helper để lấy tên file từ Part
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
}