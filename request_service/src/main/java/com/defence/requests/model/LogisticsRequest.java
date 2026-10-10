package com.defence.requests.model;

import java.time.LocalDateTime;
import java.util.List;

public record LogisticsRequest(
        Long id,
        String requestNo,
        Long requestingUnitId,
        String requestingUnitName,
        String requestType,
        String priority,
        String requestedBy,
        String justification,
        String sourceLocation,
        String destinationLocation,
        LocalDateTime requiredFrom,
        LocalDateTime requiredTo,
        String status,
        String approvedBy,
        LocalDateTime approvedAt,
        List<RequestItem> items) {

    /** Same request, with the items filled in. */
    public LogisticsRequest withItems(List<RequestItem> newItems) {
        return new LogisticsRequest(id, requestNo, requestingUnitId, requestingUnitName, requestType,
                priority, requestedBy, justification, sourceLocation, destinationLocation,
                requiredFrom, requiredTo, status, approvedBy, approvedAt, newItems);
    }
}
