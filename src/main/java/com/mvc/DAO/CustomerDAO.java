package com.mvc.DAO;

import com.mvc.dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO() {
        conn = DBContext.getConnection();
    }

    public int getCustomerIdByUserId(int userId) {
        String sql = "SELECT customer_id FROM Customer WHERE user_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
