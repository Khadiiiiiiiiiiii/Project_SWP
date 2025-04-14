package com.mvc.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Class đại diện cho giỏ hàng của khách hàng, chứa danh sách sản phẩm và số lượng.
 * Hỗ trợ Discount Management với giảm giá trực tiếp và mã giảm giá.
 */
public class Cart {
    private Map<Integer, CartItem> items = new HashMap<>();

    public void addItem(CartItem item) {
        items.put(item.getProductId(), item);
    }

    public void updateItem(int productId, int quantity) {
        if (items.containsKey(productId)) {
            items.get(productId).setQuantity(quantity);
        }
    }

    public void removeItem(int productId) {
        items.remove(productId);
    }

    public BigDecimal getTotalPrice() {
        return items.values().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Integer, CartItem> getItems() {
        return items;
    }
}