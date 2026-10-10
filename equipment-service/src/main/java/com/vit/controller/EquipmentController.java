package com.vit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vit.dao.EquipmentDAO;
import com.vit.model.Equipment;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentDAO dao;

    @GetMapping
    public List<Equipment> getAll() {
        return dao.findAll();
    }

    @GetMapping("/available")
    public List<Equipment> getAvailable() {
        return dao.findAvailable();
    }

    @GetMapping("/{id}")
    public Equipment getById(@PathVariable String id) {
        return dao.findById(id);
    }

    @PostMapping
    public String create(@RequestBody Equipment e) {
        dao.insert(e);
        return "Equipment created";
    }

    @PutMapping
    public String update(@RequestBody Equipment e) {
        dao.update(e);
        return "Equipment updated";
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable String id, @RequestParam String status) {
        dao.updateStatus(id, status);
        return "Status updated";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        dao.delete(id);
        return "Equipment deleted";
    }
}