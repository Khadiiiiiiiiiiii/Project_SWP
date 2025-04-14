package com.mvc.model;

import java.util.Date;

public class Store {

    private int storeId;
    private int staffManagementId;
    private String storeName;
    private String location;
    private Date createdAt;
    private Date updatedAt;

    // Default constructor
    public Store() {
    }

    // Constructor with all fields
    public Store(int storeId, int staffManagementId, String storeName, String location, Date createdAt, Date updatedAt) {
        this.storeId = storeId;
        this.staffManagementId = staffManagementId;
        this.storeName = storeName;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getStaffManagementId() {
        return staffManagementId;
    }

    public void setStaffManagementId(int staffManagementId) {
        this.staffManagementId = staffManagementId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Store{"
                + "storeId=" + storeId
                + ", staffManagementId=" + staffManagementId
                + ", storeName='" + storeName + '\''
                + ", location='" + location + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
