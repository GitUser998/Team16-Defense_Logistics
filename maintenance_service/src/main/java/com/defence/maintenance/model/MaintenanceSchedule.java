package com.defence.maintenance.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MaintenanceSchedule(
        Long id,
        String assetType,          // VEHICLE or EQUIPMENT
        Long assetId,
        String assetLabel,         // e.g. registration number or asset tag
        String categoryOrType,     // optional, e.g. Truck. Used to find the rule
        String maintenanceType,    // PREVENTIVE or CORRECTIVE
        String priority,           // LOW, MEDIUM, HIGH, URGENT
        LocalDate scheduledDate,
        String description,
        String assignedTechnician,
        String status,
        LocalDateTime startedAt,
        LocalDateTime completedAt) {
}