
package com.mvc.model;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author admin
 */
public class Order {
    private int order_id;
    private int customer_id;
    private double total_amount;
    private LocalDate order_date;
    private String shipping_address;
    private String order_status;
    private int payment_id;
    private int promotion_id;
    private String first_name; 
    private String last_name;
    private String phone; 
    private String email; 
    private List<String> nameProducts; // Sửa thành List<String> để lưu danh sách sản phẩm
    private int quantity; // Tổng số lượng
    private List<ViewProductInOrder> ViewProductInOrders;
    

    public Order() {
    }

    public Order(int order_id, int customer_id, double total_amount, LocalDate order_date, String shipping_address, String order_status, int payment_id, int promotion_id) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.total_amount = total_amount;
        this.order_date = order_date;
        this.shipping_address = shipping_address;
        this.order_status = order_status;
        this.payment_id = payment_id;
        this.promotion_id = promotion_id;
    }
    

    public Order(int order_id, double total_amount, LocalDate order_date, String shipping_address, String order_status, String first_name, String last_name) {
        this.order_id = order_id;
        this.total_amount = total_amount;
        this.order_date = order_date;
        this.shipping_address = shipping_address;
        this.order_status = order_status;
        this.first_name = first_name;
        this.last_name = last_name;
    }

        public Order(int order_id, int customer_id, double total_amount, LocalDate order_date, String shipping_address, 
                 String order_status, int payment_id, int promotion_id, String first_name, String last_name, 
                 String phone, String email, List<String> nameProducts, int quantity, List<ViewProductInOrder> ViewProductInOrders ) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.total_amount = total_amount;
        this.order_date = order_date;
        this.shipping_address = shipping_address;
        this.order_status = order_status;
        this.payment_id = payment_id;
        this.promotion_id = promotion_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.email = email;
        this.nameProducts = nameProducts;
        this.quantity = quantity;
        this.ViewProductInOrders = ViewProductInOrders;
    }

    public List<ViewProductInOrder> getViewProductInOrders() {
        return ViewProductInOrders;
    }

    public void setViewProductInOrders(List<ViewProductInOrder> ViewProductInOrders) {
        this.ViewProductInOrders = ViewProductInOrders;
    }
        
    
        
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public LocalDate getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDate order_date) {
        this.order_date = order_date;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public int getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public int getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(int promotion_id) {
        this.promotion_id = promotion_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getNameProducts() {
        return nameProducts;
    }

    public void setNameProducts(List<String> nameProducts) {
        this.nameProducts = nameProducts;
    }

    

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    

    @Override
    public String toString() {
        return "Order{" + "order_id=" + order_id + ", customer_id=" + customer_id + ", total_amount=" + total_amount + ", order_date=" + order_date + ", shipping_address=" + shipping_address + ", order_status=" + order_status + ", payment_id=" + payment_id + ", promotion_id=" + promotion_id + '}';
    }
}
