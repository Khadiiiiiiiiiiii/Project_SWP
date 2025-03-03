package com.mvc.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Integer, Integer> items; // Key: product_id, Value: quantity

    public Cart() {
        items = new HashMap<>();
    }

    public void addItem(int productId, int quantity) {
        items.put(productId, items.getOrDefault(productId, 0) + quantity);
    }

    public void updateItem(int productId, int quantity) {
        if (items.containsKey(productId)) {
            if (quantity > 0) {
                items.put(productId, quantity);
            } else {
                items.remove(productId);
            }
        }
    }

    public void removeItem(int productId) {
        items.remove(productId);
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    public void clearCart() {
        items.clear();
    }
}
