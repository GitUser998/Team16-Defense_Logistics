package com.vit.dao;

import com.vit.model.Personnel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonnelDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Personnel> rowMapper = (rs, rowNum) -> new Personnel(
        rs.getLong("id"),
        rs.getString("service_number"),
        rs.getString("full_name"),
        rs.getString("rank"),
        rs.getString("unit_assignment"),
        rs.getString("status")
    );

    public List<Personnel> findAll() {
        String sql = "SELECT * FROM personnel";
        return jdbcTemplate.query(sql, rowMapper);
    }
}