package com.mvc.DAO;

import com.mvc.model.Customer;
import com.mvc.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    private Connection connection;

    public StaffDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT u.user_id, u.email, u.first_name, u.last_name, u.phone, u.address AS user_address, "
                + "c.loyalty_points, c.preferred_payment_method, c.delivery_address "
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
                // Gán Customer.delivery_address cho address, nếu null thì dùng Users.address, nếu cả hai null thì để trống
                String deliveryAddress = rs.getString("delivery_address");
                String userAddress = rs.getString("user_address");
                customer.setAddress(deliveryAddress != null ? deliveryAddress : (userAddress != null ? userAddress : ""));
                customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
                customer.setPreferredPaymentMethod(rs.getString("preferred_payment_method"));
                customer.setOrders(getOrdersByCustomerId(customer.getUserId()));
                customers.add(customer);
            }
        }
        return customers;
    }

    public Customer getCustomerById(int userId) throws SQLException {
        Customer cus = null;
        String query = "SELECT u.user_id, u.email, u.first_name, u.last_name, u.phone, u.address AS user_address, "
                + "c.loyalty_points, c.preferred_payment_method, c.delivery_address "
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
                    cus.setOrders(getOrdersByCustomerId(cus.getUserId()));
                }
            }
        }
        return cus;
    }

    private List<Order> getOrdersByCustomerId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.order_id, o.customer_id, o.total_amount, o.order_date, o.shipping_address, o.order_status "
                + "FROM Orders o "
                + "JOIN Customer c ON o.customer_id = c.customer_id "
                + "WHERE c.user_id = ?";
        try ( PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try ( ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setOrderStatus(rs.getString("order_status"));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        boolean updated = false;
        String updateUserQuery = "UPDATE Users SET email = ?, first_name = ?, last_name = ?, phone = ?, address = ? "
                + "WHERE user_id = ? AND role = 'Customer'";
        String updateCustomerQuery = "UPDATE Customer SET loyalty_points = ?, preferred_payment_method = ? "
                + "WHERE user_id = ?";
        String insertCustomerQuery = "INSERT INTO Customer (user_id, loyalty_points, preferred_payment_method) "
                + "VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try ( PreparedStatement stmtUser = connection.prepareStatement(updateUserQuery)) {
                stmtUser.setString(1, customer.getEmail());
                stmtUser.setString(2, customer.getFirstName());
                stmtUser.setString(3, customer.getLastName());
                stmtUser.setString(4, customer.getPhone());
                stmtUser.setString(5, customer.getAddress());
                stmtUser.setInt(6, customer.getUserId());
                int rowsAffectedUser = stmtUser.executeUpdate();

                if (rowsAffectedUser > 0) {
                    try ( PreparedStatement stmtCustomer = connection.prepareStatement(updateCustomerQuery)) {
                        stmtCustomer.setInt(1, customer.getLoyaltyPoints());
                        stmtCustomer.setString(2, customer.getPreferredPaymentMethod());
                        stmtCustomer.setInt(3, customer.getUserId());
                        int rowsAffectedCustomer = stmtCustomer.executeUpdate();

                        if (rowsAffectedCustomer == 0) {
                            try ( PreparedStatement stmtInsert = connection.prepareStatement(insertCustomerQuery)) {
                                stmtInsert.setInt(1, customer.getUserId());
                                stmtInsert.setInt(2, customer.getLoyaltyPoints());
                                stmtInsert.setString(3, customer.getPreferredPaymentMethod());
                                stmtInsert.executeUpdate();
                            }
                        }
                        updated = true;
                    }
                }
            }

            if (updated) {
                connection.commit();
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return updated;
    }

    public boolean addCustomer(Customer customer, String password) throws SQLException {
        boolean added = false;
        String insertUserQuery = "INSERT INTO Users (email, password_hash, role, first_name, last_name, phone, address) "
                + "VALUES (?, ?, 'Customer', ?, ?, ?, ?)";
        String insertCustomerQuery = "INSERT INTO Customer (user_id, loyalty_points, preferred_payment_method, delivery_address) "
                + "VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            // Thêm người dùng vào bảng Users trước
            try ( PreparedStatement stmtUser = connection.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, customer.getEmail());
                stmtUser.setString(2, password); // Nếu cần mã hóa, hãy thay bằng hashPassword(password)
                stmtUser.setString(3, customer.getFirstName());
                stmtUser.setString(4, customer.getLastName());
                stmtUser.setString(5, customer.getPhone());
                stmtUser.setString(6, customer.getAddress());

                int rowsAffectedUser = stmtUser.executeUpdate();
                if (rowsAffectedUser > 0) {
                    // Lấy user_id mới chèn vào
                    ResultSet generatedKeys = stmtUser.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }

                    if (userId > 0) {
                        // Thêm vào bảng Customer
                        try ( PreparedStatement stmtCustomer = connection.prepareStatement(insertCustomerQuery)) {
                            stmtCustomer.setInt(1, userId);
                            stmtCustomer.setInt(2, customer.getLoyaltyPoints());
                            stmtCustomer.setString(3, customer.getPreferredPaymentMethod());
                            stmtCustomer.setString(4, customer.getAddress());

                            int rowsAffectedCustomer = stmtCustomer.executeUpdate();
                            if (rowsAffectedCustomer > 0) {
                                added = true;
                            }
                        }
                    }
                }
            }

            if (added) {
                connection.commit();
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
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

        try {
            connection.setAutoCommit(false);

            try ( PreparedStatement stmtOrderDetails = connection.prepareStatement(deleteOrderDetailsQuery)) {
                stmtOrderDetails.setInt(1, userId);
                stmtOrderDetails.executeUpdate();
            }
            try ( PreparedStatement stmtReviews = connection.prepareStatement(deleteReviewsQuery)) {
                stmtReviews.setInt(1, userId);
                stmtReviews.executeUpdate();
            }
            try ( PreparedStatement stmtOrders = connection.prepareStatement(deleteOrdersQuery)) {
                stmtOrders.setInt(1, userId);
                stmtOrders.executeUpdate();
            }
            try ( PreparedStatement stmtCart = connection.prepareStatement(deleteCartQuery)) {
                stmtCart.setInt(1, userId);
                stmtCart.executeUpdate();
            }

            int rowsAffectedCustomer = 0;
            try ( PreparedStatement stmtCustomer = connection.prepareStatement(deleteCustomerQuery)) {
                stmtCustomer.setInt(1, userId);
                rowsAffectedCustomer = stmtCustomer.executeUpdate();
            }

            int rowsAffectedUser = 0;
            try ( PreparedStatement stmtUser = connection.prepareStatement(deleteUserQuery)) {
                stmtUser.setInt(1, userId);
                rowsAffectedUser = stmtUser.executeUpdate();
            }

            if (rowsAffectedUser > 0) {
                deleted = true;
            }

            if (deleted) {
                connection.commit();
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

        return deleted;
    }
}
