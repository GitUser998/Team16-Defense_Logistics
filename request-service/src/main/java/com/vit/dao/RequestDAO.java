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
import com.vit.model.Request;

@Repository
public class RequestDAO {

    @Value("${db.url}")
    private String URL;

    @Value("${db.user}")
    private String USER;

    @Value("${db.password}")
    private String PASSWORD;

    private final String COLUMNS = "request_id, requester_unit, item_requested, description, priority, request_date, status, assigned_to, resolution_notes, allocated_vehicle, allocated_equipment, location, personnel_count, supply_item_id, supply_quantity, allocated_personnel, allocated_supply";

    private Request makeRequest(ResultSet rs) throws SQLException {
        return new Request(
                rs.getString("request_id"),
                rs.getString("requester_unit"),
                rs.getString("item_requested"),
                rs.getString("description"),
                rs.getString("priority"),
                rs.getString("request_date"),
                rs.getString("status"),
                rs.getString("assigned_to"),
                rs.getString("resolution_notes"),
                rs.getString("allocated_vehicle"),
                rs.getString("allocated_equipment"),
                rs.getString("location"),
                rs.getString("personnel_count"),
                rs.getString("supply_item_id"),
                rs.getString("supply_quantity"),
                rs.getString("allocated_personnel"),
                rs.getString("allocated_supply"));
    }

    public List<Request> findAll() {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM request_table ORDER BY request_date DESC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(makeRequest(rs));
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
        return list;
    }

    public List<Request> findByStatus(String status) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM request_table WHERE status = ? ORDER BY request_date DESC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(makeRequest(rs));
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
        return list;
    }

    public Request findById(String id) {
        String sql = "SELECT " + COLUMNS + " FROM request_table WHERE request_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return makeRequest(rs);
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
        return null;
    }

    public void insert(Request r) {
        String sql = "INSERT INTO request_table (request_id, requester_unit, item_requested, description, priority, request_date, status, location, personnel_count, supply_item_id, supply_quantity) VALUES (?, ?, ?, ?, ?, CURRENT_DATE, 'SUBMITTED', ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRequestId());
            ps.setString(2, r.getRequesterUnit());
            ps.setString(3, r.getItemRequested());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getPriority());
            ps.setString(6, r.getLocation());
            ps.setString(7, r.getPersonnelCount());
            ps.setString(8, r.getSupplyItemId());
            ps.setString(9, r.getSupplyQuantity());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
    }

    public void updateStatus(String id, String status) {
        String sql = "UPDATE request_table SET status = ? WHERE request_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
    }

    public void assign(String id, String assignee) {
        String sql = "UPDATE request_table SET status = 'ASSIGNED', assigned_to = ? WHERE request_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignee);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
    }

    // saves what was allocated, old notes stay if there are no new notes
    public void setAllocation(String id, String vehicleId, String equipmentId,
                              String personnel, String supply, String notes) {
        String sql = "UPDATE request_table SET status = 'ALLOCATED', allocated_vehicle = ?, allocated_equipment = ?, allocated_personnel = ?, allocated_supply = ?, resolution_notes = COALESCE(?, resolution_notes) WHERE request_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicleId);
            ps.setString(2, equipmentId);
            ps.setString(3, personnel);
            ps.setString(4, supply);
            ps.setString(5, notes);
            ps.setString(6, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
    }

    public void complete(String id, String notes) {
        String sql = "UPDATE request_table SET status = 'COMPLETED', resolution_notes = ? WHERE request_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notes);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
    }

    public void reject(String id, String notes) {
        String sql = "UPDATE request_table SET status = 'REJECTED', resolution_notes = ? WHERE request_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notes);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e.getMessage(), e); }
    }
}
