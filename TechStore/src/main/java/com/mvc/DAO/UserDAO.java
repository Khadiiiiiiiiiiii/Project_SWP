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
            stmt.setString(2, password.trim());

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

     // ✅ Cập nhật register() để tự động thêm vào Customer nếu role = 'customer'
    public boolean register(String email, String password, String firstName, String lastName, String phone, String address) throws SQLException {
        if (email == null || password == null || firstName == null || lastName == null
                || email.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            return false;
        }

        if (isEmailExist(email)) {
            return false;
        }

        String sqlUser = "INSERT INTO Users (email, password_hash, role, first_name, last_name, phone, address) "
                + "VALUES (?, ?, 'customer', ?, ?, ?, ?)";

        String sqlCustomer = "INSERT INTO Customer (user_id, loyalty_points, preferred_payment_method, delivery_address) "
                           + "VALUES (?, 0, NULL, NULL)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {

            stmtUser.setString(1, email.trim());
            stmtUser.setString(2, password.trim());
            stmtUser.setString(3, firstName.trim());
            stmtUser.setString(4, lastName.trim());
            stmtUser.setString(5, phone != null ? phone.trim() : null);
            stmtUser.setString(6, address != null ? address.trim() : null);

            int rowsInserted = stmtUser.executeUpdate();

            if (rowsInserted > 0) {
                // Lấy user_id vừa tạo
                ResultSet rs = stmtUser.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);

                    // Thêm vào Customer
                    try (PreparedStatement stmtCustomer = conn.prepareStatement(sqlCustomer)) {
                        stmtCustomer.setInt(1, userId);
                        stmtCustomer.executeUpdate();
                    }
                }
            }
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            throw e;
        }
    }

    public boolean isEmailExist(String email) throws SQLException {
        String sql = "SELECT email FROM Users WHERE email = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());

            try ( ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User findOrCreateGoogleUser(String email, String firstName, String lastName) throws SQLException {
        User existingUser = findUserByEmail(email);
        if (existingUser != null) {
            return existingUser;
        }

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

    public boolean updateProfile(int userId, String firstName, String lastName, String phone, String address) throws SQLException {
        if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            return false;
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

    public boolean validatePassword(String email, String password) throws SQLException {
        String sql = "SELECT password_hash FROM Users WHERE email = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password_hash"); // Mật khẩu lấy từ DB
                return password.equals(storedPassword); // So sánh trực tiếp
            }
        }
        return false; // Trả về false nếu không tìm thấy email
    }

    public boolean updatePassword(String email, String newPassword) throws SQLException {
        String sql = "UPDATE Users SET password_hash = ? WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword.trim());
            stmt.setString(2, email.trim());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

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
        return null;
    }

    public boolean saveResetToken(String email, String token) throws SQLException {
        String sql = "UPDATE Users SET reset_token = ? WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePasswordByEmail(String email, String newPassword) throws SQLException {
        String sql = "UPDATE Users SET password_hash = ? WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        }
    }

}
