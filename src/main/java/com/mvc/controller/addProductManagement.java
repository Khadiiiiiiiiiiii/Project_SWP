package com.mvc.controller;

import com.mvc.DAO.ProductDAO;
import com.mvc.model.Product;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.util.HashMap;
import java.util.Map;

@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // Giới hạn 5MB cho file upload
public class addProductManagement extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isValid = true;
        Map<String, String> errors = new HashMap<>(); // Lưu lỗi riêng cho từng trường

        // Lấy dữ liệu từ form
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
        int priceValue = 0;
        if (priceStr == null || priceStr.trim().isEmpty()) {
            isValid = false;
            errors.put("price", "Price is required.");
        } else {
            try {
                priceValue = Integer.parseInt(priceStr);
                if (priceValue <= 1000) {
                    isValid = false;
                    errors.put("price", "Price must be greater than 1000.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("price", "Price must be a valid integer.");
            }
        }

        // 4. Validate Discount Price
        Integer discountPriceValue = null;
        if (discountPriceStr != null && !discountPriceStr.trim().isEmpty()) {
            try {
                discountPriceValue = Integer.parseInt(discountPriceStr);
                if (discountPriceValue < 0) {
                    isValid = false;
                    errors.put("discountPrice", "Discount price cannot be negative.");
                } else if (discountPriceValue > 100) {
                    isValid = false;
                    errors.put("discountPrice", "Discount price must be between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("discountPrice", "Discount price must be a valid integer.");
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
        String imageUrl = "";
        Part filePart = request.getPart("imageFile");
        if (filePart == null || filePart.getSize() == 0) {
            isValid = false;
            errors.put("imageFile", "Image file is required.");
        } else if (filePart.getSize() > 5 * 1024 * 1024) { // 5MB
            isValid = false;
            errors.put("imageFile", "Image file size must not exceed 5MB.");
        } else {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileExt = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            if (!fileExt.matches("\\.(jpg|jpeg|png|gif)$")) {
                isValid = false;
                errors.put("imageFile", "Image must be JPG, JPEG, PNG, or GIF.");
            } else {
                String uploadDir = getServletContext().getRealPath("") + "img/";
                Files.createDirectories(Paths.get(uploadDir));
                String filePath = uploadDir + fileName;
                filePart.write(filePath);
                imageUrl = "img/" + fileName;
            }
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
                    isValid = false;
                    errors.put("promotionId", "Promotion ID already exists in the database.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                errors.put("promotionId", "Promotion ID must be a valid integer.");
            }
        }

        // Nếu hợp lệ, thêm sản phẩm
        if (isValid) {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(BigDecimal.valueOf(priceValue));
            product.setDiscountPrice(discountPriceValue != null ? BigDecimal.valueOf(discountPriceValue) : null);
            product.setCategoryId(categoryId);
            product.setStockQuantity(stockQuantity);
            product.setImageUrl(imageUrl);
            product.setPromotionId(promotionId);
            product.setIsDeleted(false);

            boolean success = productDAO.addProduct(product);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/ProductManagement");
                return;
            } else {
                isValid = false;
                errors.put("general", "Failed to add product to database.");
            }
        }

        // Nếu không hợp lệ, trả về trang JSP với các lỗi riêng
        request.setAttribute("errors", errors);
        request.getRequestDispatcher("addProduct.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("addProduct.jsp").forward(request, response);
    }
}
