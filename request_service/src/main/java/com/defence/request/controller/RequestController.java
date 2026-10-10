package com.defence.request.controller;

import com.defence.requests.model.LogisticsRequest;
import com.defence.requests.model.StatusHistory;
import com.defence.request.service.RequestService;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/requests")
public class RequestController {

    /** Body for approve / reject / assign / start / complete / cancel. */
    public record ActionBody(String actor, String comment) { }

    private final RequestService service;

    public RequestController(RequestService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogisticsRequest create(@RequestBody LogisticsRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<LogisticsRequest> list(@RequestParam(required = false) String status) {
        return service.list(status);
    }

    @GetMapping("/summary")
    public Map<String, Long> summary() {
        return service.summary();
    }

    @GetMapping("/{id}")
    public LogisticsRequest get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/{id}/history")
    public List<StatusHistory> history(@PathVariable Long id) {
        return service.history(id);
    }

    @PutMapping("/{id}/approve")
    public LogisticsRequest approve(@PathVariable Long id, @RequestBody ActionBody body) {
        return service.approve(id, body.actor(), body.comment());
    }

    @PutMapping("/{id}/reject")
    public LogisticsRequest reject(@PathVariable Long id, @RequestBody ActionBody body) {
        return service.reject(id, body.actor(), body.comment());
    }

    @PutMapping("/{id}/assign")
    public LogisticsRequest assign(@PathVariable Long id, @RequestBody ActionBody body) {
        return service.assign(id, body.actor(), body.comment());
    }

    @PutMapping("/{id}/start")
    public LogisticsRequest start(@PathVariable Long id, @RequestBody ActionBody body) {
        return service.start(id, body.actor(), body.comment());
    }

    @PutMapping("/{id}/complete")
    public LogisticsRequest complete(@PathVariable Long id, @RequestBody ActionBody body) {
        return service.complete(id, body.actor(), body.comment());
    }

    @PutMapping("/{id}/cancel")
    public LogisticsRequest cancel(@PathVariable Long id, @RequestBody ActionBody body) {
        return service.cancel(id, body.actor(), body.comment());
    }
}
