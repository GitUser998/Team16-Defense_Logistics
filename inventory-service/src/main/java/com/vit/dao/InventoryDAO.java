package com.vit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.vit.model.InventoryItem;

@Repository
public class InventoryDAO {

    @Value("${db.url}")
    private String URL;

    @Value("${db.user}")
    private String USER;

    @Value("${db.password}")
    private String PASSWORD;

    private InventoryItem makeItem(ResultSet rs) throws SQLException {
        return new InventoryItem(
                rs.getString("item_id"),
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getInt("reorder_level"),
                rs.getString("unit"),
                rs.getString("location"));
    }

    public List<InventoryItem> findAll() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT item_id, item_name, category, quantity, reorder_level, unit, location FROM inventory_table";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeItem(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    public List<InventoryItem> findLowStock() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT item_id, item_name, category, quantity, reorder_level, unit, location FROM inventory_table WHERE quantity <= reorder_level";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeItem(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    public InventoryItem findById(String id) {
        InventoryItem item = null;
        String sql = "SELECT item_id, item_name, category, quantity, reorder_level, unit, location FROM inventory_table WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                item = makeItem(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return item;
    }

    public void insert(InventoryItem i) {
        String sql = "INSERT INTO inventory_table (item_id, item_name, category, quantity, reorder_level, unit, location) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, i.getItemId());
            ps.setString(2, i.getItemName());
            ps.setString(3, i.getCategory());
            ps.setInt(4, i.getQuantity());
            ps.setInt(5, i.getReorderLevel());
            ps.setString(6, i.getUnit());
            ps.setString(7, i.getLocation());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void update(InventoryItem i) {
        String sql = "UPDATE inventory_table SET item_name = ?, category = ?, quantity = ?, reorder_level = ?, unit = ?, location = ? WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, i.getItemName());
            ps.setString(2, i.getCategory());
            ps.setInt(3, i.getQuantity());
            ps.setInt(4, i.getReorderLevel());
            ps.setString(5, i.getUnit());
            ps.setString(6, i.getLocation());
            ps.setString(7, i.getItemId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // reduce stock, returns 1 if done, 0 if stock is less or item not found
    public int issue(String id, int qty) {
        int rows = 0;
        String sql = "UPDATE inventory_table SET quantity = quantity - ? WHERE item_id = ? AND quantity >= ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setString(2, id);
            ps.setInt(3, qty);
            rows = ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return rows;
    }

    public void delete(String id) {
        String sql = "DELETE FROM inventory_table WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}