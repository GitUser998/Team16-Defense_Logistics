package com.defence.maintenance.controller;

import com.defence.maintenance.model.CompleteRequest;
import com.defence.maintenance.model.MaintenanceSchedule;
import com.defence.maintenance.model.ServiceHistory;
import com.defence.maintenance.service.MaintenanceService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService service;

    public MaintenanceController(MaintenanceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceSchedule schedule(@RequestBody MaintenanceSchedule body) {
        return service.schedule(body);
    }

    /** Filters: ?status=&assetType=&from=2026-11-01&to=2026-11-30 */
    @GetMapping
    public List<MaintenanceSchedule> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.list(status, assetType, from, to);
    }

    @GetMapping("/upcoming")
    public List<MaintenanceSchedule> upcoming(@RequestParam(defaultValue = "7") int days) {
        return service.upcoming(days);
    }

    @GetMapping("/overdue")
    public List<MaintenanceSchedule> overdue() {
        return service.overdue();
    }

    /** Called by request-service. from/to are date-times such as 2026-11-01T08:00:00. */
    @GetMapping("/conflicts")
    public List<MaintenanceSchedule> conflicts(
            @RequestParam String assetType,
            @RequestParam Long assetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return service.conflicts(assetType, assetId, from.toLocalDate(), to.toLocalDate());
    }

    @GetMapping("/summary")
    public Map<String, Long> summary() {
        return service.summary();
    }

    @GetMapping("/history/{assetType}/{assetId}")
    public List<ServiceHistory> history(@PathVariable String assetType, @PathVariable Long assetId) {
        return service.history(assetType, assetId);
    }

    @GetMapping("/{id}")
    public MaintenanceSchedule get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}/start")
    public MaintenanceSchedule start(@PathVariable Long id) {
        return service.start(id);
    }

    @PutMapping("/{id}/complete")
    public MaintenanceSchedule complete(@PathVariable Long id, @RequestBody CompleteRequest body) {
        return service.complete(id, body);
    }

    @PutMapping("/{id}/cancel")
    public MaintenanceSchedule cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
