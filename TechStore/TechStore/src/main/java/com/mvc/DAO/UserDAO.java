package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public User login(String email, String password) throws SQLException {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT user_id, email, role, first_name, last_name, phone, address "
                + "FROM Users "
                + "WHERE email = ? AND password_hash = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            stmt.setString(2, password.trim());  // Trong thực tế nên hash password trước

            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            throw e;
        }

        return null;
    }

    // ✅ Thêm phương thức đăng ký chỉ dành cho CUSTOMER
    public boolean register(String email, String password, String firstName, String lastName, String phone, String address) throws SQLException {
        if (email == null || password == null || firstName == null || lastName == null
                || email.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            return false;
        }

        // Kiểm tra xem email đã tồn tại chưa
        if (isEmailExist(email)) {
            return false; // Không cho phép đăng ký nếu email đã tồn tại
        }

        String sql = "INSERT INTO Users (email, password_hash, role, first_name, last_name, phone, address) "
                + "VALUES (?, ?, 'customer', ?, ?, ?, ?)";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            stmt.setString(2, password.trim()); // Trong thực tế, cần mã hóa mật khẩu trước khi lưu
            stmt.setString(3, firstName.trim());
            stmt.setString(4, lastName.trim());
            stmt.setString(5, phone != null ? phone.trim() : null);
            stmt.setString(6, address != null ? address.trim() : null);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            throw e;
        }
    }

    // ✅ Phương thức kiểm tra email đã tồn tại chưa
    private boolean isEmailExist(String email) throws SQLException {
        String sql = "SELECT email FROM Users WHERE email = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());

            try ( ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Nếu có dữ liệu, email đã tồn tại
            }
        }
    }

    public User findOrCreateGoogleUser(String email, String firstName, String lastName) throws SQLException {
        // First try to find existing user
        User existingUser = findUserByEmail(email);
        if (existingUser != null) {
            return existingUser;
        }

        // If user doesn't exist, create new one
        String sql = "INSERT INTO Users (email, role, first_name, last_name) VALUES (?, 'customer', ?, ?)";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try ( ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        User newUser = new User();
                        newUser.setUserId(generatedKeys.getInt(1));
                        newUser.setEmail(email);
                        newUser.setRole("customer");
                        newUser.setFirstName(firstName);
                        newUser.setLastName(lastName);
                        return newUser;
                    }
                }
            }
        }
        return null;
    }

    public User findUserByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, email, role, first_name, last_name, phone, address FROM Users WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    return user;
                }
            }
        }
        return null;
    }

    public boolean updateProfile(int userId, String firstName, String lastName, String phone, String address) throws SQLException {
        if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            return false; // Bắt buộc có tên
        }

        String sql = "UPDATE Users SET first_name = ?, last_name = ?, phone = ?, address = ? WHERE user_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName.trim());
            stmt.setString(2, lastName.trim());
            stmt.setString(3, (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null);
            stmt.setString(4, (address != null && !address.trim().isEmpty()) ? address.trim() : null);
            stmt.setInt(5, userId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Database error during profile update: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

}
