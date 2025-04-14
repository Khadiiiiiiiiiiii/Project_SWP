package com.mvc.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RevenueReport {

    private int reportId;
    private int storeId;
    private Date reportDate;
    private BigDecimal totalRevenue;
    private int totalOrders;
    private int totalSales;
    private String storeName;
    private List<Map<String, Object>> orderDetails; // Thêm danh sách chi tiết đơn hàng

    // Constructors
    public RevenueReport() {
    }

    public RevenueReport(int reportId, int storeId, Date reportDate, BigDecimal totalRevenue,
            int totalOrders, int totalSales, String storeName) {
        this.reportId = reportId;
        this.storeId = storeId;
        this.reportDate = reportDate;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalSales = totalSales;
        this.storeName = storeName;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<Map<String, Object>> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<Map<String, Object>> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "RevenueReport{"
                + "reportId=" + reportId
                + ", storeId=" + storeId
                + ", reportDate=" + reportDate
                + ", totalRevenue=" + totalRevenue
                + ", totalOrders=" + totalOrders
                + ", totalSales=" + totalSales
                + ", storeName='" + storeName + '\''
                + ", orderDetails=" + orderDetails
                + '}';
    }
}
