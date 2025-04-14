package com.mvc.controller;

import com.mvc.DAO.PromotionDAO;
import com.mvc.DAO.UserDAO;
import com.mvc.model.Promotion;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import com.google.gson.Gson;

/**
 * Servlet to handle requests related to managing discount codes for Store Managers and Admins.
 */
@WebServlet("/DiscountServlet")
public class DiscountServlet extends HttpServlet {
    private PromotionDAO promotionDAO = new PromotionDAO();
    private UserDAO userDAO = new UserDAO();

    // Method to update the status of expired promotions
    private void updateExpiredPromotionsStatus() {
        List<Promotion> allPromotions = promotionDAO.getAllPromotions();
        LocalDate today = LocalDate.now();

        for (Promotion promotion : allPromotions) {
            LocalDate expirationDate = promotion.getExpirationDate().toLocalDate();
            if ("Active".equals(promotion.getStatus()) && expirationDate.isBefore(today)) {
                promotion.setStatus("Inactive");
                promotionDAO.updatePromotion(promotion);
                System.out.println("Updated promotion " + promotion.getCode() + " to Inactive due to expiration.");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !("Admin".equals(user.getRole()) || "Store Manager".equals(user.getRole()) || "Customer".equals(user.getRole()))) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Update the status of expired promotions before processing the request
        updateExpiredPromotionsStatus();

        String action = request.getParameter("action");
        String promotionId = request.getParameter("promotionId");
        String storeId = request.getParameter("storeId");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            if ("update".equals(action)) {
                // Show update form for the promotion
                Promotion promotion = promotionDAO.getPromotionById(Integer.parseInt(promotionId));
                if (promotion != null) {
                    request.setAttribute("promotion", promotion);
                    request.setAttribute("storeId", storeId);
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                } else {
                    out.print("{\"error\": \"Promotion not found\"}");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else if ("delete".equals(action)) {
                // Delete the promotion
                if (promotionDAO.deletePromotion(Integer.parseInt(promotionId))) {
                    response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeId + "&message=Promotion deleted successfully");
                } else {
                    out.print("{\"error\": \"Failed to delete promotion\"}");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else if ("view".equals(action)) {
                // Return the list of promotions (JSON) for Store Manager or Admin
                List<Promotion> promotions;
                if ("Admin".equals(user.getRole())) {
                    promotions = promotionDAO.getAllPromotions(); // Admin sees all
                } else if ("Store Manager".equals(user.getRole())) {
                    if (storeId != null && !storeId.trim().isEmpty()) {
                        promotions = promotionDAO.getPromotionsByStoreId(Integer.parseInt(storeId)); // Store Manager sees only their store's promotions
                    } else {
                        promotions = new ArrayList<>(); // No storeId, return empty
                    }
                } else {
                    promotions = new ArrayList<>(); // Other roles have no access
                }
                out.print("[");
                for (int i = 0; i < promotions.size(); i++) {
                    Promotion p = promotions.get(i);
                    out.print("{\"promotionId\":" + p.getPromotionId() + 
                              ",\"code\":\"" + p.getCode() + 
                              "\",\"discountPercentage\":" + p.getDiscountPercentage() + 
                              ",\"expirationDate\":\"" + p.getExpirationDate() + 
                              "\",\"status\":\"" + p.getStatus() + "\"}");
                    if (i < promotions.size() - 1) out.print(",");
                }
                out.print("]");
            } else if ("getAvailablePromotions".equals(action)) {
                // Return the list of active promotions for display in productDetail.jsp or cart.jsp
                List<Promotion> activePromotions = promotionDAO.getActivePromotions();
                System.out.println("Available promotions for customer: " + activePromotions.size());
                if (activePromotions.isEmpty()) {
                    System.out.println("No active promotions found. Check Promotions table for status='Active' and expiration_date >= today.");
                }
                Gson gson = new Gson();
                out.print(gson.toJson(activePromotions));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\": \"Invalid parameter format\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !("Admin".equals(user.getRole()) || "Store Manager".equals(user.getRole()))) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Update the status of expired promotions before processing the request
        updateExpiredPromotionsStatus();

        String action = request.getParameter("action");
        String storeIdStr = request.getParameter("storeId");

        response.setCharacterEncoding("UTF-8");

        if ("create".equals(action)) {
            // Create a new promotion
            String code = request.getParameter("code");
            String discountPercentageStr = request.getParameter("discountPercentage");
            String expirationDateStr = request.getParameter("expirationDate");
            String status = request.getParameter("status");

            // Check storeId
            int storeId;
            try {
                storeId = Integer.parseInt(storeIdStr);
                if (storeId <= 0) {
                    response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Invalid store ID. Please contact the administrator to assign a store.");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Invalid store ID format");
                return;
            }

            // Validate input data
            if (code == null || code.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Promo code cannot be empty");
                return;
            }

            if (discountPercentageStr == null || discountPercentageStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Discount percentage cannot be empty");
                return;
            }

            double discountPercentage;
            try {
                discountPercentage = Double.parseDouble(discountPercentageStr);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Invalid discount percentage format");
                return;
            }

            if (discountPercentage < 0 || discountPercentage > 100) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Discount percentage must be between 0 and 100");
                return;
            }

            java.sql.Date expirationDate;
            try {
                expirationDate = java.sql.Date.valueOf(expirationDateStr);
            } catch (IllegalArgumentException e) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Invalid date format");
                return;
            }

            // Check expiration date
            LocalDate today = LocalDate.now();
            LocalDate expDate = expirationDate.toLocalDate();
            System.out.println("Creating promotion - Code: " + code + ", Expiration Date: " + expDate + ", Today: " + today);
            if (expDate.isBefore(today)) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Expiration date must be today or in the future");
                return;
            }

            if (status == null || status.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Status cannot be empty");
                return;
            }

            // Check if the promo code already exists
            if (promotionDAO.isCodeExists(code)) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Promo code already exists");
                return;
            }

            // Create the promotion with storeId
            Promotion promotion = new Promotion();
            promotion.setCode(code);
            promotion.setDiscountPercentage(new BigDecimal(discountPercentage));
            promotion.setExpirationDate(expirationDate);
            promotion.setStatus(status);
            promotion.setStoreId(storeId); // Set storeId

            int promotionId = promotionDAO.createPromotion(promotion);
            if (promotionId > 0) {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeId + "&message=Promotion created successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/storeManagerDashboard.jsp?storeId=" + storeIdStr + "&error=Failed to create promotion. Check database constraints or logs.");
            }
        } else if ("update".equals(action)) {
            response.setContentType("text/html"); // Đặt content type là HTML để hiển thị JSP
            try {
                // Update the promotion
                int promotionId = Integer.parseInt(request.getParameter("promotionId"));
                String code = request.getParameter("code");
                String discountPercentageStr = request.getParameter("discountPercentage");
                String expirationDateStr = request.getParameter("expirationDate");
                String status = request.getParameter("status");

                // Lấy thông tin mã giảm giá hiện tại để truyền lại nếu có lỗi
                Promotion existingPromotion = promotionDAO.getPromotionById(promotionId);
                if (existingPromotion == null) {
                    request.setAttribute("error", "Promotion not found");
                    request.setAttribute("storeId", storeIdStr);
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                // Truyền lại thông tin storeId và promotion để hiển thị form
                request.setAttribute("promotion", existingPromotion);
                request.setAttribute("storeId", storeIdStr);

                // Validate input data
                if (code == null || code.trim().isEmpty()) {
                    request.setAttribute("error", "Promo code cannot be empty");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                if (discountPercentageStr == null || discountPercentageStr.trim().isEmpty()) {
                    request.setAttribute("error", "Discount percentage cannot be empty");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                double discountPercentage;
                try {
                    discountPercentage = Double.parseDouble(discountPercentageStr);
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Invalid discount percentage format");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                // 47.0.E2: Kiểm tra phần trăm giảm giá
                if (discountPercentage < 0 || discountPercentage > 100) {
                    request.setAttribute("error", "Discount percentage must be between 0 and 100.");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                java.sql.Date expirationDate;
                try {
                    expirationDate = java.sql.Date.valueOf(expirationDateStr);
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", "Invalid date format");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                if (status == null || status.trim().isEmpty()) {
                    request.setAttribute("error", "Status cannot be empty");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                // 47.0.E3: Kiểm tra trạng thái Active và ngày hết hạn
                LocalDate today = LocalDate.now();
                LocalDate expDate = expirationDate.toLocalDate();
                if ("Active".equals(status) && expDate.isBefore(today)) {
                    request.setAttribute("error", "Please update the expiration date to today or a future date before activating the discount code.");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                // 47.0.E1: Kiểm tra mã trùng (trừ mã hiện tại)
                if (!existingPromotion.getCode().equals(code) && promotionDAO.isCodeExists(code)) {
                    request.setAttribute("error", "Promo code already exists");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    return;
                }

                // Cập nhật mã giảm giá
                Promotion promotion = new Promotion(promotionId, code, new BigDecimal(discountPercentage), expirationDate, status);
                promotion.setStoreId(existingPromotion.getStoreId()); // Giữ nguyên storeId

                try {
                    if (promotionDAO.updatePromotion(promotion)) {
                        // Cập nhật thành công, truyền thông báo thành công
                        request.setAttribute("message", "Promotion updated successfully");
                        request.setAttribute("promotion", promotionDAO.getPromotionById(promotionId)); // Cập nhật lại thông tin
                        request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    } else {
                        // 47.0.E4: Lỗi hệ thống
                        request.setAttribute("error", "An error occurred while updating the discount code.");
                        request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    // 47.0.E4: Lỗi hệ thống (ví dụ: lỗi kết nối cơ sở dữ liệu)
                    request.setAttribute("error", "An error occurred while updating the discount code.");
                    request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid parameter format");
                request.setAttribute("promotion", promotionDAO.getPromotionById(Integer.parseInt(request.getParameter("promotionId"))));
                request.setAttribute("storeId", storeIdStr);
                request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid date format");
                request.setAttribute("promotion", promotionDAO.getPromotionById(Integer.parseInt(request.getParameter("promotionId"))));
                request.setAttribute("storeId", storeIdStr);
                request.getRequestDispatcher("/updateDiscount.jsp").forward(request, response);
            }
        }
    }
}