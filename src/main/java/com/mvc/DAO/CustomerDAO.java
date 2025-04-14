package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.Customer;
import com.mvc.model.Orderr;
import com.mvc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng Customer, xử lý các thao tác với thông tin khách hàng trong database. Hỗ trợ Discount Management để lấy thông tin khách hàng liên quan đến giỏ hàng và giảm giá.
 */
public class CustomerDAO {
    // Không khởi tạo connection trong constructor nữa, thay vào đó lấy khi cần
    // private Connection conn; (bỏ dòng này)

    public CustomerDAO() {
        // Constructor rỗng, không cần khởi tạo connection
    }

    /**
     * Lấy customer_id của khách hàng dựa trên user_id.
     *
     * @param userId ID của người dùng
     * @return customer_id nếu tìm thấy, -1 nếu không
     */
    public int getCustomerIdByUserId(int userId) {
        String sql = "SELECT customer_id FROM Customer WHERE user_id = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching customer_id: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Không tìm thấy customer_id
    }

    /**
     * Kiểm tra xem một user_id có phải là khách hàng hay không (vai trò 'Customer').
     *
     * @param userId ID của người dùng
     * @return true nếu là khách hàng, false nếu không
     */
    public boolean isCustomer(int userId) {
        String sql = "SELECT role FROM Users WHERE user_id = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    return "Customer".equalsIgnoreCase(role) || "customer".equalsIgnoreCase(role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during checking customer role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy thông tin khách hàng dựa trên customerId, bao gồm thông tin User liên kết.
     *
     * @param customerId ID của khách hàng
     * @return Đối tượng Customer nếu tìm thấy, null nếu không
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT c.customer_id, c.user_id, u.first_name, u.last_name, u.email, u.role "
                + "FROM Customer c "
                + "JOIN Users u ON c.user_id = u.user_id "
                + "WHERE c.customer_id = ?";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setUserId(rs.getInt("user_id"));

                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    customer.setUser(user);

                    // Bỏ các cột created_at, updated_at vì không tồn tại trong bảng Customer
                    // customer.setCreatedAt(rs.getTimestamp("created_at"));
                    // customer.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching customer by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    private Connection connection;

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not available.");
        }

        String query = "SELECT u.user_id, u.email, u.first_name, u.last_name, u.phone, u.address AS user_address, "
                + "c.loyalty_points, c.preferred_payment_method, c.delivery_address, u.status "
                + "FROM Users u LEFT JOIN Customer c ON u.user_id = c.user_id "
                + "WHERE u.role = 'Customer'";

        try ( PreparedStatement stmt = connection.prepareStatement(query);  ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setUserId(rs.getInt("user_id"));
                customer.setEmail(rs.getString("email"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("user_address"));
                customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
                customer.setPreferredPaymentMethod(rs.getString("preferred_payment_method"));
                customer.setStatus(rs.getString("status")); // Thêm status
                customers.add(customer);
            }
        }
        return customers;
    }

    public Customer getCustomerByID(int userId) throws SQLException {
        Customer cus = null;
        String query = "SELECT u.user_id, u.email, u.first_name, u.last_name, u.phone, u.address AS user_address, "
                + "c.loyalty_points, c.preferred_payment_method, c.delivery_address, u.status "
                + "FROM Users u LEFT JOIN Customer c ON u.user_id = c.user_id "
                + "WHERE u.user_id = ? AND u.role = 'Customer'";
        try ( PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cus = new Customer();
                    cus.setUserId(rs.getInt("user_id"));
                    cus.setEmail(rs.getString("email"));
                    cus.setFirstName(rs.getString("first_name"));
                    cus.setLastName(rs.getString("last_name"));
                    cus.setPhone(rs.getString("phone"));
                    // Gán Customer.delivery_address cho address, nếu null thì dùng Users.address, nếu cả hai null thì để trống
                    String deliveryAddress = rs.getString("delivery_address");
                    String userAddress = rs.getString("user_address");
                    cus.setAddress(deliveryAddress != null ? deliveryAddress : (userAddress != null ? userAddress : ""));
                    cus.setLoyaltyPoints(rs.getInt("loyalty_points"));
                    cus.setPreferredPaymentMethod(rs.getString("preferred_payment_method"));
                    cus.setStatus(rs.getString("status")); // Lấy và gán giá trị status
                    cus.setOrders(getOrdersByCustomerId(cus.getUserId()));
                }
            }
        }
        return cus;
    }

    private List<Orderr> getOrdersByCustomerId(int userId) throws SQLException {
        List<Orderr> orders = new ArrayList<>();
        String query = "SELECT o.order_id, o.customer_id, o.total_amount, o.order_date, o.shipping_address, o.order_status, "
                + "o.payment_id, p.payment_date, p.amount AS payment_amount, p.payment_status, "
                + "o.promotion_id, pr.code AS promotion_code, pr.discount_percentage, pr.expiration_date, pr.status AS promotion_status "
                + "FROM Orders o "
                + "JOIN Customer c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Payments p ON o.payment_id = p.payment_id "
                + "LEFT JOIN Promotions pr ON o.promotion_id = pr.promotion_id "
                + "WHERE c.user_id = ?";

        try ( Connection conn = DBContext.getConnection(); // Giả sử DBContext cung cấp kết nối
                  PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try ( ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Orderr order = new Orderr();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setOrderStatus(rs.getString("order_status"));

                    // Thông tin từ Payments
                    order.setPaymentId(rs.getInt("payment_id"));
                    order.setPaymentDate(rs.getTimestamp("payment_date"));
                    order.setPaymentAmount(rs.getBigDecimal("payment_amount"));
                    order.setPaymentStatus(rs.getString("payment_status"));

                    // Thông tin từ Promotions
                    order.setPromotionId(rs.getInt("promotion_id"));
                    order.setPromotionCode(rs.getString("promotion_code"));
                    order.setDiscountPercentage(rs.getBigDecimal("discount_percentage"));
                    order.setExpirationDate(rs.getDate("expiration_date"));
                    order.setPromotionStatus(rs.getString("promotion_status"));

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during fetching orders: " + e.getMessage());
            throw e;
        }
        return orders;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        boolean updated = false;
        String updateUserQuery = "UPDATE Users SET email = ?, first_name = ?, last_name = ?, phone = ?, address = ? "
                + "WHERE user_id = ? AND role = 'Customer'";
        String updateCustomerQuery = "UPDATE Customer SET loyalty_points = ?, preferred_payment_method = ?, delivery_address = ? "
                + "WHERE user_id = ?";
        String insertCustomerQuery = "INSERT INTO Customer (user_id, loyalty_points, preferred_payment_method, delivery_address) "
                + "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật bảng Users
            try ( PreparedStatement stmtUser = conn.prepareStatement(updateUserQuery)) {
                stmtUser.setString(1, customer.getEmail());
                stmtUser.setString(2, customer.getFirstName());
                stmtUser.setString(3, customer.getLastName());
                stmtUser.setString(4, customer.getPhone());
                stmtUser.setString(5, customer.getAddress());
                stmtUser.setInt(6, customer.getUserId());
                int rowsAffectedUser = stmtUser.executeUpdate();

                if (rowsAffectedUser > 0) {
                    // Cập nhật hoặc thêm mới vào bảng Customer
                    try ( PreparedStatement stmtCustomer = conn.prepareStatement(updateCustomerQuery)) {
                        stmtCustomer.setInt(1, customer.getLoyaltyPoints());
                        stmtCustomer.setString(2, customer.getPreferredPaymentMethod());
                        stmtCustomer.setString(3, customer.getAddress()); // Sử dụng address làm delivery_address
                        stmtCustomer.setInt(4, customer.getUserId());
                        int rowsAffectedCustomer = stmtCustomer.executeUpdate();

                        if (rowsAffectedCustomer == 0) {
                            // Nếu không có bản ghi trong Customer, thêm mới
                            try ( PreparedStatement stmtInsert = conn.prepareStatement(insertCustomerQuery)) {
                                stmtInsert.setInt(1, customer.getUserId());
                                stmtInsert.setInt(2, customer.getLoyaltyPoints());
                                stmtInsert.setString(3, customer.getPreferredPaymentMethod());
                                stmtInsert.setString(4, customer.getAddress());
                                stmtInsert.executeUpdate();
                            }
                        }
                        updated = true;
                    }
                }
            }

            if (updated) {
                conn.commit();
            } else {
                conn.rollback();
            }

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

        return updated;
    }

    public boolean addCustomer(Customer customer, String password) throws SQLException {
        boolean added = false;
        String insertUserQuery = "INSERT INTO Users (email, password_hash, role, first_name, last_name, phone, address, status) "
                + "VALUES (?, ?, 'Customer', ?, ?, ?, ?, 'active')";
        String insertCustomerQuery = "INSERT INTO Customer (user_id, loyalty_points, preferred_payment_method, delivery_address) "
                + "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Thêm người dùng vào bảng Users với status = 'active'
            try ( PreparedStatement stmtUser = conn.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, customer.getEmail());
                stmtUser.setString(2, password); // Nếu cần mã hóa, thay bằng hashPassword(password)
                stmtUser.setString(3, customer.getFirstName());
                stmtUser.setString(4, customer.getLastName());
                stmtUser.setString(5, customer.getPhone());
                stmtUser.setString(6, customer.getAddress());

                int rowsAffectedUser = stmtUser.executeUpdate();
                if (rowsAffectedUser > 0) {
                    // Lấy user_id vừa tạo
                    ResultSet generatedKeys = stmtUser.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        customer.setUserId(userId); // Cập nhật userId vào đối tượng Customer
                    }

                    if (userId > 0) {
                        // Thêm vào bảng Customer
                        try ( PreparedStatement stmtCustomer = conn.prepareStatement(insertCustomerQuery)) {
                            stmtCustomer.setInt(1, userId);
                            stmtCustomer.setInt(2, customer.getLoyaltyPoints());
                            stmtCustomer.setString(3, customer.getPreferredPaymentMethod());
                            stmtCustomer.setString(4, customer.getAddress()); // Sử dụng address làm delivery_address

                            int rowsAffectedCustomer = stmtCustomer.executeUpdate();
                            if (rowsAffectedCustomer > 0) {
                                added = true;
                            }
                        }
                    }
                }
            }

            if (added) {
                conn.commit();
            } else {
                conn.rollback();
            }

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

        return added;
    }

    public boolean deleteCustomer(int userId) throws SQLException {
        boolean deleted = false;
        String deleteOrderDetailsQuery = "DELETE FROM OrderDetails WHERE order_id IN (SELECT order_id FROM Orders WHERE customer_id IN (SELECT customer_id FROM Customer WHERE user_id = ?))";
        String deleteReviewsQuery = "DELETE FROM Reviews WHERE customer_id IN (SELECT customer_id FROM Customer WHERE user_id = ?)";
        String deleteOrdersQuery = "DELETE FROM Orders WHERE customer_id IN (SELECT customer_id FROM Customer WHERE user_id = ?)";
        String deleteCartQuery = "DELETE FROM Cart WHERE customer_id IN (SELECT customer_id FROM Customer WHERE user_id = ?)";
        String deleteCustomerQuery = "DELETE FROM Customer WHERE user_id = ?";
        String deleteUserQuery = "DELETE FROM Users WHERE user_id = ? AND role = 'Customer'";

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Xóa các bản ghi liên quan
            try ( PreparedStatement stmtOrderDetails = conn.prepareStatement(deleteOrderDetailsQuery)) {
                stmtOrderDetails.setInt(1, userId);
                stmtOrderDetails.executeUpdate();
            }
            try ( PreparedStatement stmtReviews = conn.prepareStatement(deleteReviewsQuery)) {
                stmtReviews.setInt(1, userId);
                stmtReviews.executeUpdate();
            }
            try ( PreparedStatement stmtOrders = conn.prepareStatement(deleteOrdersQuery)) {
                stmtOrders.setInt(1, userId);
                stmtOrders.executeUpdate();
            }
            try ( PreparedStatement stmtCart = conn.prepareStatement(deleteCartQuery)) {
                stmtCart.setInt(1, userId);
                stmtCart.executeUpdate();
            }

            // Xóa từ bảng Customer
            int rowsAffectedCustomer = 0;
            try ( PreparedStatement stmtCustomer = conn.prepareStatement(deleteCustomerQuery)) {
                stmtCustomer.setInt(1, userId);
                rowsAffectedCustomer = stmtCustomer.executeUpdate();
            }

            // Xóa từ bảng Users
            int rowsAffectedUser = 0;
            try ( PreparedStatement stmtUser = conn.prepareStatement(deleteUserQuery)) {
                stmtUser.setInt(1, userId);
                rowsAffectedUser = stmtUser.executeUpdate();
            }

            // Chỉ coi là thành công nếu xóa được từ Users (vì Customer có thể không tồn tại)
            if (rowsAffectedUser > 0) {
                deleted = true;
            }

            if (deleted) {
                conn.commit();
            } else {
                conn.rollback();
            }

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

        return deleted;
    }

    public boolean lockCustomer(int userId) throws SQLException {
        String query = "UPDATE Users SET status = 'disable' WHERE user_id = ? AND role = 'Customer'";
        try ( PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean unlockCustomer(int userId) throws SQLException {
        String query = "UPDATE Users SET status = 'active' WHERE user_id = ? AND role = 'Customer'";
        try ( PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

}