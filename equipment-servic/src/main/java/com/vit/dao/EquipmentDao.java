package com.vit.dao;

import com.vit.model.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Equipment> rowMapper = (rs, rowNum) -> new Equipment(
        rs.getLong("id"),
        rs.getString("equipment_name"),
        rs.getString("serial_number"),
        rs.getString("category"),
        rs.getString("assigned_unit"),
        rs.getString("operational_status")
    );

    public List<Equipment> findAll() {
        String sql = "SELECT * FROM equipment";
        return jdbcTemplate.query(sql, rowMapper);
    }
}