package com.defence.maintenance.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceJob {

    private final MaintenanceService service;

    public MaintenanceJob(MaintenanceService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void daily() {
        service.runDailyJob();
    }
}
