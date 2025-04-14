package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.Review;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) cho bảng Reviews, xử lý các thao tác với đánh giá sản phẩm.
 */
public class ReviewDAO {
    public ReviewDAO() {
        // Constructor rỗng, không khởi tạo connection
    }

    /**
     * Lấy đánh giá dựa trên productId và customerId.
     * @param productId ID sản phẩm
     * @param customerId ID khách hàng
     * @return Đối tượng Review nếu tìm thấy, null nếu không
     */
    public Review getReviewByCustomerAndProduct(int productId, int customerId) {
        String sql = "SELECT review_id, product_id, customer_id, rating, comment, created_at, updated_at " +
                    "FROM Reviews WHERE product_id = ? AND customer_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setProductId(rs.getInt("product_id"));
                    review.setCustomerId(rs.getInt("customer_id"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setCreatedAt(rs.getTimestamp("created_at"));
                    review.setUpdatedAt(rs.getTimestamp("updated_at")); // Thêm lấy giá trị updated_at
                    return review;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching review: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tạo đánh giá mới.
     * @param productId ID sản phẩm
     * @param customerId ID khách hàng
     * @param rating Đánh giá (1-5)
     * @param comment Bình luận
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean createReview(int productId, int customerId, int rating, String comment) {
        String sql = "INSERT INTO Reviews (product_id, customer_id, rating, comment, created_at) VALUES (?, ?, ?, ?, GETDATE())";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, customerId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Database error during creating review: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật đánh giá.
     * @param reviewId ID đánh giá
     * @param rating Đánh giá mới
     * @param comment Bình luận mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateReview(int reviewId, int rating, String comment) {
        String sql = "UPDATE Reviews SET rating = ?, comment = ?, updated_at = GETDATE() WHERE review_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, reviewId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Database error during updating review: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa đánh giá.
     * @param reviewId ID đánh giá
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM Reviews WHERE review_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Database error during deleting review: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy tất cả đánh giá của một sản phẩm.
     * @param productId ID sản phẩm
     * @return Danh sách Review (cần triển khai List nếu cần)
     */
    public Review[] getReviewsByProductId(int productId) {
        String sql = "SELECT review_id, product_id, customer_id, rating, comment, created_at, updated_at " +
                    "FROM Reviews WHERE product_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                java.util.List<Review> reviews = new java.util.ArrayList<>();
                while (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setProductId(rs.getInt("product_id"));
                    review.setCustomerId(rs.getInt("customer_id"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setCreatedAt(rs.getTimestamp("created_at"));
                    review.setUpdatedAt(rs.getTimestamp("updated_at")); // Thêm lấy giá trị updated_at
                    reviews.add(review);
                }
                return reviews.toArray(new Review[0]);
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching reviews: " + e.getMessage());
            e.printStackTrace();
        }
        return new Review[0]; // Trả về mảng rỗng nếu lỗi
    }
}