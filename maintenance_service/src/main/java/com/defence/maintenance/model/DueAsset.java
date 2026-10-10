package com.defence.maintenance.model;

import java.time.LocalDate;

/** An asset whose last service says it is due again soon. Used by the daily job. */
public record DueAsset(
        String assetType,
        Long assetId,
        String assetLabel,
        String categoryOrType,
        LocalDate nextDueDate) {
}
