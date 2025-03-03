package com.mvc.model;

import java.sql.Date;

public class Staff {
    private String staff_id;
    private String user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String address;
    private String role;
    private Date hired_date;
    private String password; 

    public Staff() {
    }

    public Staff(String staff_id, String user_id, String first_name, String last_name, String email, String phone, String address, String role, Date hired_date) {
        this.staff_id = staff_id;
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.hired_date = hired_date;
    }

    public Staff(String staff_id, String user_id, String role, Date hired_date) {
        this.staff_id = staff_id;
        this.user_id = user_id;
        this.role = role;
        this.hired_date = hired_date;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getHired_date() {
        return hired_date;
    }

    public void setHired_date(Date hired_date) {
        this.hired_date = hired_date;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}