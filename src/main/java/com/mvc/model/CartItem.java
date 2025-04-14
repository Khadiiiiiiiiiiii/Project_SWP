package com.mvc.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Class đại diện cho một mục trong giỏ hàng, chứa thông tin về sản phẩm, số lượng, và giảm giá (trực tiếp hoặc mã giảm giá).
 * Hỗ trợ Discount Management với giảm giá trực tiếp và mã giảm giá.
 */
public class CartItem {
    private int cartItemId;
    private int cartId;
    private int productId;
    private int quantity;
    private Timestamp addedAt;
    private BigDecimal price; // Giá gốc của sản phẩm
    private BigDecimal discountPrice; // Phần trăm giảm giá trực tiếp của sản phẩm
    private Integer promotionId; // ID của mã giảm giá áp dụng (nếu có)
    private String promoCode; // Mã giảm giá áp dụng (nếu có)
    private BigDecimal discountPercentage; // Phần trăm giảm giá từ promo_code trong CartItems

    public CartItem() {
    }

    public CartItem(int cartItemId, int cartId, int productId, int quantity, Timestamp addedAt, 
                    BigDecimal price, BigDecimal discountPrice) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    // Getters and Setters
    public int getCartItemId() { return cartItemId; }
    public void setCartItemId(int cartItemId) { this.cartItemId = cartItemId; }
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }
    public Integer getPromotionId() { return promotionId; }
    public void setPromotionId(Integer promotionId) { this.promotionId = promotionId; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    /**
     * Tính giá đã giảm của mục này, kết hợp giữa giảm giá trực tiếp và mã giảm giá (nếu có).
     * @return Giá đã giảm sau khi áp dụng cả hai loại giảm giá
     */
    public BigDecimal getDiscountedPrice() {
        BigDecimal basePrice = price != null ? price : BigDecimal.ZERO;
        if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal directDiscountFactor = discountPrice.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
            basePrice = basePrice.multiply(BigDecimal.ONE.subtract(directDiscountFactor));
        }
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal promoDiscountFactor = discountPercentage.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
            basePrice = basePrice.multiply(BigDecimal.ONE.subtract(promoDiscountFactor));
        }
        return basePrice.max(BigDecimal.ZERO); // Đảm bảo giá không âm
    }

    /**
     * Tính tổng giá của mục này (số lượng * giá đã giảm).
     * @return Tổng giá của mục sau khi áp dụng giảm giá
     */
    public BigDecimal getTotalPrice() {
        BigDecimal discountedPrice = getDiscountedPrice();
        return discountedPrice.multiply(new BigDecimal(quantity));
    }
}