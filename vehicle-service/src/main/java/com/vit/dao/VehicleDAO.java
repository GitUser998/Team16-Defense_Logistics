package com.vit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.vit.model.Vehicle;

@Repository
public class VehicleDAO {

    private final String URL = "jdbc:postgresql://localhost:5432/vehicle_db";
    private final String USER = "postgres";
    private final String PASSWORD = "admin";

    public List<Vehicle> findAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT vehicle_id, vehicle_type, registration_no, driver_name, location, status FROM vehicle_table";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("registration_no"),
                        rs.getString("driver_name"),
                        rs.getString("location"),
                        rs.getString("status")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Vehicle> findAvailable() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT vehicle_id, vehicle_type, registration_no, driver_name, location, status FROM vehicle_table WHERE status = 'AVAILABLE'";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("registration_no"),
                        rs.getString("driver_name"),
                        rs.getString("location"),
                        rs.getString("status")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Vehicle findById(String id) {
        Vehicle vehicle = null;
        String sql = "SELECT vehicle_id, vehicle_type, registration_no, driver_name, location, status FROM vehicle_table WHERE vehicle_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vehicle = new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("vehicle_type"),
                        rs.getString("registration_no"),
                        rs.getString("driver_name"),
                        rs.getString("location"),
                        rs.getString("status"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicle;
    }

    public void insert(Vehicle v) {
        String sql = "INSERT INTO vehicle_table (vehicle_id, vehicle_type, registration_no, driver_name, location, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getVehicleId());
            ps.setString(2, v.getVehicleType());
            ps.setString(3, v.getRegistrationNo());
            ps.setString(4, v.getDriverName());
            ps.setString(5, v.getLocation());
            ps.setString(6, v.getStatus());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Vehicle v) {
        String sql = "UPDATE vehicle_table SET vehicle_type = ?, registration_no = ?, driver_name = ?, location = ?, status = ? WHERE vehicle_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getVehicleType());
            ps.setString(2, v.getRegistrationNo());
            ps.setString(3, v.getDriverName());
            ps.setString(4, v.getLocation());
            ps.setString(5, v.getStatus());
            ps.setString(6, v.getVehicleId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(String id, String status) {
        String sql = "UPDATE vehicle_table SET status = ? WHERE vehicle_id = ?";

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
        String sql = "DELETE FROM vehicle_table WHERE vehicle_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}