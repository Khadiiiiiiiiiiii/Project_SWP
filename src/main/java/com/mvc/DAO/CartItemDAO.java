package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class CartItemDAO {

    private Connection conn;

    public CartItemDAO() {
        conn = DBContext.getConnection();
    }

    // Lấy cart_id của Customer
    public int getCartIdByCustomerId(int customerId) {
        String sql = "SELECT cart_id FROM Cart WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("cart_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy cart_id
    }

    // Tạo giỏ hàng mới nếu chưa có
    public int createCartForCustomer(int customerId) {
        String sql = "INSERT INTO Cart (customer_id, created_at, updated_at) VALUES (?, GETDATE(), GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về cart_id mới tạo
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Ensure cart exists or create it if not
public void addCartItem(int customerId, int productId, int quantity) {
    int cartId = getCartIdByCustomerId(customerId);
    if (cartId == -1) {
        cartId = createCartForCustomer(customerId);  // If cart doesn't exist, create it
    }
    if (cartId == -1) {
        return;
    }

    // Now we check if the item already exists in the cart
    CartItem existingCartItem = getCartItemByProduct(customerId, productId);
    if (existingCartItem != null) {
        // If the item exists, update the quantity
        int newQuantity = existingCartItem.getQuantity() + quantity;
        updateCartItem(existingCartItem.getCartItemId(), newQuantity);
    } else {
        // If the item doesn't exist, add a new entry to the cart
        String sql = "INSERT INTO CartItems (cart_id, product_id, quantity, added_at) VALUES (?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    public void updateCartItem(int cartItemId, int newQuantity) {
        String sql = "UPDATE CartItems SET quantity = ? WHERE cart_item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeCartItem(int cartItemId) {
        String sql = "DELETE FROM CartItems WHERE cart_item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả sản phẩm trong giỏ hàng của customer
    public List<CartItem> getCartItems(int customerId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT cart_item_id, product_id, quantity FROM CartItems WHERE cart_id = (SELECT cart_id FROM Cart WHERE customer_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("cart_item_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                cartItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    // Kiểm tra nếu sản phẩm đã có trong giỏ hàng
    public CartItem getCartItemByProduct(int customerId, int productId) {
        CartItem cartItem = null;
        String sql = "SELECT * FROM CartItems WHERE cart_id = (SELECT cart_id FROM Cart WHERE customer_id = ?) AND product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cartItem = new CartItem();
                cartItem.setCartItemId(rs.getInt("cart_item_id"));
                cartItem.setCartId(rs.getInt("cart_id"));
                cartItem.setProductId(rs.getInt("product_id"));
                cartItem.setQuantity(rs.getInt("quantity"));
                cartItem.setAddedAt(rs.getTimestamp("added_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItem;
    }

}
