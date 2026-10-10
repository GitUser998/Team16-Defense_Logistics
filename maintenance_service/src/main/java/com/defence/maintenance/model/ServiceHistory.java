package com.defence.maintenance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ServiceHistory(
        Long id,
        Long maintenanceId,
        String assetType,
        Long assetId,
        String assetLabel,
        LocalDate performedOn,
        String workDone,
        String partsUsed,
        BigDecimal cost,
        String performedBy,
        LocalDate nextDueDate) {
}
