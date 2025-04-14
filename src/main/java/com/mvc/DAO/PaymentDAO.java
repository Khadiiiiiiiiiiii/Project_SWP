/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mvc.DAO;

import com.mvc.model.Payment;
import com.mvc.dal.DBContext;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 *
 * @author admin
 */
public class PaymentDAO {

    public Payment createPayment(double amount) {
        String sql = "INSERT INTO dbo.Payments (payment_date, amount, payment_status) VALUES (?, ?, ?)";
        try ( Connection conn = DBContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(2, BigDecimal.valueOf(amount));
            ps.setString(3, "Pending");
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {

                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int paymentId = rs.getInt(1);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                        return new Payment(paymentId, localDate, amount, "Pending");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during creating payment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {
        PaymentDAO pd = new PaymentDAO();
        double amount = 1670000000;
        Payment p = pd.createPayment(amount);
        System.out.println(p.getPayment_id());
    }
}
