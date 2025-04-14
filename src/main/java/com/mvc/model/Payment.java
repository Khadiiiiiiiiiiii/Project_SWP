/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mvc.model;

import java.time.LocalDate;

/**
 *
 * @author admin
 */
public class Payment {
    private int payment_id;
    private LocalDate payment_date;
    private double amount;
    private String payment_status;

    public Payment() {
    }

    public Payment(int payment_id, LocalDate payment_date, double amount, String payment_status) {
        this.payment_id = payment_id;
        this.payment_date = payment_date;
        this.amount = amount;
        this.payment_status = payment_status;
    }

    public int getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public LocalDate getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(LocalDate payment_date) {
        this.payment_date = payment_date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    @Override
    public String toString() {
        return "Payment{" + "payment_id=" + payment_id + ", payment_date=" + payment_date + ", amount=" + amount + ", payment_status=" + payment_status + '}';
    }
}
