package com.mvc.DAO;

import com.mvc.model.Promotion;
import com.mvc.dal.DBContext;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Promotions table, handling operations related to discount codes in the database.
 * Supports Discount Management for Store Manager and Customer.
 */
public class PromotionDAO {

    public PromotionDAO() {
        // No need to keep a static Connection
    }

    /**
     * Creates a new promotion in the database.
     * @param promotion The Promotion object to create
     * @return The ID of the newly created promotion (if successful), or -1 if failed
     */
    public int createPromotion(Promotion promotion) {
        String sql = "INSERT INTO Promotions (store_id, code, discount_percentage, expiration_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, promotion.getStoreId());
            ps.setString(2, promotion.getCode());
            ps.setBigDecimal(3, promotion.getDiscountPercentage());
            ps.setDate(4, new java.sql.Date(promotion.getExpirationDate().getTime()));
            ps.setString(5, promotion.getStatus());

            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters - store_id: " + promotion.getStoreId() +
                              ", code: " + promotion.getCode() +
                              ", discount_percentage: " + promotion.getDiscountPercentage() +
                              ", expiration_date: " + promotion.getExpirationDate() +
                              ", status: " + promotion.getStatus());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        System.out.println("Generated promotion_id: " + generatedId);
                        return generatedId;
                    } else {
                        System.err.println("No generated keys returned after insert.");
                    }
                }
            } else {
                System.err.println("No rows affected by the insert operation.");
            }
        } catch (SQLException e) {
            System.err.println("Database error during promotion creation: " + e.getMessage());
            e.printStackTrace();
            if (e.getSQLState() != null) {
                System.err.println("SQL State: " + e.getSQLState());
            }
            if (e.getErrorCode() != 0) {
                System.err.println("Error Code: " + e.getErrorCode());
            }
        }
        return -1;
    }

    /**
     * Checks if a promotion code already exists.
     * @param code The promotion code to check
     * @return true if the code already exists, false otherwise
     */
    public boolean isCodeExists(String code) {
        String sql = "SELECT COUNT(*) FROM Promotions WHERE code = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error checking code existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the information of a promotion.
     * @param promotion The Promotion object to update
     * @return true if the update is successful, false otherwise
     */
    public boolean updatePromotion(Promotion promotion) {
        String sql = "UPDATE Promotions SET code = ?, discount_percentage = ?, expiration_date = ?, status = ? WHERE promotion_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promotion.getCode());
            ps.setBigDecimal(2, promotion.getDiscountPercentage());
            ps.setDate(3, new java.sql.Date(promotion.getExpirationDate().getTime()));
            ps.setString(4, promotion.getStatus());
            ps.setInt(5, promotion.getPromotionId());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Database error during promotion update: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a promotion from the database.
     * @param promotionId The ID of the promotion to delete
     * @return true if the deletion is successful, false otherwise
     */
    public boolean deletePromotion(int promotionId) {
        String sql = "DELETE FROM Promotions WHERE promotion_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Database error during promotion deletion: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a list of all promotions.
     * @return List<Promotion> containing all promotions
     */
    public List<Promotion> getAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM Promotions";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Promotion promotion = new Promotion(
                    rs.getInt("promotion_id"),
                    rs.getString("code"),
                    rs.getBigDecimal("discount_percentage"),
                    rs.getDate("expiration_date"),
                    rs.getString("status"),
                    rs.getInt("store_id")
                );
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching all promotions: " + e.getMessage());
            e.printStackTrace();
        }
        return promotions;
    }

    /**
     * Retrieves a promotion by its ID.
     * @param promotionId The ID of the promotion
     * @return The Promotion object if found, null otherwise
     */
    public Promotion getPromotionById(int promotionId) {
        String sql = "SELECT * FROM Promotions WHERE promotion_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Promotion(
                        rs.getInt("promotion_id"),
                        rs.getString("code"),
                        rs.getBigDecimal("discount_percentage"),
                        rs.getDate("expiration_date"),
                        rs.getString("status"),
                        rs.getInt("store_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching promotion by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a promotion by its code, only if it is active and not expired.
     * @param code The promotion code (e.g., "STORE10")
     * @return The Promotion object if found and valid, null otherwise
     */
    public Promotion getPromotionByCode(String code) {
        String sql = "SELECT * FROM Promotions WHERE code = ? AND status = 'Active' AND expiration_date >= ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setDate(2, new java.sql.Date(new java.util.Date().getTime())); // Ngày hiện tại
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Promotion(
                        rs.getInt("promotion_id"),
                        rs.getString("code"),
                        rs.getBigDecimal("discount_percentage"),
                        rs.getDate("expiration_date"),
                        rs.getString("status"),
                        rs.getInt("store_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching promotion by code: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a list of active and non-expired promotions.
     * @return List<Promotion> containing active and non-expired promotions
     */
    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM Promotions WHERE status = 'Active' AND expiration_date >= ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(new java.util.Date().getTime())); // Ngày hiện tại
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promotion promotion = new Promotion(
                        rs.getInt("promotion_id"),
                        rs.getString("code"),
                        rs.getBigDecimal("discount_percentage"),
                        rs.getDate("expiration_date"),
                        rs.getString("status"),
                        rs.getInt("store_id")
                    );
                    promotions.add(promotion);
                }
            }
            System.out.println("Active promotions retrieved: " + promotions.size());
            for (Promotion promo : promotions) {
                System.out.println("Promotion: " + promo.getCode() + " (" + promo.getDiscountPercentage() + "%), Expiration: " + promo.getExpirationDate() + ", Status: " + promo.getStatus());
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching active promotions: " + e.getMessage());
            e.printStackTrace();
        }
        return promotions;
    }

    /**
     * Retrieves a list of active and non-expired promotions that have not been used by a specific customer.
     * @param customerId The ID of the customer
     * @return List<Promotion> containing active, non-expired, and unused promotions for the customer
     */
    public List<Promotion> getUnusedPromotionsByCustomer(int customerId) {
        List<Promotion> unusedPromotions = new ArrayList<>();
        String sql = "SELECT p.* FROM Promotions p " +
                    "WHERE p.status = 'Active' AND p.expiration_date >= ? " +
                    "AND p.code NOT IN (SELECT o.promo_code FROM Orders o WHERE o.customer_id = ? AND o.promo_code IS NOT NULL)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(new java.util.Date().getTime())); // Ngày hiện tại
            ps.setInt(2, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promotion promotion = new Promotion(
                        rs.getInt("promotion_id"),
                        rs.getString("code"),
                        rs.getBigDecimal("discount_percentage"),
                        rs.getDate("expiration_date"),
                        rs.getString("status"),
                        rs.getInt("store_id")
                    );
                    unusedPromotions.add(promotion);
                }
            }
            System.out.println("Unused promotions retrieved for customer " + customerId + ": " + unusedPromotions.size());
            for (Promotion promo : unusedPromotions) {
                System.out.println("Unused Promotion: " + promo.getCode() + " (" + promo.getDiscountPercentage() + "%), Expiration: " + promo.getExpirationDate() + ", Status: " + promo.getStatus());
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching unused promotions for customer: " + e.getMessage());
            e.printStackTrace();
        }
        return unusedPromotions;
    }

    /**
     * Retrieves a list of all promotions for a specific store (including both active and inactive).
     * @param storeId The ID of the store
     * @return List<Promotion> containing promotions for the specified store
     */
    public List<Promotion> getPromotionsByStoreId(int storeId) {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM Promotions WHERE store_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, storeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promotion promotion = new Promotion(
                        rs.getInt("promotion_id"),
                        rs.getString("code"),
                        rs.getBigDecimal("discount_percentage"),
                        rs.getDate("expiration_date"),
                        rs.getString("status"),
                        rs.getInt("store_id")
                    );
                    promotions.add(promotion);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching promotions: " + e.getMessage());
            e.printStackTrace();
            if (e.getSQLState() != null) {
                System.err.println("SQL State: " + e.getSQLState());
            }
            if (e.getErrorCode() != 0) {
                System.err.println("Error Code: " + e.getErrorCode());
            }
        }
        return promotions;
    }
}