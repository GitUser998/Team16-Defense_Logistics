package com.defence.requests.model;

public record RequestItem(
        Long id,
        String resourceType,      // PERSONNEL, EQUIPMENT, VEHICLE, SUPPLY
        String categoryOrType,    // e.g. Truck
        int quantity,
        Long resourceId,
        String assignmentStatus) {
}
