package com.mvc.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mvc.dal.DBContext;
import com.mvc.model.RevenueReport;

public class RevenueReportDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Get all revenue reports
    public List<RevenueReport> getAllRevenueReports() {
        List<RevenueReport> list = new ArrayList<>();
        String query = "SELECT r.*, s.store_name FROM RevenueReports r "
                + "LEFT JOIN Stores s ON r.store_id = s.store_id "
                + "ORDER BY r.report_date DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                RevenueReport report = new RevenueReport();
                report.setReportId(rs.getInt("report_id"));
                report.setStoreId(rs.getInt("store_id"));
                report.setReportDate(rs.getDate("report_date"));
                report.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                report.setTotalOrders(rs.getInt("total_orders"));
                report.setTotalSales(rs.getInt("total_sales"));
                report.setStoreName(rs.getString("store_name"));
                list.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Get revenue reports by date range
    public List<RevenueReport> getRevenueReportsByDateRange(Date startDate, Date endDate) {
        List<RevenueReport> list = new ArrayList<>();
        String query = "SELECT r.*, s.store_name FROM RevenueReports r "
                + "LEFT JOIN Stores s ON r.store_id = s.store_id "
                + "WHERE r.report_date BETWEEN ? AND ? "
                + "ORDER BY r.report_date DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                RevenueReport report = new RevenueReport();
                report.setReportId(rs.getInt("report_id"));
                report.setStoreId(rs.getInt("store_id"));
                report.setReportDate(rs.getDate("report_date"));
                report.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                report.setTotalOrders(rs.getInt("total_orders"));
                report.setTotalSales(rs.getInt("total_sales"));
                report.setStoreName(rs.getString("store_name"));
                list.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Get revenue reports by store
    public List<RevenueReport> getRevenueReportsByStore(int storeId) {
        List<RevenueReport> list = new ArrayList<>();
        String query = "SELECT r.*, s.store_name FROM RevenueReports r "
                + "LEFT JOIN Stores s ON r.store_id = s.store_id "
                + "WHERE r.store_id = ? "
                + "ORDER BY r.report_date DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, storeId);
            rs = ps.executeQuery();
            while (rs.next()) {
                RevenueReport report = new RevenueReport();
                report.setReportId(rs.getInt("report_id"));
                report.setStoreId(rs.getInt("store_id"));
                report.setReportDate(rs.getDate("report_date"));
                report.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                report.setTotalOrders(rs.getInt("total_orders"));
                report.setTotalSales(rs.getInt("total_sales"));
                report.setStoreName(rs.getString("store_name"));
                list.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Get aggregate summary for date range
    public RevenueReport getSummaryByDateRange(Date startDate, Date endDate, int storeId) {
        RevenueReport summary = new RevenueReport();
        String query = "SELECT SUM(total_revenue) as total_revenue, "
                + "SUM(total_orders) as total_orders, "
                + "SUM(total_sales) as total_sales "
                + "FROM RevenueReports "
                + "WHERE report_date BETWEEN ? AND ? "
                + "AND (store_id = ? OR ? = 0)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));
            ps.setInt(3, storeId);
            ps.setInt(4, storeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                summary.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                summary.setTotalOrders(rs.getInt("total_orders"));
                summary.setTotalSales(rs.getInt("total_sales"));
                summary.setReportDate(startDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return summary;
    }

    public boolean generateTodayReport(int storeId) {
        // Query để log các đơn hàng 'completed' được xem xét
        String debugQuery = "SELECT o.order_id, o.order_date, o.order_status, o.total_amount "
                + "FROM Orders o "
                + "LEFT JOIN OrderDetails od ON o.order_id = od.order_id "
                + "WHERE o.order_date = CAST(GETDATE() AS DATE) "
                + "AND o.order_status = 'completed';";

        // Query để tổng hợp dữ liệu trước khi MERGE
        String selectQuery = "SELECT "
                + "   COUNT(DISTINCT o.order_id) AS total_orders, "
                + "   COALESCE(SUM(o.total_amount), 0) AS total_revenue, "
                + "   COALESCE(SUM(od.quantity), 0) AS total_sales "
                + "FROM Orders o "
                + "LEFT JOIN OrderDetails od ON o.order_id = od.order_id "
                + "WHERE o.order_date = CAST(GETDATE() AS DATE) "
                + "AND o.order_status = 'completed';";

        // Query MERGE để cập nhật hoặc chèn báo cáo
        String mergeQuery = "MERGE INTO RevenueReports AS target "
                + "USING ( "
                + "   SELECT "
                + "       ? AS store_id, "
                + "       CAST(GETDATE() AS DATE) AS report_date, "
                + "       COALESCE(SUM(o.total_amount), 0) AS total_revenue, "
                + "       COUNT(DISTINCT o.order_id) AS total_orders, "
                + "       COALESCE(SUM(od.quantity), 0) AS total_sales "
                + "   FROM Orders o "
                + "   LEFT JOIN OrderDetails od ON o.order_id = od.order_id "
                + "   WHERE o.order_date = CAST(GETDATE() AS DATE) "
                + "   AND o.order_status = 'completed' "
                + ") AS source "
                + "ON (target.store_id = source.store_id AND target.report_date = source.report_date) "
                + "WHEN MATCHED THEN "
                + "   UPDATE SET "
                + "       total_revenue = source.total_revenue, "
                + "       total_orders = source.total_orders, "
                + "       total_sales = source.total_sales "
                + "WHEN NOT MATCHED THEN "
                + "   INSERT (store_id, report_date, total_revenue, total_orders, total_sales) "
                + "   VALUES (source.store_id, source.report_date, source.total_revenue, source.total_orders, source.total_sales);";

        try {
            conn = new DBContext().getConnection();

            // Debug: Kiểm tra và log các đơn hàng 'completed'
            ps = conn.prepareStatement(debugQuery);
            rs = ps.executeQuery();
            System.out.println("Checking 'completed' orders for Store ID " + storeId + " on " + new java.sql.Date(System.currentTimeMillis()) + ":");
            boolean hasCompletedOrders = false;
            while (rs.next()) {
                hasCompletedOrders = true;
                System.out.println("Order ID: " + rs.getInt("order_id") + ", Date: " + rs.getTimestamp("order_date")
                        + ", Status: " + rs.getString("order_status") + ", Total Amount: " + rs.getDouble("total_amount"));
            }
            rs.close();
            ps.close();

            // Nếu không có đơn hàng 'completed', không tạo report và thông báo
            if (!hasCompletedOrders) {
                System.out.println("No 'completed' orders found for Store ID " + storeId + " on " + new java.sql.Date(System.currentTimeMillis()) + ". Report not generated.");
                return false;
            }

            // Log aggregated data before merging with detailed breakdown
            ps = conn.prepareStatement(selectQuery);
            rs = ps.executeQuery();
            if (rs.next()) {
                int totalOrders = rs.getInt("total_orders");
                double totalRevenue = rs.getDouble("total_revenue");
                int totalSales = rs.getInt("total_sales");
                String individualOrders = getIndividualOrders(storeId);
                System.out.println("Before merging for Store ID " + storeId + " on " + new java.sql.Date(System.currentTimeMillis())
                        + " - Total Orders: " + totalOrders + ", Total Revenue: " + totalRevenue + ", Total Sales: " + totalSales
                        + ", Individual Orders: " + individualOrders);
            } else {
                System.out.println("No aggregated data found for Store ID " + storeId + " on " + new java.sql.Date(System.currentTimeMillis()) + " for 'completed' orders");
            }
            rs.close();
            ps.close();

            // Execute the MERGE query and log the result
            ps = conn.prepareStatement(mergeQuery);
            ps.setInt(1, storeId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("After MERGE for Store ID " + storeId + " on " + new java.sql.Date(System.currentTimeMillis())
                    + " - Rows affected: " + rowsAffected);

            // Verify the inserted/updated data
            ps = conn.prepareStatement("SELECT total_revenue, total_orders, total_sales FROM RevenueReports WHERE store_id = ? AND report_date = CAST(GETDATE() AS DATE)");
            ps.setInt(1, storeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                double storedRevenue = rs.getDouble("total_revenue");
                int storedOrders = rs.getInt("total_orders");
                int storedSales = rs.getInt("total_sales");
                System.out.println("Stored data in RevenueReports - Store ID: " + storeId + ", Revenue: " + storedRevenue
                        + ", Orders: " + storedOrders + ", Sales: " + storedSales);
            }
            rs.close();
            ps.close();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Error in generateTodayReport for Store ID " + storeId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    // Helper method to get individual orders for debugging
    private String getIndividualOrders(int storeId) {
        String query = "SELECT o.order_id, o.total_amount "
                + "FROM Orders o "
                + "WHERE o.order_date = CAST(GETDATE() AS DATE) "
                + "AND o.order_status = 'completed'";
        StringBuilder result = new StringBuilder();
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                result.append("Order ID: ").append(rs.getInt("order_id"))
                        .append(", Amount: ").append(rs.getDouble("total_amount")).append("; ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return result.toString();
    }

    // Close database resources
    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            // Không đóng conn ở đây vì nó được quản lý ở cấp cao hơn
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
