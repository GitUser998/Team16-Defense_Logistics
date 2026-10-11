package com.vit.controller;

import com.vit.model.Personnel;
import com.vit.dao.PersonnelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    @Autowired
    private PersonnelDao personnelDao;

    @GetMapping
    public List<Personnel> getAllPersonnel() {
        return personnelDao.findAll();
    }
}