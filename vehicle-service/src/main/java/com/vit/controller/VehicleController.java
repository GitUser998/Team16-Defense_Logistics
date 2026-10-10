package com.vit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vit.dao.VehicleDAO;
import com.vit.model.Vehicle;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleDAO dao;

    @GetMapping
    public List<Vehicle> getAll() {
        return dao.findAll();
    }

    @GetMapping("/available")
    public List<Vehicle> getAvailable() {
        return dao.findAvailable();
    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable String id) {
        return dao.findById(id);
    }

    @PostMapping
    public String create(@RequestBody Vehicle v) {
        dao.insert(v);
        return "Vehicle created";
    }

    @PutMapping
    public String update(@RequestBody Vehicle v) {
        dao.update(v);
        return "Vehicle updated";
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable String id, @RequestParam String status) {
        dao.updateStatus(id, status);
        return "Status updated";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        dao.delete(id);
        return "Vehicle deleted";
    }
}