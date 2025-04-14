package com.mvc.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Promotion {
    private int promotionId;
    private int storeId;
    private String code;
    private BigDecimal discountPercentage;
    private Date expirationDate;
    private String status;

    // Default constructor
    public Promotion() {
    }

    // Constructor with all fields
    public Promotion(int promotionId, String code, BigDecimal discountPercentage, Date expirationDate, String status, int storeId) {
        this.promotionId = promotionId;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.expirationDate = expirationDate;
        this.status = status;
        this.storeId = storeId;
    }

    // Constructor without storeId (if needed for backward compatibility)
    public Promotion(int promotionId, String code, BigDecimal discountPercentage, Date expirationDate, String status) {
        this.promotionId = promotionId;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.expirationDate = expirationDate;
        this.status = status;
    }

    // Getters and setters
    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}