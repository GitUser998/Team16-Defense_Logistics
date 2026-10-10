package com.defence.maintenance.service;

import com.defence.maintenance.model.CompleteRequest;
import com.defence.maintenance.model.DueAsset;
import com.defence.maintenance.model.MaintenanceSchedule;
import com.defence.maintenance.model.ServiceHistory;
import com.defence.maintenance.repository.MaintenanceRepository;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class MaintenanceService {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository repo;

    public MaintenanceService(MaintenanceRepository repo) {
        this.repo = repo;
    }

    // ---------- create / read ----------

    public MaintenanceSchedule schedule(MaintenanceSchedule s) {
        if (!List.of("VEHICLE", "EQUIPMENT").contains(s.assetType())) {
            throw bad("assetType must be VEHICLE or EQUIPMENT");
        }
        if (s.assetId() == null) {
            throw bad("assetId is required");
        }
        if (s.assetLabel() == null || s.assetLabel().isBlank()) {
            throw bad("assetLabel is required (asset tag or registration number)");
        }
        if (!List.of("PREVENTIVE", "CORRECTIVE").contains(s.maintenanceType())) {
            throw bad("maintenanceType must be PREVENTIVE or CORRECTIVE");
        }
        if (!List.of("LOW", "MEDIUM", "HIGH", "URGENT").contains(s.priority())) {
            throw bad("priority must be LOW, MEDIUM, HIGH or URGENT");
        }
        if (s.scheduledDate() == null || s.scheduledDate().isBefore(LocalDate.now())) {
            throw bad("scheduledDate is required and must not be in the past");
        }
        Long id = repo.save(s);
        // later: tell equipment/vehicle service about the planned maintenance, if needed
        return get(id);
    }

    public List<MaintenanceSchedule> list(String status, String assetType, LocalDate from, LocalDate to) {
        return repo.search(status, assetType, from, to);
    }

    public MaintenanceSchedule get(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maintenance " + id + " not found"));
    }

    public List<MaintenanceSchedule> upcoming(int days) {
        return repo.findUpcoming(days);
    }

    public List<MaintenanceSchedule> overdue() {
        return repo.findOverdue();
    }

    public List<ServiceHistory> history(String assetType, Long assetId) {
        return repo.findHistory(assetType, assetId);
    }

    /** Used by request-service: an empty list means the asset is free in that window. */
    public List<MaintenanceSchedule> conflicts(String assetType, Long assetId, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw bad("'to' must not be before 'from'");
        }
        return repo.findConflicts(assetType, assetId, from, to);
    }

    public Map<String, Long> summary() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (String status : List.of("SCHEDULED", "IN_PROGRESS", "OVERDUE", "COMPLETED", "CANCELLED")) {
            result.put(status, 0L);
        }
        result.putAll(repo.countByStatus());
        return result;
    }

    // ---------- workflow ----------

    public MaintenanceSchedule start(Long id) {
        MaintenanceSchedule s = get(id);
        requireStatus(s, List.of("SCHEDULED", "OVERDUE"));
        repo.markStarted(id);
        // later: call equipment/vehicle service -> PUT /{assetId}/status = UNDER_MAINTENANCE
        return get(id);
    }

    public MaintenanceSchedule complete(Long id, CompleteRequest body) {
        MaintenanceSchedule s = get(id);
        requireStatus(s, List.of("IN_PROGRESS"));
        if (body.performedBy() == null || body.performedBy().isBlank()) {
            throw bad("performedBy is required");
        }
        if (body.workDone() == null || body.workDone().isBlank()) {
            throw bad("workDone is required");
        }

        LocalDate performedOn = body.performedOn() != null ? body.performedOn() : LocalDate.now();

        // next due date: given by the technician, otherwise from the rule (performedOn + interval)
        LocalDate nextDue = body.nextDueDate();
        if (nextDue == null && s.categoryOrType() != null) {
            nextDue = repo.findRuleInterval(s.assetType(), s.categoryOrType())
                    .map(performedOn::plusDays)
                    .orElse(null);
        }

        repo.saveHistory(new ServiceHistory(null, id, s.assetType(), s.assetId(), s.assetLabel(), performedOn,
                body.workDone(), body.partsUsed(), body.cost(), body.performedBy(), nextDue));
        repo.markCompleted(id);
        // later: call equipment/vehicle service -> status AVAILABLE, last_service_date, next_service_due
        return get(id);
    }

    public MaintenanceSchedule cancel(Long id) {
        MaintenanceSchedule s = get(id);
        requireStatus(s, List.of("SCHEDULED", "OVERDUE", "IN_PROGRESS"));
        repo.markCancelled(id);
        // later: if it was IN_PROGRESS, set the asset back to AVAILABLE
        return get(id);
    }

    // ---------- daily job ----------

    /** Creates PREVENTIVE schedules for assets due within 7 days, then marks late items OVERDUE. */
    public void runDailyJob() {
        List<DueAsset> due = repo.findDueForPreventive(LocalDate.now().plusDays(7));
        for (DueAsset d : due) {
            repo.save(new MaintenanceSchedule(null, d.assetType(), d.assetId(), d.assetLabel(),
                    d.categoryOrType(), "PREVENTIVE", "MEDIUM", d.nextDueDate(),
                    "Automatic preventive maintenance", null, null, null, null));
        }
        int overdue = repo.markOverdue();
        log.info("Daily maintenance job: {} preventive schedule(s) created, {} item(s) marked OVERDUE",
                due.size(), overdue);
    }

    // ---------- helpers ----------

    private void requireStatus(MaintenanceSchedule s, List<String> allowed) {
        if (!allowed.contains(s.status())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Maintenance is " + s.status() + ", but must be " + allowed + " for this action");
        }
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
