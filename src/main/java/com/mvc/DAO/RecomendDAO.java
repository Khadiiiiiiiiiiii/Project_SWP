package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecomendDAO {

    private Product extractProduct(ResultSet rs) throws SQLException {
        Integer promotionId = rs.getObject("promotion_id") != null ? rs.getInt("promotion_id") : null;

        return new Product(
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
                promotionId,
                false
        );
    }

    public List<Product> getTopRatedProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT TOP (?) "
                + "p.product_id, p.name, CAST(p.description AS VARCHAR(MAX)) AS description, "
                + "p.price, p.discount_price, p.category_id, p.stock_quantity, p.image_url, "
                + "p.created_at, p.updated_at, p.promotion_id, "
                + "AVG(r.rating) AS avg_rating, COUNT(r.rating) AS review_count "
                + "FROM Products p "
                + "JOIN Reviews r ON p.product_id = r.product_id "
                + "WHERE p.is_deleted = 0 "
                + "GROUP BY p.product_id, p.name, CAST(p.description AS VARCHAR(MAX)), "
                + "p.price, p.discount_price, p.category_id, p.stock_quantity, p.image_url, "
                + "p.created_at, p.updated_at, p.promotion_id "
                + "HAVING AVG(r.rating) >= 4 AND COUNT(r.rating) >= 2 "
                + "ORDER BY review_count DESC"; 

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getBestSellingProducts(int limit) {
    List<Product> products = new ArrayList<>();
    String sql = "SELECT TOP (?) "
            + "p.product_id, p.name, CAST(p.description AS VARCHAR(MAX)) AS description, "
            + "p.price, p.discount_price, p.category_id, p.stock_quantity, p.image_url, "
            + "p.created_at, p.updated_at, p.promotion_id, SUM(od.quantity) AS total_sold "
            + "FROM Products p "
            + "JOIN OrderDetails od ON p.product_id = od.product_id "
            + "WHERE p.is_deleted = 0 "
            + "GROUP BY p.product_id, p.name, CAST(p.description AS VARCHAR(MAX)), "
            + "p.price, p.discount_price, p.category_id, p.stock_quantity, p.image_url, "
            + "p.created_at, p.updated_at, p.promotion_id "
            + "HAVING SUM(od.quantity) >= 2 "  // Chỉ chọn sản phẩm bán được >= 2 lần
            + "ORDER BY total_sold DESC";

    try (Connection conn = DBContext.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, limit);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            products.add(extractProduct(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

}
