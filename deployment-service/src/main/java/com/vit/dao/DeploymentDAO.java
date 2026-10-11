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

import com.vit.model.Deployment;

@Repository
public class DeploymentDAO {

    @Value("${db.url}")
    private String URL;

    @Value("${db.user}")
    private String USER;

    @Value("${db.password}")
    private String PASSWORD;

    public List<Deployment> findAll() {
        List<Deployment> list = new ArrayList<>();
        String sql = "SELECT deployment_id, unit_code, destination, status, deployment_date FROM deployment_table";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Deployment(
                        rs.getString("deployment_id"),
                        rs.getString("unit_code"),
                        rs.getString("destination"),
                        rs.getString("status"),
                        rs.getString("deployment_date")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    public Deployment findById(String id) {
        Deployment deployment = null;
        String sql = "SELECT deployment_id, unit_code, destination, status, deployment_date FROM deployment_table WHERE deployment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                deployment = new Deployment(
                        rs.getString("deployment_id"),
                        rs.getString("unit_code"),
                        rs.getString("destination"),
                        rs.getString("status"),
                        rs.getString("deployment_date"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return deployment;
    }

    public void insert(Deployment d) {
        String sql = "INSERT INTO deployment_table (deployment_id, unit_code, destination, status, deployment_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getDeploymentId());
            ps.setString(2, d.getUnitCode());
            ps.setString(3, d.getDestination());
            ps.setString(4, d.getStatus());
            ps.setDate(5, Date.valueOf(d.getDeploymentDate()));
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void update(Deployment d) {
        String sql = "UPDATE deployment_table SET unit_code = ?, destination = ?, status = ?, deployment_date = ? WHERE deployment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getUnitCode());
            ps.setString(2, d.getDestination());
            ps.setString(3, d.getStatus());
            ps.setDate(4, Date.valueOf(d.getDeploymentDate()));
            ps.setString(5, d.getDeploymentId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateStatus(String id, String status) {
        String sql = "UPDATE deployment_table SET status = ? WHERE deployment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM deployment_table WHERE deployment_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}