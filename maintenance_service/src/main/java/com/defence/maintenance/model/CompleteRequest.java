package com.defence.maintenance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Body of PUT /api/maintenance/{id}/complete */
public record CompleteRequest(
        String performedBy,       // required
        String workDone,          // required
        LocalDate performedOn,    // optional, defaults to today
        String partsUsed,
        BigDecimal cost,
        LocalDate nextDueDate) {  // optional, otherwise worked out from the rule
}