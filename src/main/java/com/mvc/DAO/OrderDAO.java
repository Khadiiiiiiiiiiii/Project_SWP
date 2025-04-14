/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.HistoryOrder;
import com.mvc.model.Order;
import com.mvc.model.OrderDetail;
import com.mvc.model.Payment;
import com.mvc.model.ViewProductInOrder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class OrderDAO {

public Order createOrder(int customer_id, long total_amount, LocalDate order_date,
        String shipping_address, int payment_id, int promotion_id,
        String promo_code, double discount_percentage, double discount_amount) { // Thêm các tham số mới
    String orderSql = "INSERT INTO dbo.Orders (customer_id, total_amount, order_date, " +
                     "shipping_address, order_status, payment_id, promotion_id, " +
                     "promo_code, discount_percentage, discount_amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBContext.getConnection();
         PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, customer_id);
        ps.setBigDecimal(2, BigDecimal.valueOf(total_amount));
        ps.setDate(3, Date.valueOf(order_date));
        ps.setString(4, shipping_address);
        ps.setString(5, "Pending");  // Trạng thái mặc định của đơn hàng
        ps.setObject(6, payment_id > 0 ? payment_id : null);
        ps.setObject(7, promotion_id > 0 ? promotion_id : null);
        ps.setString(8, promo_code); // Lưu promo_code
        ps.setDouble(9, discount_percentage); // Lưu discount_percentage
        ps.setDouble(10, discount_amount); // Lưu discount_amount

        int affectedRows = ps.executeUpdate();

        if (affectedRows > 0) {
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    return new Order(orderId, customer_id, total_amount, order_date,
                            shipping_address, "Pending", payment_id, promotion_id);
                }
            }
        } else {
            System.err.println("Order creation failed, no rows affected.");
        }
    } catch (SQLException e) {
        System.err.println("Database error during creating order: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}

    public OrderDetail createOrderDetail(int order_id, int product_id, int quantity, BigDecimal unit_price) {
        String sql = "INSERT INTO dbo.OrderDetails (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order_id);
            ps.setInt(2, product_id);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, unit_price);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderDetailId = rs.getInt(1);
                        return new OrderDetail(orderDetailId, order_id, product_id, quantity, unit_price);
                    }
                }
            } else {
                System.err.println("OrderDetail creation failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Database error during creating OrderDetail: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Trả về null nếu có lỗi
    }

    public void updateStatusOrder(int orderId) {
        String sql = "UPDATE Orders SET order_status = 'Processing' WHERE order_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Order status updated successfully.");
            } else {
                System.out.println("No order found with the provided ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatusCancelOrder(int orderId) {
        String sql = "UPDATE Orders SET order_status = 'Cancel' WHERE order_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Order status updated successfully.");
            } else {
                System.out.println("No order found with the provided ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OrderDetail> getListOrderDetailByOrderId(int orderId) {
        String orderSql = "select od.product_id, od.quantity from Orders as o\n"
                + "join OrderDetails as od on o.order_id = od.order_id\n"
                + "where o.order_id = ?";
        List<OrderDetail> list = new ArrayList<>();
        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail od = new OrderDetail();
                od.setProduct_id(rs.getInt("product_id"));
                od.setQuantity(rs.getInt("quantity"));
                list.add(od);
            }
            return list;
        } catch (SQLException e) {
            System.err.println("Database error during creating order: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateQuantityProduct(List<OrderDetail> list) {
        String sql = "UPDATE Products\n"
                + "SET stock_quantity = stock_quantity - ?\n"
                + "WHERE product_id = ?";

        for (OrderDetail od : list) {
            try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, od.getQuantity());
                ps.setInt(2, od.getProduct_id());
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Product stock updated successfully.");
                } else {
                    System.out.println("No order found with the provided ID.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Order> getInfoOrderList() throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT o.order_id, o.total_amount, o.order_date, o.shipping_address, o.order_status, "
                + "u.first_name, u.last_name "
                + "FROM Orders o "
                + "JOIN Customer c ON o.customer_id = c.customer_id "
                + "JOIN Users u ON c.user_id = u.user_id";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql);  ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getDouble("total_amount"),
                        rs.getDate("order_date").toLocalDate(),
                        rs.getString("shipping_address"),
                        rs.getString("order_status"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                orderList.add(order);
            }
            System.out.println("OrderDAO: Retrieved " + orderList.size() + " orders");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("OrderDAO: SQL Error - " + e.getMessage());
            throw e;
        }
        return orderList;
    }

    // option updateOrderStatus
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
    // Chuẩn hóa giá trị status (chuyển về chữ thường nếu cần)
    String normalizedStatus = status.toLowerCase();
    String sql = "UPDATE Orders SET order_status = ? WHERE order_id = ?";

    try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        conn.setAutoCommit(false); // Tắt AutoCommit
        stmt.setString(1, normalizedStatus);
        stmt.setInt(2, orderId);
        int rowsAffected = stmt.executeUpdate();
        conn.commit(); // Commit thay đổi vào DB
        return rowsAffected > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        throw e;
    }
}

    // Lấy sản phẩm theo ID cho detail
    public Order getOrderManagementByIdforDetail(int order_id) {
        Order order = null;
        List<String> productNames = new ArrayList<>();
        int totalQuantity = 0;

        String sql = "SELECT o.order_id, o.total_amount, o.order_date, o.shipping_address, o.order_status, "
                + "u.first_name, u.last_name, u.email, u.phone, "
                + "od.quantity, "
                + "p.name "
                + "FROM Orders o "
                + "LEFT JOIN Customer c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Users u ON c.user_id = u.user_id "
                + "LEFT JOIN OrderDetails od ON o.order_id = od.order_id "
                + "LEFT JOIN Products p ON od.product_id = p.product_id "
                + "WHERE o.order_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Nếu order chưa được khởi tạo, khởi tạo nó với các thông tin chung
                if (order == null) {
                    order = new Order();
                    order.setOrder_id(rs.getInt("order_id"));
                    order.setTotal_amount(rs.getDouble("total_amount"));
                    order.setOrder_date(rs.getDate("order_date").toLocalDate());
                    order.setShipping_address(rs.getString("shipping_address"));
                    order.setOrder_status(rs.getString("order_status"));
                    order.setFirst_name(rs.getString("first_name"));
                    order.setLast_name(rs.getString("last_name"));
                    order.setPhone(rs.getString("phone"));
                    order.setEmail(rs.getString("email"));
                }

                // Thêm tên sản phẩm vào danh sách
                String productName = rs.getString("name");
                if (productName != null) {
                    productNames.add(productName);
                }

                // Cộng dồn số lượng
                totalQuantity += rs.getInt("quantity");
            }

            // Gán danh sách sản phẩm và tổng số lượng vào order
            if (order != null) {
                order.setNameProducts(productNames);
                order.setQuantity(totalQuantity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }
    
        public Order getOrderProductManagementByIdforDetail(int order_id) {
        Order order = null;
        List<ViewProductInOrder> viewProductInOrders = new ArrayList<>();

        String sql = "SELECT o.order_id, o.order_status, "
                + "od.quantity, od.unit_price, od.product_id, "
                + "p.name, p.discount_price "
                + "FROM Orders o "
                + "LEFT JOIN OrderDetails od ON o.order_id = od.order_id "
                + "LEFT JOIN Products p ON od.product_id = p.product_id "
                + "WHERE o.order_id = ?";

        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, order_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (order == null) {
                    order = new Order();
                    order.setOrder_id(rs.getInt("order_id"));
                    order.setOrder_status(rs.getString("order_status"));
                }

                // Create an OrderDetail object for each product
                String productName = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double unitPrice = rs.getDouble("unit_price");
                ViewProductInOrder detail = new ViewProductInOrder(productName, quantity, unitPrice);
                viewProductInOrders.add(detail);
            }

            if (order != null) {
                order.setViewProductInOrders(viewProductInOrders);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    public List<HistoryOrder> getHistoryOrder(int userId) throws SQLException {
    List<HistoryOrder> list = new ArrayList<>();
    String sql = "SELECT o.order_id, o.total_amount, o.order_date, o.shipping_address, o.order_status, " +
                "od.order_detail_id, p.name, p.image_url, od.quantity, od.unit_price, " +
                "o.promo_code, o.discount_percentage, o.discount_amount " +
                "FROM Orders o " +
                "JOIN OrderDetails od ON o.order_id = od.order_id " +
                "JOIN Products p ON od.product_id = p.product_id " +
                "JOIN Customer c ON o.customer_id = c.customer_id " +
                "WHERE c.user_id = ?";

    try (Connection conn = DBContext.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            HistoryOrder ho = new HistoryOrder();
            ho.setOrderId(rs.getInt("order_id"));
            ho.setTotalAmount(rs.getDouble("total_amount"));
            ho.setOrderDate(rs.getTimestamp("order_date"));
            ho.setShippingAddress(rs.getString("shipping_address"));
            ho.setOrderStatus(rs.getString("order_status"));
            ho.setOrderDetail(rs.getInt("order_detail_id"));
            ho.setName(rs.getString("name"));
            ho.setImage(rs.getString("image_url"));
            ho.setQuantity(rs.getInt("quantity"));
            ho.setPricerByQuantity(rs.getInt("quantity") * rs.getDouble("unit_price"));
            // Lấy thông tin mã giảm giá trực tiếp từ Orders
            ho.setPromoCode(rs.getString("promo_code") != null ? rs.getString("promo_code") : "No promotion applied");
            ho.setDiscountPercentage(rs.getDouble("discount_percentage"));
            ho.setDiscountAmount(rs.getDouble("discount_amount"));
            list.add(ho);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("OrderDAO: SQL Error - " + e.getMessage());
        throw e;
    }
    return list;
}
    

    public static void main(String[] args) {
        OrderDAO orderDao = new OrderDAO();
        List<OrderDetail> listOrderDetail = orderDao.getListOrderDetailByOrderId(9);
        orderDao.updateQuantityProduct(listOrderDetail);
    }
}
