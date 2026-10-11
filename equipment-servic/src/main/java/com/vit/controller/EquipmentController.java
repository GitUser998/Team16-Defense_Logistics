package com.vit.controller;

import com.vit.model.Equipment;
import com.vit.dao.EquipmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentDao equipmentDao;

    @GetMapping
    public List<Equipment> getAllEquipment() {
        return equipmentDao.findAll();
    }
}