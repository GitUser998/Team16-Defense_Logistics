package com.defence.request.service;

import com.defence.requests.model.LogisticsRequest;
import com.defence.requests.model.RequestItem;
import com.defence.requests.model.StatusHistory;
import com.defence.request.repository.RequestRepository;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class RequestService {

    private final RequestRepository repo;

    public RequestService(RequestRepository repo) {
        this.repo = repo;
    }

    // ---------- create / read ----------

    public LogisticsRequest create(LogisticsRequest r) {
        if (r.items() == null || r.items().isEmpty()) {
            throw bad("A request needs at least one item");
        }
        if (r.requiredFrom() == null || r.requiredTo() == null || r.requiredTo().isBefore(r.requiredFrom())) {
            throw bad("requiredFrom and requiredTo are needed, and requiredTo must not be before requiredFrom");
        }
        for (RequestItem item : r.items()) {
            if (item.quantity() <= 0) {
                throw bad("Item quantity must be greater than 0");
            }
        }
        Long id = repo.save(r);
        repo.addHistory(id, null, "SUBMITTED", r.requestedBy(), "Request submitted");
        return get(id);
    }

    public List<LogisticsRequest> list(String status) {
        return repo.findAll(status);
    }

    public LogisticsRequest get(Long id) {
        LogisticsRequest r = find(id);
        return r.withItems(repo.findItems(id));
    }

    public List<StatusHistory> history(Long id) {
        find(id);
        return repo.findHistory(id);
    }

    public Map<String, Long> summary() {
        return repo.countByStatus();
    }

    // ---------- workflow: each action moves the request one step ----------

    public LogisticsRequest approve(Long id, String actor, String comment) {
        change(id, List.of("SUBMITTED"), "APPROVED", actor, comment);
        repo.setApprover(id, actor);
        return get(id);
    }

    public LogisticsRequest reject(Long id, String actor, String comment) {
        if (comment == null || comment.isBlank()) {
            throw bad("A comment is required to reject a request");
        }
        change(id, List.of("SUBMITTED"), "REJECTED", actor, comment);
        return get(id);
    }

    public LogisticsRequest assign(Long id, String actor, String comment) {
        change(id, List.of("APPROVED"), "ASSIGNED", actor, comment);
        return get(id);
    }

    public LogisticsRequest start(Long id, String actor, String comment) {
        change(id, List.of("ASSIGNED"), "IN_PROGRESS", actor, comment);
        return get(id);
    }

    public LogisticsRequest complete(Long id, String actor, String comment) {
        change(id, List.of("IN_PROGRESS"), "COMPLETED", actor, comment);
        return get(id);
    }

    public LogisticsRequest cancel(Long id, String actor, String comment) {
        change(id, List.of("SUBMITTED", "APPROVED", "ASSIGNED", "IN_PROGRESS"), "CANCELLED", actor, comment);
        return get(id);
    }

    // ---------- helpers ----------

    private void change(Long id, List<String> allowedFrom, String newStatus, String actor, String comment) {
        if (actor == null || actor.isBlank()) {
            throw bad("actor is required");
        }
        LogisticsRequest r = find(id);
        if (!allowedFrom.contains(r.status())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Request is " + r.status() + ", but must be " + allowedFrom + " for this action");
        }
        repo.updateStatus(id, newStatus);
        repo.addHistory(id, r.status(), newStatus, actor, comment);
    }

    private LogisticsRequest find(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request " + id + " not found"));
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
