package com.vit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vit.dao.DeploymentDAO;
import com.vit.model.Deployment;

@RestController
@RequestMapping("/deployments")
public class DeploymentController {

    @Autowired
    private DeploymentDAO dao;

    @GetMapping
    public List<Deployment> getAll() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public Deployment getById(@PathVariable String id) {
        return dao.findById(id);
    }

    @PostMapping
    public String create(@RequestBody Deployment d) {
        dao.insert(d);
        return "Deployment created";
    }

    @PutMapping
    public String update(@RequestBody Deployment d) {
        dao.update(d);
        return "Deployment updated";
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable String id, @RequestParam String status) {
        dao.updateStatus(id, status);
        return "Status updated";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        dao.delete(id);
        return "Deployment deleted";
    }
}