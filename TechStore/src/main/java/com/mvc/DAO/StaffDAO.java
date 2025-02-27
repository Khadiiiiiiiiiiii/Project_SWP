package com.mvc.DAO;

import com.mvc.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private Connection connection;

    public StaffDAO(Connection connection) {
        this.connection = connection;
    }

    public List<User> getAllCustomers() {
        List<User> customers = new ArrayList<>();
        String query = "SELECT user_id, email, first_name, last_name, phone, address FROM Users WHERE role = 'customer'";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("user_id"));
                user.setEmail(resultSet.getString("email"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                customers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}
