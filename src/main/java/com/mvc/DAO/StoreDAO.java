package com.mvc.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.mvc.dal.DBContext;
import com.mvc.model.Store;

public class StoreDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Get all stores
    public List<Store> getAllStores() {
        List<Store> list = new ArrayList<>();
        String query = "SELECT * FROM Stores ORDER BY store_name";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                store.setLocation(rs.getString("location"));
                store.setCreatedAt(rs.getTimestamp("created_at"));
                store.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(store);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Get store by ID
    public Store getStoreById(int storeId) {
        String query = "SELECT * FROM Stores WHERE store_id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, storeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                store.setLocation(rs.getString("location"));
                store.setCreatedAt(rs.getTimestamp("created_at"));
                store.setUpdatedAt(rs.getTimestamp("updated_at"));
                return store;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return null;
    }

    // Add a new store
    public boolean addStore(Store store) {
        String query = "INSERT INTO Stores (store_name, location, created_at, updated_at) VALUES (?, ?, GETDATE(), GETDATE())";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, store.getStoreName());
            ps.setString(2, store.getLocation());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    // Update an existing store
    public boolean updateStore(Store store) {
        String query = "UPDATE Stores SET store_name = ?, location = ?, updated_at = GETDATE() WHERE store_id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, store.getStoreName());
            ps.setString(2, store.getLocation());
            ps.setInt(3, store.getStoreId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    // Delete a store
    public boolean deleteStore(int storeId) {
        String query = "DELETE FROM Stores WHERE store_id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, storeId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    // Close database resources
    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
