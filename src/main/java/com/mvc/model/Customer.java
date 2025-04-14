package com.mvc.model;

import java.sql.Timestamp;
import java.util.List;

public class Customer {

    private int customerId; // Thêm customerId để khớp với khóa chính của bảng Customer
    private int userId;
    private User user; // Liên kết với thông tin User để lấy firstName, lastName
    private Timestamp createdAt; // Thêm nếu có trong bảng Customer
    private Timestamp updatedAt; // Thêm nếu có trong bảng Customer
    private String email;
    private String password; // Thêm password để hỗ trợ updatePassword trong DAO
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Integer loyaltyPoints;
    private String preferredPaymentMethod;
    private List<Orderr> orders; // Sửa Orderr thành Order
    private String status;

    // Constructor mặc định
    public Customer() {
    }

    public Customer(int customerId, int userId, User user, Timestamp createdAt, Timestamp updatedAt, String email, String password, String firstName, String lastName, String phone, String address, Integer loyaltyPoints, String preferredPaymentMethod, List<Orderr> orders, String status) {
        this.customerId = customerId;
        this.userId = userId;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
        this.preferredPaymentMethod = preferredPaymentMethod;
        this.orders = orders;
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public List<Orderr> getOrders() {
        return orders;
    }

    public void setOrders(List<Orderr> orders) {
        this.orders = orders;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
