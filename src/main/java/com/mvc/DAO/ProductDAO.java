package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng Products, xử lý các thao tác với thông tin sản phẩm trong database. Hỗ trợ Discount Management với giảm giá trực tiếp và mã giảm giá.
 */
public class ProductDAO {

    public ProductDAO() {
        System.out.println("ProductDAO initialized.");
    }

    // Lấy sản phẩm theo phạm vi giá và categoryId (null nếu lấy tất cả)
    public List<Product> getProductsByPriceRange(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE 1=1 AND is_deleted = 0");

        if (categoryId != null) {
            sql.append(" AND category_id = ?");
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
        }

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (categoryId != null) {
                ps.setInt(index++, categoryId);
            }
            if (minPrice != null) {
                ps.setBigDecimal(index++, minPrice);
            }
            if (maxPrice != null) {
                ps.setBigDecimal(index++, maxPrice);
            }

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
                            rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                    );
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching products by price range: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Lấy danh sách tất cả các sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE is_deleted = 0";

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
                        rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                );
                products.add(product);
            }
            System.out.println("getAllProducts: Retrieved " + products.size() + " products");
        } catch (SQLException e) {
            System.err.println("Database error during fetching all products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Lấy danh sách sản phẩm theo categoryId
    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE category_id = ? AND is_deleted = 0";

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
                            rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                    );
                    products.add(product);
                }
                System.out.println("getProductsByCategoryId(" + categoryId + "): Retrieved " + products.size() + " products");
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching products by categoryId: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Lấy thông tin một sản phẩm theo productId
    public Product getProductById(int productId) {
        Product product = null;
        String query = "SELECT * FROM Products WHERE product_id = ? AND is_deleted = 0";

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
                            rs.getInt("category_id"),
                            rs.getInt("stock_quantity"),
                            rs.getString("image_url"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at"),
                            rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                    );
                    System.out.println("getProductById(" + productId + "): Fetched - price=" + product.getPrice()
                            + ", discountPrice(%)=" + product.getDiscountPrice()
                            + ", calculated discountPrice=" + product.getDiscountedPrice());
                } else {
                    System.out.println("getProductById(" + productId + "): Not found");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching product by ID: " + e.getMessage());
            System.out.println("getProductById(" + productId + "): SQLException occurred");
            e.printStackTrace();
        }
        return product;
    }

    // Lấy danh sách sản phẩm theo tên category
    public List<Product> getProductsByCategory(String categoryName) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM Products p "
                + "INNER JOIN Categories c ON p.category_id = c.category_id "
                + "WHERE c.category_name = ? AND is_deleted = 0";

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
                            rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                    );
                    products.add(product);
                }
                System.out.println("getProductsByCategory(" + categoryName + "): Retrieved " + products.size() + " products");
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching products by category name: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Phương thức tìm kiếm sản phẩm theo tên hoặc mô tả
    public List<Product> searchProducts(String searchQuery) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE name LIKE ? OR description LIKE ? AND is_deleted = 0";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchQuery + "%");
            ps.setString(2, "%" + searchQuery + "%");
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
                            rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                    );
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during searching products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Phương thức lấy sản phẩm giảm giá (có discount_price)
    public List<Product> getDiscountedProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE discount_price IS NOT NULL AND discount_price > 0 AND is_deleted = 0";

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
                        rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                );
                products.add(product);
            }
            System.out.println("getDiscountedProducts: Retrieved " + products.size() + " discounted products");
        } catch (SQLException e) {
            System.err.println("Database error during fetching discounted products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

// Phương thức lấy sản phẩm có giảm giá và trong khoảng giá (minPrice, maxPrice)
    public List<Product> getDiscountedProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE discount_price IS NOT NULL AND discount_price > 0 AND is_deleted = 0");

        if (minPrice != null) {
            sql.append(" AND price >= ?");
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
        }

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (minPrice != null) {
                ps.setBigDecimal(index++, minPrice);
            }
            if (maxPrice != null) {
                ps.setBigDecimal(index++, maxPrice);
            }

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
                            rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id")
                    );
                    products.add(product);
                }
            }
            System.out.println("getDiscountedProductsByPriceRange: Retrieved " + products.size() + " discounted products");
        } catch (SQLException e) {
            System.err.println("Database error during fetching discounted products by price range: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Các phương thức khác giữ nguyên...
    public void updateProductDiscount(int productId, BigDecimal discountPrice) {
        String sql = "UPDATE Products SET discount_price = ?, updated_at = GETDATE() WHERE product_id = ? AND is_deleted = 0";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, discountPrice);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database error during updating product discount: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateBulkProductDiscount(List<Integer> productIds, BigDecimal discountPrice) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        String sql = "UPDATE Products SET discount_price = ?, updated_at = GETDATE() WHERE product_id = ? AND is_deleted = 0";
        try ( Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try ( PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Integer productId : productIds) {
                    ps.setBigDecimal(1, discountPrice);
                    ps.setInt(2, productId);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Database error during bulk product discount update: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Database error during bulk product discount setup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Product> getProductsWithPromotion() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE promotion_id IS NOT NULL AND is_deleted = 0";

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
        } catch (SQLException e) {
            System.err.println("Database error during fetching products with promotion: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products ";

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
                        rs.getObject("promotion_id", Integer.class),
                        rs.getBoolean("is_deleted")
                );
                products.add(product);
            }
            System.out.println("getAll: Retrieved " + products.size() + " products");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getAll: SQL Error - " + e.getMessage());
        }
        return products;
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO Products (name, description, price, discount_price, category_id, stock_quantity, image_url, promotion_id, created_at, updated_at, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setObject(3, product.getPrice());
            stmt.setObject(4, product.getDiscountPrice());
            stmt.setInt(5, product.getCategoryId());
            stmt.setInt(6, product.getStockQuantity());
            stmt.setString(7, product.getImageUrl());
            stmt.setObject(8, product.getPromotionId());
            stmt.setBoolean(9, false);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product getProductManagementById(int productId) {
        Product product = null;
        String query = "SELECT * FROM Products WHERE product_id = ? AND is_deleted = 0";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setDiscountPrice(rs.getBigDecimal("discount_price"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setImageUrl(rs.getString("image_url"));
                product.setPromotionId(rs.getInt("promotion_id") == 0 ? null : rs.getInt("promotion_id"));
                product.setIsDeleted(rs.getBoolean("is_deleted"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE Products SET name = ?, description = ?, price = ?, discount_price = ?, "
                + "category_id = ?, stock_quantity = ?, image_url = ?, promotion_id = ?, "
                + "updated_at = CURRENT_TIMESTAMP WHERE product_id = ?";
        boolean success = false;

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setBigDecimal(4, product.getDiscountPrice() != null ? product.getDiscountPrice() : null);
            ps.setInt(5, product.getCategoryId());
            ps.setInt(6, product.getStockQuantity());
            ps.setString(7, product.getImageUrl());
            ps.setObject(8, product.getPromotionId(), java.sql.Types.INTEGER);
            ps.setInt(9, product.getProductId());

            int rowsAffected = ps.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public void setProductStatus(int productId, boolean isDeleted) {
        String sql = "UPDATE Products SET is_deleted = ? WHERE product_id = ?";
        try ( Connection connection = DBContext.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, isDeleted);
            preparedStatement.setInt(2, productId);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("setProductStatus: Rows affected for productId " + productId + ": " + rowsAffected);
            if (rowsAffected == 0) {
                System.out.println("Warning: No rows updated for productId " + productId);
            }
        } catch (SQLException e) {
            System.out.println("setProductStatus: SQLException for productId " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Product> getAllProductsForManagement() {
        String sql = "SELECT * FROM Products";
        List<Product> products = new ArrayList<>();
        try ( Connection connection = DBContext.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(sql);  ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            System.out.println("getAllProductsForManagement: Retrieved " + products.size() + " products");
        } catch (SQLException e) {
            System.out.println("getAllProductsForManagement: SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getActiveProducts() {
        String sql = "SELECT * FROM Products WHERE is_deleted = 0";
        List<Product> products = new ArrayList<>();
        try ( Connection connection = DBContext.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(sql);  ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            System.out.println("getActiveProducts: Retrieved " + products.size() + " products");
        } catch (SQLException e) {
            System.out.println("getActiveProducts: SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        int productId = rs.getInt("product_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        BigDecimal price = rs.getBigDecimal("price");
        BigDecimal discountPrice = rs.getBigDecimal("discount_price");
        int categoryId = rs.getInt("category_id");
        int stockQuantity = rs.getInt("stock_quantity");
        String imageUrl = rs.getString("image_url");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        Integer promotionId = rs.getInt("promotion_id");
        if (rs.wasNull()) {
            promotionId = null;
        }
        boolean isDeleted = rs.getBoolean("is_deleted");

        return new Product(productId, name, description, price, discountPrice, categoryId, stockQuantity, imageUrl, createdAt, updatedAt, promotionId, isDeleted);
    }

    public List<Product> getAllforDetail() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products ";

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
                        rs.getTimestamp("update_at"),
                        rs.getObject("promotion_id", Integer.class),
                        rs.getBoolean("is_deleted")
                );
                products.add(product);
            }
            System.out.println("getAll: Retrieved " + products.size() + " products");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getAll: SQL Error - " + e.getMessage());
        }
        return products;
    }

    public Product getProductManagementByIdforDetail(int productId) {
        Product product = null;
        String query = "SELECT * FROM Products WHERE product_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                product = new Product();
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
                product.setIsDeleted(rs.getBoolean("is_deleted"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public boolean isPromotionIdExists(int promotionId) {
        // Kết nối cơ sở dữ liệu và kiểm tra
        String sql = "SELECT COUNT(*) FROM Products WHERE promotion_id = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, promotionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu promotion_id tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getQuantityOfAProductInCart(int productId, int userId) {
        int quantity = 0;
        String sql = "select quantity from Cart\n"
                + "join CartItems on Cart.cart_id = CartItems.cart_id\n"
                + "where product_id = ? and customer_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    quantity = rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quantity;
    }
}
