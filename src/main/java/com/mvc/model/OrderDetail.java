/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mvc.model;

import java.math.BigDecimal;

/**
 *
 * @author admin
 */
public class OrderDetail {
    private int order_detail_id;
    private int order_id;
    private int product_id;
    private int quantity;
    private BigDecimal unit_price;

    public OrderDetail() {
    }

    public OrderDetail(int order_detail_id, int order_id, int product_id, int quantity, BigDecimal unit_price) {
        this.order_detail_id = order_detail_id;
        this.order_id = order_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.unit_price = unit_price;
    }

    public int getOrder_detail_id() {
        return order_detail_id;
    }

    public void setOrder_detail_id(int order_detail_id) {
        this.order_detail_id = order_detail_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "order_detail_id=" + order_detail_id + ", order_id=" + order_id + ", product_id=" + product_id + ", quantity=" + quantity + ", unit_price=" + unit_price + '}';
    }
}
