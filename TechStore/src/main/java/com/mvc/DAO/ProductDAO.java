package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection conn;

    public ProductDAO() {
        conn = DBContext.getConnection();
        System.out.println("ProductDAO initialized. Connection: " + (conn != null ? "Established" : "Null"));
    }

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getBigDecimal("discount_price"),
                    rs.getInt("category_id"),
                    rs.getInt("stock_quantity"),
                    rs.getString("image_url"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getInt("promotion_id")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(int productId) {
        Product product = null;
        String query = "SELECT * FROM Products WHERE product_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price") != null ? rs.getBigDecimal("price") : null,
                    rs.getBigDecimal("discount_price") != null ? rs.getBigDecimal("discount_price") : null,
                    rs.getInt("category_id"),
                    rs.getInt("stock_quantity"),
                    rs.getString("image_url"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }
}
