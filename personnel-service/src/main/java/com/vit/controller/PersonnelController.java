package com.vit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vit.dao.PersonnelDAO;
import com.vit.model.Personnel;

@RestController
@RequestMapping("/personnel")
public class PersonnelController {

    @Autowired
    private PersonnelDAO dao;

    @GetMapping
    public List<Personnel> getAll() {
        return dao.findAll();
    }

    @GetMapping("/available")
    public List<Personnel> getAvailable() {
        return dao.findAvailable();
    }

    @GetMapping("/{id}")
    public Personnel getById(@PathVariable String id) {
        return dao.findById(id);
    }

    @PostMapping
    public String create(@RequestBody Personnel p) {
        dao.insert(p);
        return "Personnel created";
    }

    @PutMapping
    public String update(@RequestBody Personnel p) {
        dao.update(p);
        return "Personnel updated";
    }

    @PutMapping("/{id}/availability")
    public String updateAvailability(@PathVariable String id, @RequestParam String availability) {
        dao.updateAvailability(id, availability);
        return "Availability updated";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        dao.delete(id);
        return "Personnel deleted";
    }
}