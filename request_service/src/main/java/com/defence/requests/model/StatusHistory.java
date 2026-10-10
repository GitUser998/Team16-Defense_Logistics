package com.defence.requests.model;

import java.time.LocalDateTime;

public record StatusHistory(
        Long id,
        String oldStatus,
        String newStatus,
        String changedBy,
        String comment,
        LocalDateTime changedAt) {
}
