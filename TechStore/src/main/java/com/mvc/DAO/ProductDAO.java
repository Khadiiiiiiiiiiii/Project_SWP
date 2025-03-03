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

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        
        try {
            System.out.println("Executing query: " + sql);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setDiscountPrice(rs.getBigDecimal("discount_price"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setImageUrl(rs.getString("image_url"));
                product.setCreatedAt(rs.getTimestamp("created_at"));
                product.setUpdatedAt(rs.getTimestamp("updated_at"));
                product.setPromotionId(rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id"));
                
                products.add(product);
                System.out.println("Added product: " + product.getName() + " - Price: " + product.getPrice());
            }
            System.out.println("Total products retrieved: " + products.size());
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.getAllProducts();
        System.out.println("Test result - Total products: " + products.size());
        for (Product p : products) {
            System.out.println("Product: " + p.getName() + " - Price: " + p.getPrice());
        }
    }
}