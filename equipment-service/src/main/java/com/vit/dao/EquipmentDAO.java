package com.vit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.vit.model.Equipment;

@Repository
public class EquipmentDAO {

    private final String URL = "jdbc:postgresql://localhost:5432/equipment_db";
    private final String USER = "postgres";
    private final String PASSWORD = "admin";

    private Equipment makeEquipment(ResultSet rs) throws SQLException {
        return new Equipment(
                rs.getString("equipment_id"),
                rs.getString("equipment_name"),
                rs.getString("category"),
                rs.getString("owner_unit"),
                rs.getString("location"),
                rs.getString("operational_condition"),
                rs.getString("status"));
    }

    public List<Equipment> findAll() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT equipment_id, equipment_name, category, owner_unit, location, operational_condition, status FROM equipment_table";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeEquipment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Equipment> findAvailable() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT equipment_id, equipment_name, category, owner_unit, location, operational_condition, status FROM equipment_table WHERE status = 'AVAILABLE'";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeEquipment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Equipment findById(String id) {
        Equipment equipment = null;
        String sql = "SELECT equipment_id, equipment_name, category, owner_unit, location, operational_condition, status FROM equipment_table WHERE equipment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                equipment = makeEquipment(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipment;
    }

    public void insert(Equipment e) {
        String sql = "INSERT INTO equipment_table (equipment_id, equipment_name, category, owner_unit, location, operational_condition, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getEquipmentId());
            ps.setString(2, e.getEquipmentName());
            ps.setString(3, e.getCategory());
            ps.setString(4, e.getOwnerUnit());
            ps.setString(5, e.getLocation());
            ps.setString(6, e.getOperationalCondition());
            ps.setString(7, e.getStatus());
            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(Equipment e) {
        String sql = "UPDATE equipment_table SET equipment_name = ?, category = ?, owner_unit = ?, location = ?, operational_condition = ?, status = ? WHERE equipment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getEquipmentName());
            ps.setString(2, e.getCategory());
            ps.setString(3, e.getOwnerUnit());
            ps.setString(4, e.getLocation());
            ps.setString(5, e.getOperationalCondition());
            ps.setString(6, e.getStatus());
            ps.setString(7, e.getEquipmentId());
            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateStatus(String id, String status) {
        String sql = "UPDATE equipment_table SET status = ? WHERE equipment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM equipment_table WHERE equipment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}