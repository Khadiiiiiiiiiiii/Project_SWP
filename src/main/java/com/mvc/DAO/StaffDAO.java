package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.Staff;
import com.mvc.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private Connection connection;

    public StaffDAO(Connection connection) {
        this.connection = connection;
    }

    public List<User> getAllCustomers() {
        List<User> customers = new ArrayList<>();
        String query = "SELECT user_id, email, first_name, last_name, phone, address FROM Users WHERE role = 'customer'";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("user_id"));
                user.setEmail(resultSet.getString("email"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                customers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean addStaff(String email, String password, String firstName, String lastName, String phone, String address) throws SQLException {
        System.out.println("StaffDAO: Starting addStaff with email=" + email);

        // Validate input
        if (email == null || password == null || firstName == null || lastName == null
                || email.trim().isEmpty() || password.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            System.out.println("StaffDAO: Invalid input - some required fields are empty or null");
            return false;
        }

        // Kiểm tra email đã tồn tại
        if (isEmailExist(email)) {
            System.out.println("StaffDAO: Email already exists - " + email);
            return false;
        }

        Connection conn = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtStaff = null;
        ResultSet rs = null;
        conn = DBContext.getConnection(); 

        try {
            
            // Bắt đầu transaction
            conn.setAutoCommit(false);
            System.out.println("StaffDAO: Starting transaction");

            // Thêm vào bảng Users với role = 'STAFF'
            String sqlUser = "INSERT INTO Users (email, password_hash, role, first_name, last_name, phone, address, created_at, updated_at) "
                    + "VALUES (?, ?, 'STAFF', ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, email.trim());
            stmtUser.setString(2, password.trim());
            stmtUser.setString(3, firstName.trim());
            stmtUser.setString(4, lastName.trim());
            stmtUser.setString(5, phone != null ? phone.trim() : null);
            stmtUser.setString(6, address != null ? address.trim() : null);

            int rowsInserted = stmtUser.executeUpdate();
            if (rowsInserted <= 0) {
                System.out.println("StaffDAO: Failed to insert into Users table");
                conn.rollback();
                return false;
            }
            System.out.println("StaffDAO: Inserted into Users table successfully");

            // Lấy user_id vừa thêm
            rs = stmtUser.getGeneratedKeys();
            if (!rs.next()) {
                System.out.println("StaffDAO: Failed to retrieve user_id");
                conn.rollback();
                return false;
            }
            int userId = rs.getInt(1);
            System.out.println("StaffDAO: Generated user_id=" + userId);

            // Thêm vào bảng Staff (bỏ cột staff_id để SQL Server tự sinh)
            String sqlStaff = "INSERT INTO Staff (user_id, role, hired_date) VALUES (?, 'STAFF', CURRENT_TIMESTAMP)";
            stmtStaff = conn.prepareStatement(sqlStaff);
            stmtStaff.setInt(1, userId);
            rowsInserted = stmtStaff.executeUpdate();

            if (rowsInserted <= 0) {
                System.out.println("StaffDAO: Failed to insert into Staff table");
                conn.rollback();
                return false;
            }
            System.out.println("StaffDAO: Inserted into Staff table successfully");

            // Commit transaction
            conn.commit();
            System.out.println("StaffDAO: Transaction committed successfully");
            return true;

        } catch (SQLException e) {
            System.out.println("StaffDAO: Database error - " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("StaffDAO: Transaction rolled back");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmtUser != null) stmtUser.close();
            if (stmtStaff != null) stmtStaff.close();
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isEmailExist(String email) throws SQLException {
        System.out.println("StaffDAO: Checking if email exists - " + email);
        String sql = "SELECT email FROM Users WHERE email = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                boolean exists = rs.next();
                System.out.println("StaffDAO: Email exists=" + exists);
                return exists;
            }
        }
    }

    public Staff getStaffById(int staffId) throws SQLException {
        Staff staff = null;
        String sql = "SELECT s.staff_id, u.user_id, u.email, u.first_name, u.last_name, u.phone, u.address, s.role, s.hired_date " +
                     "FROM Staff s JOIN Users u ON s.user_id = u.user_id WHERE s.staff_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    staff = new Staff();
                    staff.setStaff_id(String.valueOf(rs.getInt("staff_id"))); // Chuyển sang String để tránh lỗi parse
                    staff.setUser_id(String.valueOf(rs.getInt("user_id")));
                    staff.setEmail(rs.getString("email"));
                    staff.setFirst_name(rs.getString("first_name"));
                    staff.setLast_name(rs.getString("last_name"));
                    staff.setPhone(rs.getString("phone"));
                    staff.setAddress(rs.getString("address"));
                    staff.setRole(rs.getString("role"));
                    staff.setHired_date(rs.getDate("hired_date"));
                } else {
                    System.out.println("StaffDAO: No staff found for staff_id=" + staffId);
                    throw new SQLException("Not found with ID: " + staffId);
                }
            }
        }
        return staff;
    }

    public void updateStaff(Staff staff, boolean updatePassword) throws SQLException {
        // Câu lệnh SQL cơ bản (không bao gồm password_hash nếu không cập nhật mật khẩu)
        String sql = "UPDATE Users SET email = ?, first_name = ?, last_name = ?, phone = ?, address = ?, updated_at = CURRENT_TIMESTAMP " +
                     (updatePassword ? ", password_hash = ?" : "") +
                     " WHERE user_id = (SELECT user_id FROM Staff WHERE staff_id = ?)";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, staff.getEmail().trim());
            stmt.setString(paramIndex++, staff.getFirst_name().trim());
            stmt.setString(paramIndex++, staff.getLast_name().trim());
            stmt.setString(paramIndex++, staff.getPhone() != null ? staff.getPhone().trim() : null);
            stmt.setString(paramIndex++, staff.getAddress() != null ? staff.getAddress().trim() : null);
            if (updatePassword) {
                stmt.setString(paramIndex++, staff.getPassword().trim());
            }
            stmt.setInt(paramIndex, Integer.parseInt(staff.getStaff_id()));

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated <= 0) {
                System.out.println("StaffDAO: No rows updated for staff_id=" + staff.getStaff_id());
                throw new SQLException("Unable to update employee information with ID: " + staff.getStaff_id());
            }
            System.out.println("StaffDAO: Updated " + rowsUpdated + " rows in Users table for staff_id=" + staff.getStaff_id());
        }
    }

    public void deleteStaff(int staffId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtStaff = null;
        PreparedStatement stmtUser = null;
        conn = DBContext.getConnection(); 

        try {
            // Bắt đầu transaction
            conn.setAutoCommit(false);
            System.out.println("StaffDAO: Starting transaction to delete staff with ID=" + staffId);

            // Lấy user_id từ bảng Staff
            String getUserIdSql = "SELECT user_id FROM Staff WHERE staff_id = ?";
            int userId = -1;
            try (PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql)) {
                getUserIdStmt.setInt(1, staffId);
                try (ResultSet rs = getUserIdStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    } else {
                        throw new SQLException("Not found with ID: " + staffId);
                    }
                }
            }

            // Xóa bản ghi trong bảng Staff
            String deleteStaffSql = "DELETE FROM Staff WHERE staff_id = ?";
            stmtStaff = conn.prepareStatement(deleteStaffSql);
            stmtStaff.setInt(1, staffId);
            int staffRowsDeleted = stmtStaff.executeUpdate();
            if (staffRowsDeleted <= 0) {
                System.out.println("StaffDAO: Failed to delete from Staff table for staff_id=" + staffId);
                conn.rollback();
                throw new SQLException("Cannot delete employee from Staff table with ID: " + staffId);
            }
            System.out.println("StaffDAO: Deleted " + staffRowsDeleted + " rows from Staff table");

            // Xóa bản ghi trong bảng Users
            String deleteUserSql = "DELETE FROM Users WHERE user_id = ?";
            stmtUser = conn.prepareStatement(deleteUserSql);
            stmtUser.setInt(1, userId);
            int userRowsDeleted = stmtUser.executeUpdate();
            if (userRowsDeleted <= 0) {
                System.out.println("StaffDAO: Failed to delete from Users table for user_id=" + userId);
                conn.rollback();
                throw new SQLException("Cannot delete user information from Users table with ID: " + userId);
            }
            System.out.println("StaffDAO: Deleted " + userRowsDeleted + " rows from Users table");

            // Commit transaction
            conn.commit();
            System.out.println("StaffDAO: Transaction committed successfully for deleting staff_id=" + staffId);

        } catch (SQLException e) {
            System.out.println("StaffDAO: Database error during delete - " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("StaffDAO: Transaction rolled back");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (stmtStaff != null) stmtStaff.close();
            if (stmtUser != null) stmtUser.close();
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT s.staff_id, u.user_id, u.email, u.first_name, u.last_name, u.phone, u.address, s.role, s.hired_date " +
                     "FROM Staff s JOIN Users u ON s.user_id = u.user_id WHERE s.role = 'STAFF'";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Staff staff = new Staff();
                staff.setStaff_id(String.valueOf(rs.getInt("staff_id")));
                staff.setUser_id(String.valueOf(rs.getInt("user_id")));
                staff.setEmail(rs.getString("email"));
                staff.setFirst_name(rs.getString("first_name"));
                staff.setLast_name(rs.getString("last_name"));
                staff.setPhone(rs.getString("phone"));
                staff.setAddress(rs.getString("address"));
                staff.setRole(rs.getString("role"));
                staff.setHired_date(rs.getDate("hired_date"));
                staffList.add(staff);
            }
        }
        return staffList;
    }
}