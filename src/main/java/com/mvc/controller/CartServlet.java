package com.mvc.controller;

import com.google.gson.Gson;
import com.mvc.DAO.CartDAO;
import com.mvc.DAO.CustomerDAO;
import com.mvc.DAO.ProductDAO;
import com.mvc.DAO.PromotionDAO;
import com.mvc.model.CartItem;
import com.mvc.model.Product;
import com.mvc.model.Promotion;
import com.mvc.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("redirectUrl", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect("login.jsp");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
        if (customerId == -1) {
            System.err.println("CustomerId not found for user: " + user.getUserId());
            response.sendRedirect("error.jsp");
            return;
        }

        CartDAO cartDAO = new CartDAO();
        String action = request.getParameter("action");

        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action parameter is required");
            return;
        }

        try {
            int productId = -1;
            if (!"applyCartPromo".equals(action) && !"removePromo".equals(action) && !"removeCartPromo".equals(action) && !"removeCartItem".equals(action)) {
                String productIdParam = request.getParameter("product_id");
                if (productIdParam != null && !productIdParam.trim().isEmpty()) {
                    productId = Integer.parseInt(productIdParam);
                } else {
                    throw new IllegalArgumentException("Product ID is required for action: " + action);
                }
            }

            switch (action) {
                case "add":
                case "buyNow":
                    String quantityParam = request.getParameter("quantity");
                    if (quantityParam == null || quantityParam.trim().isEmpty()) {
                        throw new IllegalArgumentException("Quantity is required for action: " + action);
                    }
                    int quantity = Integer.parseInt(quantityParam);
                    cartDAO.addOrUpdateCartItem(customerId, productId, quantity);
                    if ("add".equals(action)) {
                        session.removeAttribute("discountedTotal");
                        session.removeAttribute("discountPercentage");
                        session.removeAttribute("cartPromoCode");
                    } else { // buyNow
                        response.sendRedirect("checkout.jsp?productId=" + productId + "&quantity=" + quantity);
                        return;
                    }
                    break;

                case "update":
                    String cartItemIdParam = request.getParameter("cart_item_id");
                    if (cartItemIdParam == null || cartItemIdParam.trim().isEmpty()) {
                        throw new IllegalArgumentException("Cart Item ID is required for update action");
                    }
                    int cartItemId = Integer.parseInt(cartItemIdParam);
                    String newQuantityParam = request.getParameter("quantity");
                    if (newQuantityParam == null || newQuantityParam.trim().isEmpty()) {
                        throw new IllegalArgumentException("New quantity is required for update action");
                    }
                    int newQuantity = Integer.parseInt(newQuantityParam);
                    cartDAO.updateCartItem(cartItemId, newQuantity);
                    break;

                case "remove":
                case "removeCartItem":
                    String cartItemIdRemoveParam = request.getParameter("cart_item_id");
                    if (cartItemIdRemoveParam == null || cartItemIdRemoveParam.trim().isEmpty()) {
                        throw new IllegalArgumentException("Cart Item ID is required for remove action");
                    }
                    int cartItemIdRemove = Integer.parseInt(cartItemIdRemoveParam);
                    cartDAO.removeCartItem(cartItemIdRemove);
                    break;

                case "applyPromo":
                    String promoCode = request.getParameter("promoCode");
                    if (promoCode != null && !promoCode.trim().isEmpty()) {
                        cartDAO.applyPromoToCartItem(customerId, productId, promoCode);
                        session.setAttribute("promoCode_" + productId, promoCode);
                    }
                    break;

                case "removePromo":
                    response.setContentType("application/json");
                    Gson gson = new Gson();
                    Map<String, Object> result = new HashMap<>();
                    String promoCodeToRemove = request.getParameter("promoCode");
                    if (promoCodeToRemove != null && !promoCodeToRemove.trim().isEmpty()) {
                        cartDAO.removePromoFromCartItem(customerId, productId);
                        session.removeAttribute("promoCode_" + productId);
                        result.put("success", true);
                        result.put("message", "Promo code removed successfully");
                    } else {
                        result.put("success", false);
                        result.put("message", "No promo code to remove");
                    }
                    out.print(gson.toJson(result));
                    out.flush();
                    return;

                case "applyCartPromo":
                    String cartPromoCode = request.getParameter("promoCode");
                    if (cartPromoCode != null && !cartPromoCode.trim().isEmpty()) {
                        applyCartPromotion(request, customerId, cartPromoCode, response);
                    }
                    return;

                case "removeCartPromo":
                    response.setContentType("application/json");
                    Gson gsonRemoveCart = new Gson();
                    Map<String, Object> resultRemoveCart = new HashMap<>();
                    session.removeAttribute("cartPromoCode");
                    session.removeAttribute("discountedTotal");
                    session.removeAttribute("discountPercentage");
                    resultRemoveCart.put("success", true);
                    resultRemoveCart.put("message", "Cart promo code removed successfully");
                    out.print(gsonRemoveCart.toJson(resultRemoveCart));
                    out.flush();
                    return;

                default:
                    throw new IllegalArgumentException("Invalid action: " + action);
            }
            response.sendRedirect("CartServlet");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error in doPost: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    private void applyCartPromotion(HttpServletRequest request, int customerId, String promoCode, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        CartDAO cartDAO = new CartDAO();
        PromotionDAO promotionDAO = new PromotionDAO();
        BigDecimal originalTotal = cartDAO.calculateCartTotal(customerId);
        System.out.println("Original total for customerId " + customerId + ": " + originalTotal);

        if (originalTotal == null || originalTotal.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("success", false);
            result.put("error", "Invalid cart total: " + originalTotal);
        } else {
            Promotion promotion = promotionDAO.getPromotionByCode(promoCode);
            if (promotion == null) {
                System.out.println("Promotion not found for code: " + promoCode);
                result.put("success", false);
                result.put("error", "Invalid promo code");
            } else {
                // Kiểm tra xem khách hàng đã sử dụng mã giảm giá này (dựa trên promotion_id) chưa
                boolean hasUsedPromo = hasCustomerUsedPromotion(customerId, promotion.getPromotionId());
                if (hasUsedPromo) {
                    result.put("success", false);
                    result.put("error", "You have already used this promo code.");
                } else {
                    BigDecimal discountedTotal = cartDAO.applyCartPromotion(customerId, promoCode);
                    System.out.println("Discounted total after applying promo " + promoCode + ": " + discountedTotal);
                    if (discountedTotal != null && discountedTotal.compareTo(originalTotal) < 0 && discountedTotal.compareTo(BigDecimal.ZERO) >= 0) {
                        HttpSession session = request.getSession();
                        session.setAttribute("cartPromoCode", promoCode);
                        session.setAttribute("discountedTotal", discountedTotal.doubleValue());
                        session.setAttribute("discountPercentage", promotion.getDiscountPercentage().doubleValue());
                        result.put("success", true);
                        result.put("discountedTotal", discountedTotal.doubleValue());
                        result.put("discountPercentage", promotion.getDiscountPercentage().doubleValue());
                    } else {
                        System.out.println("Failed to apply promo code: " + promoCode + ", discountedTotal: " + discountedTotal);
                        result.put("success", false);
                        result.put("error", "Invalid promo code or no discount applied");
                    }
                }
            }
        }
        out.print(gson.toJson(result));
        out.flush();
    }

    // Phương thức kiểm tra xem khách hàng đã sử dụng mã giảm giá (dựa trên promotion_id) chưa
    private boolean hasCustomerUsedPromotion(int customerId, int promotionId) {
        String sql = "SELECT COUNT(*) FROM Orders WHERE customer_id = ? AND promotion_id = ?";
        try (Connection conn = new com.mvc.dal.DBContext().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, promotionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu có bản ghi, nghĩa là khách hàng đã sử dụng mã này
            }
        } catch (SQLException e) {
            System.err.println("Error checking Orders for promo usage: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("redirectUrl", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect("login.jsp");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        int customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
        if (customerId == -1) {
            System.err.println("CustomerId not found for user: " + user.getUserId());
            response.sendRedirect("error.jsp");
            return;
        }

        CartDAO cartDAO = new CartDAO();
        ProductDAO productDAO = new ProductDAO();
        PromotionDAO promotionDAO = new PromotionDAO();
        String action = request.getParameter("action");

        if ("calculateDiscount".equals(action)) {
            String productIdStr = request.getParameter("productId");
            String promotionCode = request.getParameter("promotionCode");

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            Map<String, Object> result = new HashMap<>();

            try {
                int productId = Integer.parseInt(productIdStr);
                Product product = productDAO.getProductById(productId);
                Promotion promotion = promotionDAO.getPromotionByCode(promotionCode);

                if (product != null && promotion != null && "Active".equals(promotion.getStatus())) {
                    BigDecimal basePrice = product.getDiscountedPrice() != null ? product.getDiscountedPrice() : product.getPrice();
                    BigDecimal discountPercentage = promotion.getDiscountPercentage();
                    BigDecimal discountFactor = discountPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
                    BigDecimal discountedPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountFactor))
                            .setScale(0, BigDecimal.ROUND_HALF_UP);
                    result.put("success", true);
                    result.put("discountedPrice", discountedPrice.doubleValue());
                } else {
                    result.put("success", false);
                    result.put("error", "Invalid product or promotion code");
                }
            } catch (Exception e) {
                result.put("success", false);
                result.put("error", "Server error: " + e.getMessage());
            }
            out.print(gson.toJson(result));
            out.flush();
            return;
        }

        List<CartItem> cartItems = cartDAO.getCartItems(customerId);
        BigDecimal totalPriceBigDecimal = cartDAO.calculateCartTotal(customerId);
        double totalPrice = (totalPriceBigDecimal != null) ? totalPriceBigDecimal.doubleValue() : 0.0;

        request.setAttribute("cartItems", cartItems != null ? cartItems : new java.util.ArrayList<>());
        request.setAttribute("totalPrice", totalPrice);
        request.setAttribute("availablePromotions", promotionDAO.getActivePromotions());

        try {
            request.getRequestDispatcher("cart.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error forwarding to cart.jsp: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading cart: " + e.getMessage());
        }
    }
}