package com.vit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vit.dao.InventoryDAO;
import com.vit.model.InventoryItem;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryDAO dao;

    @GetMapping
    public List<InventoryItem> getAll() {
        return dao.findAll();
    }

    @GetMapping("/low-stock")
    public List<InventoryItem> getLowStock() {
        return dao.findLowStock();
    }

    @GetMapping("/{id}")
    public InventoryItem getById(@PathVariable String id) {
        return dao.findById(id);
    }

    @PostMapping
    public String create(@RequestBody InventoryItem i) {
        dao.insert(i);
        return "Item created";
    }

    @PutMapping
    public String update(@RequestBody InventoryItem i) {
        dao.update(i);
        return "Item updated";
    }

    @PutMapping("/{id}/issue")
    public String issue(@PathVariable String id, @RequestParam int quantity) {
        int rows = dao.issue(id, quantity);
        if (rows > 0) {
            return "Issued " + quantity;
        }
        return "Not enough stock or item not found";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        dao.delete(id);
        return "Item deleted";
    }
}