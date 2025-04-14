package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.Review;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewsManagementDAO {

    // Lấy tất cả các đánh giá từ cơ sở dữ liệu
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM Reviews";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setProductId(rs.getInt("product_id"));
                review.setCustomerId(rs.getInt("customer_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setReply(rs.getString("reply"));
                review.setCreatedAt(rs.getTimestamp("created_at"));
                review.setUpdatedAt(rs.getTimestamp("updated_at"));
                review.setReplyAt(rs.getTimestamp("reply_at")); 
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM Reviews WHERE review_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean respondToReview(int reviewId, String reply) {
        String sql = "UPDATE Reviews SET reply = ?, reply_at = GETDATE() WHERE review_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reply);
            stmt.setInt(2, reviewId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

   public Review getReplyInfoByReviewId(int reviewId) {
    String sql = "SELECT reply, reply_at FROM Reviews WHERE review_id = ?";
    try (Connection conn = DBContext.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, reviewId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Review review = new Review();
            review.setReply(rs.getString("reply"));
            review.setReplyAt(rs.getTimestamp("reply_at"));
            return review;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

}