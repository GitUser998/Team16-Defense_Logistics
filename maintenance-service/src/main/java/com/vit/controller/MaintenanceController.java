package com.vit.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.vit.dao.MaintenanceDAO;
import com.vit.model.Maintenance;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceDAO dao;

    private RestTemplate rest = new RestTemplate();

    @GetMapping
    public List<Maintenance> getAll() {
        return dao.findAll();
    }

    @GetMapping("/upcoming")
    public List<Maintenance> getUpcoming(@RequestParam(defaultValue = "7") int days) {
        return dao.findUpcoming(days);
    }

    @GetMapping("/overdue")
    public List<Maintenance> getOverdue() {
        return dao.findOverdue();
    }

    @GetMapping("/history/{assetId}")
    public List<Maintenance> getHistory(@PathVariable String assetId) {
        return dao.findHistory(assetId);
    }

    @GetMapping("/{id}")
    public Maintenance getById(@PathVariable String id) {
        return dao.findById(id);
    }

    @PostMapping
    public String create(@RequestBody Maintenance m) {
        if (m.getStatus() == null || m.getStatus().isEmpty()) {
            m.setStatus("SCHEDULED");
        }
        dao.insert(m);
        return "Maintenance scheduled";
    }

    @PutMapping
    public String update(@RequestBody Maintenance m) {
        dao.update(m);
        return "Maintenance updated";
    }

    // only a SCHEDULED job can start, and the vehicle / equipment must not be
    // allocated to a request at that time
    @PutMapping("/{id}/start")
    public ResponseEntity<String> start(@PathVariable String id) {
        Maintenance m = dao.findById(id);
        if (m == null) {
            return ResponseEntity.badRequest().body("Maintenance job not found");
        }
        if (!"SCHEDULED".equals(m.getStatus())) {
            return ResponseEntity.badRequest().body("Job must be SCHEDULED to start, now it is " + m.getStatus());
        }
        if ("ALLOCATED".equals(assetStatus(m))) {
            return ResponseEntity.badRequest().body(m.getAssetId() + " is allocated to a request now, try later");
        }
        dao.updateStatus(id, "IN_PROGRESS");
        changeAssetStatus(m, "MAINTENANCE");
        return ResponseEntity.ok("Maintenance started");
    }

    // asset becomes AVAILABLE again only if this job had put it in MAINTENANCE
    @PutMapping("/{id}/complete")
    public ResponseEntity<String> complete(@PathVariable String id, @RequestParam String workDone) {
        Maintenance m = dao.findById(id);
        if (m == null) {
            return ResponseEntity.badRequest().body("Maintenance job not found");
        }
        if ("COMPLETED".equals(m.getStatus()) || "CANCELLED".equals(m.getStatus())) {
            return ResponseEntity.badRequest().body("Job is already " + m.getStatus());
        }
        dao.complete(id, workDone);
        if ("IN_PROGRESS".equals(m.getStatus())) {
            changeAssetStatus(m, "AVAILABLE");
        }
        return ResponseEntity.ok("Maintenance completed");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable String id) {
        Maintenance m = dao.findById(id);
        if (m == null) {
            return ResponseEntity.badRequest().body("Maintenance job not found");
        }
        if ("COMPLETED".equals(m.getStatus()) || "CANCELLED".equals(m.getStatus())) {
            return ResponseEntity.badRequest().body("Job is already " + m.getStatus());
        }
        dao.updateStatus(id, "CANCELLED");
        if ("IN_PROGRESS".equals(m.getStatus())) {
            changeAssetStatus(m, "AVAILABLE");
        }
        return ResponseEntity.ok("Maintenance cancelled");
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        dao.delete(id);
        return "Maintenance deleted";
    }

    private String assetBase(Maintenance m) {
        if ("VEHICLE".equals(m.getAssetType())) {
            return "http://localhost:8083/vehicles";
        }
        return "http://localhost:8082/equipment";
    }

    // status of the vehicle / equipment, null if we cannot read it
    @SuppressWarnings("unchecked")
    private String assetStatus(Maintenance m) {
        try {
            Map<String, Object> asset = rest.getForObject(assetBase(m) + "/{id}", Map.class, m.getAssetId());
            if (asset != null) {
                return (String) asset.get("status");
            }
        } catch (Exception e) {
            System.out.println("Could not read asset status: " + e.getMessage());
        }
        return null;
    }

    // tell vehicle or equipment service the new status
    private void changeAssetStatus(Maintenance m, String status) {
        try {
            rest.put(assetBase(m) + "/{id}/status?status=" + status, null, m.getAssetId());
        } catch (Exception e) {
            System.out.println("Could not update asset status: " + e.getMessage());
        }
    }
}
