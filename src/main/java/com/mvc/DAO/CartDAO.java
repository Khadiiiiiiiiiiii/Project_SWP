package com.mvc.DAO;

import com.mvc.model.CartItem;
import com.mvc.dal.DBContext;
import com.mvc.model.Product;
import com.mvc.model.Promotion;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng Cart và CartItems, xử lý các thao tác với
 * giỏ hàng trong database. Hỗ trợ Discount Management với giảm giá trực tiếp và
 * mã giảm giá.
 */
public class CartDAO {

    private Connection conn;

    public CartDAO() {
        // Không khởi tạo conn ngay trong constructor, để quản lý trong getConnection
    }

    /**
     * Lấy kết nối database, reconnect nếu cần.
     *
     * @return Connection đối tượng kết nối
     * @throws SQLException nếu không thể kết nối
     */
    private Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DBContext.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to establish database connection!");
            }
            System.out.println("Re-established database connection");
        }
        return conn;
    }

    /**
     * Đóng kết nối khi không dùng nữa.
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Lấy cart_id của Customer.
     *
     * @param customerId ID của khách hàng
     * @return cart_id nếu tìm thấy, -1 nếu không
     */
    public int getCartIdByCustomerId(int customerId) {
        String sql = "SELECT cart_id FROM Cart WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cart_id");
                } else {
                    // Nếu không có giỏ hàng, tạo mới giỏ hàng
                    return createCartForCustomer(customerId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error while fetching cart_id: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Tạo giỏ hàng mới nếu chưa có.
     *
     * @param customerId ID của khách hàng
     * @return cart_id mới tạo nếu thành công, -1 nếu thất bại
     */
    public int createCartForCustomer(int customerId) {
        String sql = "INSERT INTO Cart (customer_id, created_at, updated_at) VALUES (?, GETDATE(), GETDATE())";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int cartId = rs.getInt(1);
                        System.out.println("Created new cart for customerId: " + customerId + ", cartId: " + cartId);
                        return cartId;
                    }
                }
            } else {
                System.err.println("No rows inserted for customerId: " + customerId);
            }
        } catch (SQLException e) {
            System.err.println("Database error during creating cart: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Thêm hoặc cập nhật sản phẩm trong giỏ hàng của khách hàng.
     *
     * @param customerId ID của khách hàng
     * @param productId ID của sản phẩm
     * @param quantity Số lượng cần thêm/cập nhật
     */
    public void addOrUpdateCartItem(int customerId, int productId, int quantity) {
        int cartId = getCartIdByCustomerId(customerId);
        System.out.println("Attempting to add productId: " + productId + " for customerId: " + customerId + ", cartId: " + cartId);
        if (cartId == -1) {
            cartId = createCartForCustomer(customerId);
            System.out.println("Created new cartId: " + cartId);
        }
        if (cartId == -1) {
            System.err.println("Failed to get or create cart for customerId: " + customerId);
            return;
        }

        CartItem existingCartItem = getCartItemByProduct(customerId, productId);
        if (existingCartItem != null) {
            int newQuantity = existingCartItem.getQuantity() + quantity;
            updateCartItem(existingCartItem.getCartItemId(), newQuantity);
            System.out.println("Updated quantity for productId: " + productId + " to " + newQuantity);
        } else {
            String sql = "INSERT INTO CartItems (cart_id, product_id, quantity, added_at) VALUES (?, ?, ?, GETDATE())";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ps.setInt(3, quantity);
                int rowsAffected = ps.executeUpdate();
                System.out.println("Inserted new cart item, rows affected: " + rowsAffected);
            } catch (SQLException e) {
                System.err.println("Database error during adding cart item: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Cập nhật số lượng của một mục trong giỏ hàng.
     *
     * @param cartItemId ID của mục trong giỏ hàng
     * @param newQuantity Số lượng mới
     */
    public void updateCartItem(int cartItemId, int newQuantity) {
        String sql = "UPDATE CartItems SET quantity = ?, added_at = GETDATE() WHERE cart_item_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartItemId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated cart item ID: " + cartItemId + " to quantity: " + newQuantity);
            } else {
                System.err.println("No rows updated! Check if cart item ID exists: " + cartItemId);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật giỏ hàng: " + e.getMessage());
        }
    }

    /**
     * Xóa một mục khỏi giỏ hàng.
     *
     * @param cartItemId ID của mục cần xóa
     */
    public void removeCartItem(int cartItemId) {
        String sql = "DELETE FROM CartItems WHERE cart_item_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Removed cart item ID: " + cartItemId);
            } else {
                System.err.println("No rows deleted! Check if cart item ID exists: " + cartItemId);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage());
        }
    }

    /**
     * Lấy tất cả các mục trong giỏ hàng của khách hàng, bao gồm thông tin sản
     * phẩm và giảm giá.
     *
     * @param customerId ID của khách hàng
     * @return List<CartItem> chứa các mục trong giỏ hàng
     */
    public List<CartItem> getCartItems(int customerId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.quantity, ci.added_at, "
                + "p.price, p.discount_price, p.promotion_id "
                + "FROM CartItems ci "
                + "JOIN Cart c ON ci.cart_id = c.cart_id "
                + "JOIN Products p ON ci.product_id = p.product_id "
                + "WHERE c.customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setCartItemId(rs.getInt("cart_item_id"));
                    item.setCartId(rs.getInt("cart_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setAddedAt(rs.getTimestamp("added_at"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setDiscountPrice(rs.getBigDecimal("discount_price"));
                    item.setPromotionId(rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id"));
                    cartItems.add(item);
                }
                System.out.println("Fetched " + cartItems.size() + " cart items for customerId: " + customerId + ", Items: " + cartItems);
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching cart items: " + e.getMessage());
            e.printStackTrace();
        }
        return cartItems;
    }

    /**
     * Lấy thông tin một mục cụ thể trong giỏ hàng dựa trên customerId và
     * productId.
     *
     * @param customerId ID của khách hàng
     * @param productId ID của sản phẩm
     * @return CartItem nếu tìm thấy, null nếu không
     */
    public CartItem getCartItemByProduct(int customerId, int productId) {
        CartItem cartItem = null;
        String sql = "SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.quantity, ci.added_at, "
                + "p.price, p.discount_price, p.promotion_id "
                + "FROM CartItems ci "
                + "JOIN Cart c ON ci.cart_id = c.cart_id "
                + "JOIN Products p ON ci.product_id = p.product_id "
                + "WHERE c.customer_id = ? AND ci.product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cartItem = new CartItem();
                    cartItem.setCartItemId(rs.getInt("cart_item_id"));
                    cartItem.setCartId(rs.getInt("cart_id"));
                    cartItem.setProductId(rs.getInt("product_id"));
                    cartItem.setQuantity(rs.getInt("quantity"));
                    cartItem.setAddedAt(rs.getTimestamp("added_at"));
                    cartItem.setPrice(rs.getBigDecimal("price"));
                    cartItem.setDiscountPrice(rs.getBigDecimal("discount_price"));
                    cartItem.setPromotionId(rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id"));
                } else {
                    System.out.println("No cart item found for customerId: " + customerId + ", productId: " + productId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching cart item by product: " + e.getMessage());
            e.printStackTrace();
        }
        return cartItem;
    }

    /**
     * Áp dụng mã giảm giá cho một sản phẩm trong giỏ hàng (lưu vào database).
     *
     * @param customerId ID của khách hàng
     * @param productId ID của sản phẩm
     * @param promoCode Mã giảm giá cần áp dụng
     */
    public void applyPromoToCartItem(int customerId, int productId, String promoCode) {
        // Tạm thời bỏ qua vì cột promo_code và discount_percentage không tồn tại
        System.out.println("Applying promo to cart item skipped due to missing columns in CartItems table");
    }

    /**
     * Xóa mã giảm giá khỏi một sản phẩm trong giỏ hàng.
     *
     * @param customerId ID của khách hàng
     * @param productId ID của sản phẩm
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean removePromoFromCartItem(int customerId, int productId) {
        // Tạm thời bỏ qua vì cột promo_code và discount_percentage không tồn tại
        System.out.println("Removing promo from cart item skipped due to missing columns in CartItems table");
        return false;
    }

    /**
     * Tính tổng giá trị của giỏ hàng, áp dụng cả giảm giá trực tiếp và mã giảm
     * giá.
     *
     * @param customerId ID của khách hàng
     * @return BigDecimal tổng giá trị trước khi áp mã giảm giá
     */
    public BigDecimal calculateCartTotal(int customerId) {
        BigDecimal total = BigDecimal.ZERO;
        List<CartItem> cartItems = getCartItems(customerId);
        System.out.println("Cart items retrieved: " + cartItems);

        if (cartItems == null || cartItems.isEmpty()) {
            System.err.println("No cart items found for customerId: " + customerId);
            return total;
        }

        for (CartItem item : cartItems) {
            if (item != null) {
                BigDecimal itemPrice = item.getTotalPrice(); // Tổng giá trước khi áp mã
                total = total.add(itemPrice != null ? itemPrice : BigDecimal.ZERO);
                System.out.println("Item total for productId: " + item.getProductId() + " = " + itemPrice);
            }
        }
        System.out.println("Cart total before promotion: " + total);
        return total;
    }

    /**
     * Áp dụng mã giảm giá cho toàn bộ giỏ hàng.
     *
     * @param customerId ID của khách hàng
     * @param promoCode Mã giảm giá cần áp dụng
     * @return BigDecimal tổng giá sau khi áp dụng mã giảm giá
     */
    public BigDecimal applyCartPromotion(int customerId, String promoCode) {
        BigDecimal originalTotal = calculateCartTotal(customerId); // Tổng trước khi áp mã
        if (originalTotal == null || originalTotal.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Original total is invalid: " + originalTotal);
            return originalTotal;
        }

        PromotionDAO promotionDAO = new PromotionDAO();
        Promotion promotion = promotionDAO.getPromotionByCode(promoCode);
        if (promotion == null) {
            System.err.println("Promotion not found for promo code: " + promoCode);
            return originalTotal;
        }

        // Ghi log chi tiết về trạng thái của promotion
        System.out.println("Promotion details - Code: " + promoCode + ", Status: " + promotion.getStatus() +
                ", Expiration: " + promotion.getExpirationDate() + ", Discount Percentage: " + promotion.getDiscountPercentage());

        // So sánh ngày hết hạn chỉ dựa trên ngày (bỏ qua thời gian)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Date expirationDate = promotion.getExpirationDate();
        String currentDateStr = dateFormat.format(currentDate);
        String expirationDateStr = dateFormat.format(expirationDate);
        boolean isNotExpired = currentDateStr.compareTo(expirationDateStr) <= 0;

        if ("Active".equals(promotion.getStatus()) && isNotExpired) {
            BigDecimal discountPercentage = promotion.getDiscountPercentage();
            if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountFactor = discountPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
                BigDecimal discountedTotal = originalTotal.multiply(BigDecimal.ONE.subtract(discountFactor))
                        .setScale(0, BigDecimal.ROUND_HALF_UP);
                System.out.println("Applied promo code: " + promoCode + ", Discounted total: " + discountedTotal);
                return discountedTotal;
            } else {
                System.err.println("Discount percentage is invalid or zero for promo code: " + promoCode + ", Discount Percentage: " + discountPercentage);
            }
        } else {
            System.err.println("Invalid or expired promo code: " + promoCode + ", Status: " + promotion.getStatus() +
                    ", Expiration: " + (promotion.getExpirationDate() != null ? promotion.getExpirationDate() : "null") +
                    ", Current Date: " + currentDateStr + ", Expiration Date: " + expirationDateStr);
        }
        return originalTotal; // Trả về tổng gốc nếu mã không hợp lệ
    }

    public static void main(String[] args) {
        CartDAO cd = new CartDAO();
        cd.removeCartItem(19);
    }
}