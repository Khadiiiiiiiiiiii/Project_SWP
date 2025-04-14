package com.mvc.model;

import java.sql.Timestamp;

/**
 *
 * @author lenam
 */
public class HistoryOrder {
    private int orderId;
    private double totalAmount;
    private Timestamp orderDate;
    private String shippingAddress;
    private String orderStatus;
    private int orderDetail;
    private String name;
    private String image;
    private int quantity;
    private double pricerByQuantity;
    // Thêm các thuộc tính mới cho thông tin mã giảm giá
    private String promoCode;
    private double discountPercentage;
    private double discountAmount;

    public HistoryOrder() {
    }

    public HistoryOrder(int orderId, double totalAmount, Timestamp orderDate, String shippingAddress, String orderStatus, 
                        int orderDetail, String name, String image, int quantity, double pricerByQuantity,
                        String promoCode, double discountPercentage, double discountAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
        this.orderDetail = orderDetail;
        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.pricerByQuantity = pricerByQuantity;
        this.promoCode = promoCode;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
    }

    // Getters và Setters hiện có
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(int orderDetail) {
        this.orderDetail = orderDetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricerByQuantity() {
        return pricerByQuantity;
    }

    public void setPricerByQuantity(double pricerByQuantity) {
        this.pricerByQuantity = pricerByQuantity;
    }

    // Thêm Getters và Setters cho các thuộc tính mới
    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public String toString() {
        return "HistoryOrder{" + "orderId=" + orderId + ", totalAmount=" + totalAmount + ", orderDate=" + orderDate + 
               ", shippingAddress=" + shippingAddress + ", orderStatus=" + orderStatus + ", orderDetail=" + orderDetail + 
               ", name=" + name + ", image=" + image + ", quantity=" + quantity + ", pricerByQuantity=" + pricerByQuantity + 
               ", promoCode=" + promoCode + ", discountPercentage=" + discountPercentage + ", discountAmount=" + discountAmount + '}';
    }
}