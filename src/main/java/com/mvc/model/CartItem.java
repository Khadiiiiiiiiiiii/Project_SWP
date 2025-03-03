package com.mvc.model;

import java.sql.Timestamp;

public class CartItem {
    private int cartItemId;
    private int cartId;
    private int productId;
    private int quantity;
    private Timestamp addedAt;

    public CartItem() {
    }

    public CartItem(int cartItemId, int cartId, int productId, int quantity, Timestamp addedAt) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

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
}
