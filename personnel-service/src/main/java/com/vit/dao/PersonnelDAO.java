package com.vit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.vit.model.Personnel;

@Repository
public class PersonnelDAO {

    private final String URL = "jdbc:postgresql://localhost:5432/personnel_db";
    private final String USER = "postgres";
    private final String PASSWORD = "admin";

    private Personnel makePersonnel(ResultSet rs) throws SQLException {
        return new Personnel(
                rs.getString("personnel_id"),
                rs.getString("full_name"),
                rs.getString("personnel_role"),
                rs.getString("unit_code"),
                rs.getString("posting_location"),
                rs.getString("availability"));
    }

    public List<Personnel> findAll() {
        List<Personnel> list = new ArrayList<>();
        String sql = "SELECT personnel_id, full_name, personnel_role, unit_code, posting_location, availability FROM personnel_table";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makePersonnel(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Personnel> findAvailable() {
        List<Personnel> list = new ArrayList<>();
        String sql = "SELECT personnel_id, full_name, personnel_role, unit_code, posting_location, availability FROM personnel_table WHERE availability = 'AVAILABLE'";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makePersonnel(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Personnel findById(String id) {
        Personnel personnel = null;
        String sql = "SELECT personnel_id, full_name, personnel_role, unit_code, posting_location, availability FROM personnel_table WHERE personnel_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                personnel = makePersonnel(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personnel;
    }

    public void insert(Personnel p) {
        String sql = "INSERT INTO personnel_table (personnel_id, full_name, personnel_role, unit_code, posting_location, availability) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getPersonnelId());
            ps.setString(2, p.getFullName());
            ps.setString(3, p.getPersonnelRole());
            ps.setString(4, p.getUnitCode());
            ps.setString(5, p.getPostingLocation());
            ps.setString(6, p.getAvailability());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Personnel p) {
        String sql = "UPDATE personnel_table SET full_name = ?, personnel_role = ?, unit_code = ?, posting_location = ?, availability = ? WHERE personnel_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getFullName());
            ps.setString(2, p.getPersonnelRole());
            ps.setString(3, p.getUnitCode());
            ps.setString(4, p.getPostingLocation());
            ps.setString(5, p.getAvailability());
            ps.setString(6, p.getPersonnelId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAvailability(String id, String availability) {
        String sql = "UPDATE personnel_table SET availability = ? WHERE personnel_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, availability);
            ps.setString(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM personnel_table WHERE personnel_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}