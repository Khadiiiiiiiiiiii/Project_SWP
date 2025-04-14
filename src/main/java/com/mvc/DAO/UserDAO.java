package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Data Access Object (DAO) cho bảng Users, xử lý các thao tác với thông tin người dùng trong database. Hỗ trợ Discount Management với phân quyền cho Admin, Store Manager, Customer, và Staff.
 */
public class UserDAO {

    /**
     * Đăng nhập người dùng dựa trên email và mật khẩu.
     *
     * @param email Địa chỉ email của người dùng
     * @param password Mật khẩu (chưa mã hóa)
     * @return Đối tượng User nếu đăng nhập thành công, null nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    public User login(String email, String password) throws SQLException {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT user_id, email, password_hash, role, first_name, last_name, phone, address, created_at, updated_at, status "
                + "FROM Users "
                + "WHERE email = ? AND password_hash = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            stmt.setString(2, password.trim());

            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("disable".equalsIgnoreCase(status)) {
                        return null; // Tài khoản bị khóa, trả về null
                    }
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    // user.setStatus(status);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Đăng ký người dùng mới với vai trò mặc định là 'customer' và thêm vào cả bảng Customer.
     *
     * @param email Địa chỉ email
     * @param password Mật khẩu (chưa mã hóa)
     * @param firstName Tên
     * @param lastName Họ
     * @param phone Số điện thoại
     * @param address Địa chỉ
     * @return true nếu đăng ký thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    public boolean register(String email, String password, String firstName, String lastName, String phone, String address) throws SQLException {
        if (email == null || password == null || firstName == null || lastName == null
                || email.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            return false;
        }

        if (isEmailExist(email)) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmtUsers = null;
        PreparedStatement stmtCustomer = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Chèn dữ liệu vào bảng Users với status = 'active'
            String sqlUsers = "INSERT INTO Users (email, password_hash, role, first_name, last_name, phone, address, status, created_at, updated_at) "
                    + "VALUES (?, ?, 'customer', ?, ?, ?, ?, 'active', GETDATE(), GETDATE())";

            stmtUsers = conn.prepareStatement(sqlUsers, Statement.RETURN_GENERATED_KEYS);
            stmtUsers.setString(1, email.trim());
            stmtUsers.setString(2, password.trim()); // Nên mã hóa mật khẩu trong thực tế
            stmtUsers.setString(3, firstName.trim());
            stmtUsers.setString(4, lastName.trim());
            stmtUsers.setString(5, phone != null ? phone.trim() : null);
            stmtUsers.setString(6, address != null ? address.trim() : null);

            int rowsInsertedUsers = stmtUsers.executeUpdate();
            if (rowsInsertedUsers == 0) {
                conn.rollback();
                return false;
            }

            // Lấy user_id được tạo tự động
            generatedKeys = stmtUsers.getGeneratedKeys();
            int userId;
            if (generatedKeys.next()) {
                userId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // 2. Chèn dữ liệu vào bảng Customer với loyalty_points = 100 và status = 'active'
            String sqlCustomer = "INSERT INTO Customer (user_id, loyalty_points, preferred_payment_method, delivery_address, status) "
                    + "VALUES (?, 100, NULL, ?, 'active')";

            stmtCustomer = conn.prepareStatement(sqlCustomer);
            stmtCustomer.setInt(1, userId);
            stmtCustomer.setString(2, address != null ? address.trim() : null); // Sử dụng address từ Users làm delivery_address

            int rowsInsertedCustomer = stmtCustomer.executeUpdate();
            if (rowsInsertedCustomer == 0) {
                conn.rollback();
                return false;
            }

            // Cam kết giao dịch
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có lỗi
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Database error during registration: " + e.getMessage());
            throw e;
        } finally {
            // Đóng các tài nguyên
            if (generatedKeys != null) try {
                generatedKeys.close();
            } catch (SQLException e) {
            }
            if (stmtUsers != null) try {
                stmtUsers.close();
            } catch (SQLException e) {
            }
            if (stmtCustomer != null) try {
                stmtCustomer.close();
            } catch (SQLException e) {
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Khôi phục chế độ tự động cam kết
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * Kiểm tra xem email đã tồn tại trong database chưa.
     *
     * @param email Địa chỉ email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     * @throws SQLException Nếu có lỗi database
     */
    public boolean isEmailExist(String email) throws SQLException {
        String sql = "SELECT email FROM Users WHERE email = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());

            try ( ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isPhoneExist(String phone) throws SQLException {
        String sql = "SELECT phone FROM Users WHERE phone = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone.trim());

            try ( ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isPhoneExistForOtherUsers(String phone, int currentUserId) throws SQLException {
        String sql = "SELECT phone FROM Users WHERE phone = ? AND user_id != ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone.trim());
            stmt.setInt(2, currentUserId);
            try ( ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Tìm hoặc tạo người dùng mới từ thông tin Google (email, firstName, lastName).
     *
     * @param email Địa chỉ email
     * @param firstName Tên
     * @param lastName Họ
     * @return Đối tượng User nếu tìm thấy hoặc tạo thành công, null nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    public User findOrCreateGoogleUser(String email, String firstName, String lastName) throws SQLException {
        User existingUser = findUserByEmail(email);
        if (existingUser != null) {
            return existingUser;
        }

        String sql = "INSERT INTO Users (email, role, first_name, last_name, created_at, updated_at) VALUES (?, 'customer', ?, ?, GETDATE(), GETDATE())";
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
                        newUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        newUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        return newUser;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng trong cả bảng Users và Customer.
     *
     * @param userId ID của người dùng
     * @param firstName Tên mới
     * @param lastName Họ mới
     * @param phone Số điện thoại mới
     * @param address Địa chỉ mới
     * @param preferredPaymentMethod Phương thức thanh toán ưa thích mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    /**
     * Cập nhật thông tin hồ sơ người dùng trong cả bảng Users và Customer.
     *
     * @param userId ID của người dùng
     * @param firstName Tên mới
     * @param lastName Họ mới
     * @param phone Số điện thoại mới
     * @param address Địa chỉ mới
     * @param preferredPaymentMethod Phương thức thanh toán ưa thích mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    public boolean updateProfile(int userId, String firstName, String lastName, String phone, String address, String preferredPaymentMethod) throws SQLException {
        if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            return false;
        }

        String sqlUsers = "UPDATE Users SET first_name = ?, last_name = ?, phone = ?, address = ?, updated_at = GETDATE() WHERE user_id = ?";
        String sqlCustomer = "UPDATE Customer SET delivery_address = ?, preferred_payment_method = ? WHERE user_id = ?";
        String sqlInsertCustomer = "INSERT INTO Customer (user_id, delivery_address, preferred_payment_method, loyalty_points, status) VALUES (?, ?, ?, 0, 'active')";
        String sqlCheckCustomer = "SELECT COUNT(*) FROM Customer WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmtUsers = null;
        PreparedStatement stmtCustomer = null;
        PreparedStatement stmtInsertCustomer = null;
        PreparedStatement stmtCheckCustomer = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Cập nhật bảng Users
            stmtUsers = conn.prepareStatement(sqlUsers);
            stmtUsers.setString(1, firstName.trim());
            stmtUsers.setString(2, lastName.trim());
            stmtUsers.setString(3, (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null);
            stmtUsers.setString(4, (address != null && !address.trim().isEmpty()) ? address.trim() : null);
            stmtUsers.setInt(5, userId);

            int rowsUpdatedUsers = stmtUsers.executeUpdate();
            if (rowsUpdatedUsers == 0) {
                conn.rollback();
                return false; // Không tìm thấy user_id trong bảng Users
            }

            // 2. Kiểm tra xem user_id có tồn tại trong bảng Customer không
            stmtCheckCustomer = conn.prepareStatement(sqlCheckCustomer);
            stmtCheckCustomer.setInt(1, userId);
            ResultSet rs = stmtCheckCustomer.executeQuery();
            boolean customerExists = false;
            if (rs.next()) {
                customerExists = rs.getInt(1) > 0;
            }

            // 3. Nếu user_id không tồn tại trong Customer, chèn bản ghi mới
            if (!customerExists) {
                stmtInsertCustomer = conn.prepareStatement(sqlInsertCustomer);
                stmtInsertCustomer.setInt(1, userId);
                stmtInsertCustomer.setString(2, (address != null && !address.trim().isEmpty()) ? address.trim() : null);
                stmtInsertCustomer.setString(3, (preferredPaymentMethod != null && !preferredPaymentMethod.trim().isEmpty()) ? preferredPaymentMethod.trim() : null);
                int rowsInsertedCustomer = stmtInsertCustomer.executeUpdate();
                if (rowsInsertedCustomer == 0) {
                    conn.rollback();
                    return false;
                }
            } else {
                // 4. Nếu user_id đã tồn tại trong Customer, cập nhật bản ghi
                stmtCustomer = conn.prepareStatement(sqlCustomer);
                stmtCustomer.setString(1, (address != null && !address.trim().isEmpty()) ? address.trim() : null);
                stmtCustomer.setString(2, (preferredPaymentMethod != null && !preferredPaymentMethod.trim().isEmpty()) ? preferredPaymentMethod.trim() : null);
                stmtCustomer.setInt(3, userId);
                stmtCustomer.executeUpdate();
                // Không cần kiểm tra số hàng cập nhật vì có thể không thay đổi giá trị
            }

            conn.commit(); // Cam kết giao dịch
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có lỗi
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Database error during profile update: " + e.getMessage());
            throw e;
        } finally {
            // Đóng các tài nguyên
            if (stmtUsers != null) try {
                stmtUsers.close();
            } catch (SQLException e) {
            }
            if (stmtCheckCustomer != null) try {
                stmtCheckCustomer.close();
            } catch (SQLException e) {
            }
            if (stmtInsertCustomer != null) try {
                stmtInsertCustomer.close();
            } catch (SQLException e) {
            }
            if (stmtCustomer != null) try {
                stmtCustomer.close();
            } catch (SQLException e) {
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Khôi phục chế độ tự động cam kết
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public String getPreferredPaymentMethod(int userId) throws SQLException {
        String sql = "SELECT preferred_payment_method FROM Customer WHERE user_id = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("preferred_payment_method");
            }
            return null;
        }
    }

    /**
     * Xóa tài khoản người dùng từ cả bảng Users và Customer.
     *
     * @param userId ID của người dùng
     * @return true nếu xóa thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    /**
     * Vô hiệu hóa tài khoản người dùng bằng cách cập nhật status thành 'disable' trong cả bảng Users và Customer.
     *
     * @param userId ID của người dùng
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    public boolean disableUser(int userId) throws SQLException {
        String sqlUpdateCustomerStatus = "UPDATE Customer SET status = 'disable' WHERE user_id = ?";
        String sqlUpdateUserStatus = "UPDATE Users SET status = 'disable' WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmtCustomer = null;
        PreparedStatement stmtUser = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Cập nhật status trong bảng Customer
            stmtCustomer = conn.prepareStatement(sqlUpdateCustomerStatus);
            stmtCustomer.setInt(1, userId);
            int rowsUpdatedCustomer = stmtCustomer.executeUpdate();
            System.out.println("Updated Customer status: " + rowsUpdatedCustomer + " rows.");

            // 2. Cập nhật status trong bảng Users
            stmtUser = conn.prepareStatement(sqlUpdateUserStatus);
            stmtUser.setInt(1, userId);
            int rowsUpdatedUser = stmtUser.executeUpdate();
            System.out.println("Updated User status: " + rowsUpdatedUser + " rows.");

            // Kiểm tra kết quả
            if (rowsUpdatedUser > 0) { // Chỉ cần Users được cập nhật là đủ
                conn.commit();
                System.out.println("User disabled successfully.");
                return true;
            } else {
                conn.rollback();
                System.out.println("Failed to update user status. User might not exist.");
                return false;
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Database error during disableUser: " + e.getMessage());
            throw e;
        } finally {
            // Đóng tài nguyên
            if (stmtCustomer != null) try {
                stmtCustomer.close();
            } catch (SQLException e) {
            }
            if (stmtUser != null) try {
                stmtUser.close();
            } catch (SQLException e) {
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * Tìm người dùng dựa trên email.
     *
     * @param email Địa chỉ email
     * @return Đối tượng User nếu tìm thấy, null nếu không
     * @throws SQLException Nếu có lỗi database
     */
    public User findUserByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, email, password_hash, role, first_name, last_name, phone, address, created_at, updated_at, status "
                + "FROM Users WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    user.setStatus(rs.getString("status"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Kiểm tra mật khẩu của người dùng dựa trên email.
     *
     * @param email Địa chỉ email
     * @param password Mật khẩu cần kiểm tra (chưa mã hóa)
     * @return true nếu mật khẩu đúng, false nếu không
     * @throws SQLException Nếu có lỗi database
     */
    public boolean validatePassword(String email, String password) throws SQLException {
        String sql = "SELECT password_hash FROM Users WHERE email = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password_hash"); // Mật khẩu lấy từ DB
                    return password.equals(storedPassword); // So sánh trực tiếp (nên hash trong thực tế)
                }
            }
        }
        return false; // Trả về false nếu không tìm thấy email
    }

    /**
     * Cập nhật mật khẩu của người dùng dựa trên email.
     *
     * @param email Địa chỉ email
     * @param newPassword Mật khẩu mới (chưa mã hóa)
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    public boolean updatePassword(String email, String newPassword) throws SQLException {
        String sql = "UPDATE Users SET password_hash = ?, updated_at = GETDATE() WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword.trim());
            stmt.setString(2, email.trim());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    /**
     * Lấy thông tin người dùng dựa trên email.
     *
     * @param email Địa chỉ email
     * @return Đối tượng User nếu tìm thấy, null nếu không
     * @throws SQLException Nếu có lỗi database
     */
    public User getUserByEmail(String email) throws SQLException {
        return findUserByEmail(email); // Sử dụng findUserByEmail để tránh trùng lặp logic
    }

    /**
     * Lưu token reset mật khẩu cho người dùng.
     *
     * @param email Địa chỉ email
     * @param token Token reset mật khẩu
     * @return true nếu lưu thành công, false nếu thất bại
     * @throws SQLException Nếu có lỗi database
     */
    public boolean saveResetToken(String email, String token) throws SQLException {
        String sql = "UPDATE Users SET reset_token = ?, updated_at = GETDATE() WHERE email = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePasswordByEmail(String email, String newPassword) throws SQLException {
        String checkStatusSql = "SELECT status FROM Users WHERE email = ?";
        String updateSql = "UPDATE Users SET password_hash = ?, updated_at = GETDATE() WHERE email = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement checkStmt = conn.prepareStatement(checkStatusSql);  PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("disable".equalsIgnoreCase(status)) {
                    return false; // Không cho phép cập nhật nếu tài khoản bị vô hiệu hóa
                }
            } else {
                return false; // Email không tồn tại
            }

            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, email);
            return updateStmt.executeUpdate() > 0;
        }
    }

    /**
     * Lấy thông tin chi tiết hồ sơ người dùng để hiển thị trên trang view profile.
     *
     * @param userId ID của người dùng
     * @return Đối tượng User với thông tin đầy đủ từ bảng Users và Customer, null nếu không tìm thấy
     * @throws SQLException Nếu có lỗi database
     */
    public User getUserProfile(int userId) throws SQLException {
        String sql = "SELECT u.user_id, u.email, u.role, u.first_name, u.last_name, u.phone, u.address, "
                + "u.created_at, u.updated_at, u.status, c.loyalty_points, c.preferred_payment_method, c.delivery_address "
                + "FROM Users u "
                + "LEFT JOIN Customer c ON u.user_id = c.user_id "
                + "WHERE u.user_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

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
            System.err.println("Database error during getUserProfile: " + e.getMessage());
            throw e;
        }
        return null; // Trả về null nếu không tìm thấy người dùng
    }

    /**
     * Lấy storeId của người dùng (dành cho Store Manager hoặc Staff).
     *
     * @param userId ID của người dùng
     * @return store_id nếu tìm thấy, 0 nếu không
     * @throws SQLException Nếu có lỗi database
     */
   public int getStoreIdByUserId(int userId) throws SQLException {
    int storeId = 0;
    String sql = "SELECT store_id FROM Users WHERE user_id = ?";
    try (Connection conn = DBContext.getConnection();  
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                storeId = rs.getInt("store_id");
                if (rs.wasNull()) {
                    System.err.println("No store found for userId: " + userId + " (store_id is NULL)");
                    return 0;
                }
            } else {
                System.err.println("No store found for userId: " + userId);
            }
        }
    } catch (SQLException e) {
        System.err.println("Database error getting storeId: " + e.getMessage());
        throw e;
    }
    return storeId;
}

    public static void main(String[] args) throws SQLException {
        UserDAO ud = new UserDAO();
        boolean success = ud.disableUser(1); // Thay 1 bằng userId hợp lệ
        System.out.println("Disable user: " + success);
    }
}
