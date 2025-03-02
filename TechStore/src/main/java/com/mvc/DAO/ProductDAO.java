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

    public ProductDAO() {
        System.out.println("ProductDAO initialized.");
    }

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
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
            System.out.println("getAllProducts: Retrieved " + products.size() + " products");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE category_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try ( ResultSet rs = ps.executeQuery()) {
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
                System.out.println("getProductsByCategoryId(" + categoryId + "): Retrieved " + products.size() + " products");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(int productId) {
        Product product = null;
        String query = "SELECT * FROM Products WHERE product_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price") != null ? rs.getBigDecimal("price") : null,
                            rs.getBigDecimal("discount_price") != null ? rs.getBigDecimal("discount_price") : null,
                            rs.getInt("category_id"), // Đã sửa từ "categoria_id" thành "category_id"
                            rs.getInt("stock_quantity"),
                            rs.getString("image_url"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at"),
                            rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                    );
                }
                System.out.println("getProductById(" + productId + "): " + (product != null ? "Found" : "Not found"));
            }
        } catch (SQLException e) {
            System.out.println("getProductById(" + productId + "): SQLException occurred");
            e.printStackTrace();
        }
        return product;
    }

    public List<Product> getProductsByCategory(String categoryName) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM Products p "
                + "INNER JOIN Categories c ON p.category_id = c.category_id "
                + "WHERE c.category_name = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            try ( ResultSet rs = ps.executeQuery()) {
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
                System.out.println("getProductsByCategory(" + categoryName + "): Retrieved " + products.size() + " products");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Phương thức tìm kiếm sản phẩm theo tên hoặc mô tả
    public List<Product> searchProducts(String searchQuery) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE name LIKE ? OR description LIKE ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchQuery + "%"); // Tìm kiếm tên sản phẩm
            ps.setString(2, "%" + searchQuery + "%"); // Tìm kiếm mô tả sản phẩm
            try ( ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
