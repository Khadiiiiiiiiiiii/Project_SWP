package com.mvc.model;

import java.sql.Timestamp;

/**
 * Class đại diện cho một người dùng trong hệ thống, bao gồm thông tin cá nhân và vai trò. Dùng để hỗ trợ Discount Management (phân quyền cho Admin, Store Manager, Customer, Staff).
 */
public class User {

    private int userId;
    private String email;
    private String passwordHash; // Sử dụng password_hash thay vì password cho khớp với database
    private String role;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;

    // Constructor mặc định
    public User() {
    }

    public User(int userId, String email, String passwordHash, String role, String firstName, String lastName, String phone, String address, Timestamp createdAt, Timestamp updatedAt, String status) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
