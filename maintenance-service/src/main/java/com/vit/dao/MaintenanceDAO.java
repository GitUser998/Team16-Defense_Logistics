package com.vit.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.vit.model.Maintenance;

@Repository
public class MaintenanceDAO {

    @Value("${db.url}")
    private String URL;

    @Value("${db.user}")
    private String USER;

    @Value("${db.password}")
    private String PASSWORD;

    private final String COLUMNS = "maintenance_id, asset_type, asset_id, maintenance_type, priority, "
            + "scheduled_date, description, technician, status, completed_date, work_done";

    private Maintenance makeMaintenance(ResultSet rs) throws SQLException {
        return new Maintenance(
                rs.getString("maintenance_id"),
                rs.getString("asset_type"),
                rs.getString("asset_id"),
                rs.getString("maintenance_type"),
                rs.getString("priority"),
                rs.getString("scheduled_date"),
                rs.getString("description"),
                rs.getString("technician"),
                rs.getString("status"),
                rs.getString("completed_date"),
                rs.getString("work_done"));
    }

    public List<Maintenance> findAll() {
        List<Maintenance> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM maintenance_table ORDER BY scheduled_date";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeMaintenance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    // scheduled jobs from today till the next "days" days
    public List<Maintenance> findUpcoming(int days) {
        List<Maintenance> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM maintenance_table "
                + "WHERE status = 'SCHEDULED' AND scheduled_date BETWEEN CURRENT_DATE AND CURRENT_DATE + ? "
                + "ORDER BY scheduled_date";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeMaintenance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    // scheduled jobs whose date is already over
    public List<Maintenance> findOverdue() {
        List<Maintenance> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM maintenance_table "
                + "WHERE status = 'SCHEDULED' AND scheduled_date < CURRENT_DATE "
                + "ORDER BY scheduled_date";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeMaintenance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    // service history of a vehicle or equipment (completed jobs)
    public List<Maintenance> findHistory(String assetId) {
        List<Maintenance> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM maintenance_table "
                + "WHERE asset_id = ? AND status = 'COMPLETED' ORDER BY completed_date DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, assetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(makeMaintenance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    public Maintenance findById(String id) {
        Maintenance maintenance = null;
        String sql = "SELECT " + COLUMNS + " FROM maintenance_table WHERE maintenance_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maintenance = makeMaintenance(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return maintenance;
    }

    public void insert(Maintenance m) {
        String sql = "INSERT INTO maintenance_table (maintenance_id, asset_type, asset_id, maintenance_type, "
                + "priority, scheduled_date, description, technician, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getMaintenanceId());
            ps.setString(2, m.getAssetType());
            ps.setString(3, m.getAssetId());
            ps.setString(4, m.getMaintenanceType());
            ps.setString(5, m.getPriority());
            ps.setDate(6, Date.valueOf(m.getScheduledDate()));
            ps.setString(7, m.getDescription());
            ps.setString(8, m.getTechnician());
            ps.setString(9, m.getStatus());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void update(Maintenance m) {
        String sql = "UPDATE maintenance_table SET asset_type = ?, asset_id = ?, maintenance_type = ?, "
                + "priority = ?, scheduled_date = ?, description = ?, technician = ?, status = ? "
                + "WHERE maintenance_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getAssetType());
            ps.setString(2, m.getAssetId());
            ps.setString(3, m.getMaintenanceType());
            ps.setString(4, m.getPriority());
            ps.setDate(5, Date.valueOf(m.getScheduledDate()));
            ps.setString(6, m.getDescription());
            ps.setString(7, m.getTechnician());
            ps.setString(8, m.getStatus());
            ps.setString(9, m.getMaintenanceId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateStatus(String id, String status) {
        String sql = "UPDATE maintenance_table SET status = ? WHERE maintenance_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // mark the job completed today and save the work done (this is the service history)
    public void complete(String id, String workDone) {
        String sql = "UPDATE maintenance_table SET status = 'COMPLETED', completed_date = CURRENT_DATE, "
                + "work_done = ? WHERE maintenance_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, workDone);
            ps.setString(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM maintenance_table WHERE maintenance_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}