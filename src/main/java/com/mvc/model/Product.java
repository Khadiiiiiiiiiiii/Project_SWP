package com.mvc.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * Class đại diện cho một sản phẩm trong hệ thống, bao gồm thông tin giá, giảm
 * giá trực tiếp, và liên kết với mã giảm giá. Dùng để hỗ trợ Discount
 * Management (giảm giá trực tiếp và mã giảm giá từ Store Manager/Customer).
 */
public class Product {

    private int productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice; // Phần trăm giảm giá trực tiếp (ví dụ: 50.00 cho 50%)
    private int categoryId;
    private int stockQuantity;
    private String imageUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer promotionId; // Liên kết với mã giảm giá từ Promotions (nếu cần)
    private boolean isDeleted;

    // Constructor
    public Product(int productId, String name, String description, BigDecimal price,
            BigDecimal discountPrice, int categoryId, int stockQuantity,
            String imageUrl, Timestamp createdAt, Timestamp updatedAt, Integer promotionId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.categoryId = categoryId;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.promotionId = promotionId;
    }
    
    public Product() {
        this.isDeleted = false;
    }

    public Product(String name, String description, BigDecimal price, BigDecimal discountPrice, int categoryId, int stockQuantity, String imageUrl, Integer promotionId, boolean isDeleted) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.categoryId = categoryId;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.promotionId = promotionId;
        this.isDeleted = isDeleted;
    }
    
    

    public Product(int productId, String name, String description, BigDecimal price, BigDecimal discountPrice, int categoryId, int stockQuantity, String imageUrl, Integer promotionId, boolean isDeleted) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.categoryId = categoryId;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.promotionId = promotionId;
        this.isDeleted = isDeleted;
    }

    public Product(int productId, String name, String description, BigDecimal price, BigDecimal discountPrice, int categoryId, int stockQuantity, String imageUrl, Timestamp createdAt, Timestamp updatedAt, Integer promotionId, boolean isDeleted) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.categoryId = categoryId;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.promotionId = promotionId;
        this.isDeleted = isDeleted;
    }


    /**
     * Tính giá đã giảm dựa trên giảm giá trực tiếp (discountPrice) và mã giảm
     * giá (nếu có).
     *
     * @param promotionDiscount Phần trăm giảm giá từ mã giảm giá (nếu có), null
     * nếu không áp dụng
     * @return Giá sau khi áp dụng cả hai loại giảm giá
     */
    public BigDecimal getDiscountedPrice(BigDecimal promotionDiscount) {
        BigDecimal basePrice = (price != null) ? price : BigDecimal.ZERO;

        // Áp dụng giảm giá trực tiếp (discountPrice là phần trăm)
        if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal directDiscountFactor = discountPrice.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
            basePrice = basePrice.multiply(BigDecimal.ONE.subtract(directDiscountFactor))
                    .setScale(0, BigDecimal.ROUND_HALF_UP);
        }

        // Áp dụng mã giảm giá (nếu có)
        if (promotionDiscount != null && promotionDiscount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal promoDiscountFactor = promotionDiscount.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
            basePrice = basePrice.multiply(BigDecimal.ONE.subtract(promoDiscountFactor))
                    .setScale(0, BigDecimal.ROUND_HALF_UP);
        }

        return basePrice.max(BigDecimal.ZERO); // Đảm bảo giá không âm
    }

    /**
     * Tính giá đã giảm dựa trên giảm giá trực tiếp (discountPrice) mà không áp
     * dụng mã giảm giá.
     *
     * @return Giá sau khi áp dụng giảm giá trực tiếp
     */
    public BigDecimal getDiscountedPrice() {
        return getDiscountedPrice(null); // Gọi phiên bản với promotionDiscount = null
    }

    /**
     * Kiểm tra xem sản phẩm có đang được giảm giá trực tiếp hay không.
     *
     * @return true nếu có giảm giá trực tiếp, false nếu không
     */
    public boolean hasDirectDiscount() {
        return discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Tính giá đã giảm dựa trên giảm giá trực tiếp (discountPrice) mà không áp
     * dụng mã giảm giá, và trả về dưới dạng chuỗi định dạng.
     *
     * @return Giá đã giảm định dạng (VD: "7,500,000 VND")
     */
    public String getFormattedDiscountedPrice() {
        BigDecimal discounted = getDiscountedPrice();
        if (discounted != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(discounted) + " VND";
        }
        return getFormattedPrice();
    }

    /**
     * Tính giá đã giảm dựa trên giảm giá trực tiếp và mã giảm giá (nếu có), và
     * trả về dưới dạng chuỗi định dạng.
     *
     * @param promotionDiscount Phần trăm giảm giá từ mã (nếu có)
     * @return Giá đã giảm định dạng (VD: "3,750,000 VND")
     */
    public String getFormattedDiscountedPrice(BigDecimal promotionDiscount) {
        BigDecimal discounted = getDiscountedPrice(promotionDiscount);
        if (discounted != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(discounted) + " VND";
        }
        return getFormattedPrice();
    }

    /**
     * Định dạng giá gốc với dấu "," phân cách hàng nghìn, không có .00
     */
    public String getFormattedPrice() {
        if (price != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(price) + " VND";
        }
        return "0 VND";
    }

    /**
     * Định dạng phần trăm giảm giá cố định (chỉ hiển thị "50%" thay vì
     * "50.00%")
     */
    public String getFormattedDiscountPercentage() {
    if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0) {
        return "-" + discountPrice.setScale(0, BigDecimal.ROUND_HALF_UP) + "%";
    }
    return "0%";
}

    // Getters và setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    //Dùng trong JSP để gọi giá sau giảm
    public BigDecimal getDiscountedPriceRaw() {
        return getDiscountedPrice();
    }  
}
