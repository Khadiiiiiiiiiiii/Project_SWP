package com.mvc.DAO;

import com.mvc.dal.DBContext;
import com.mvc.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaffDAO {

    private static final Logger LOGGER = Logger.getLogger(StaffDAO.class.getName());

    public List<User> getAllCustomers() {
        List<User> customers = new ArrayList<>();
        try {
            Connection conn = DBContext.getConnection();
            String sql = "SELECT user_id, first_name, last_name, email, phone, address FROM Users WHERE role = 'customer'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                customers.add(user);
            }

            LOGGER.info("DAO: Retrieved " + customers.size() + " customers");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }
        return customers;
    }
}
