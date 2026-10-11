package com.vit.model;

public class Request {

    private String requestId;
    private String requesterUnit;
    private String itemRequested;      // VEHICLE, EQUIPMENT, BOTH or NONE
    private String description;
    private String priority;
    private String requestDate;
    private String status;
    private String assignedTo;
    private String resolutionNotes;
    private String allocatedVehicle;
    private String allocatedEquipment;
    private String location;           // where the resources are needed (optional)
    private String personnelCount;     // how many people are needed
    private String supplyItemId;       // inventory item needed (optional)
    private String supplyQuantity;     // quantity of that item
    private String allocatedPersonnel; // ids of the people allocated, separated by commas
    private String allocatedSupply;    // for example "ITM001 x 50"

    public Request() {}

    public Request(String requestId, String requesterUnit, String itemRequested, String description,
                   String priority, String requestDate, String status, String assignedTo,
                   String resolutionNotes, String allocatedVehicle, String allocatedEquipment,
                   String location, String personnelCount, String supplyItemId, String supplyQuantity,
                   String allocatedPersonnel, String allocatedSupply) {
        this.requestId = requestId;
        this.requesterUnit = requesterUnit;
        this.itemRequested = itemRequested;
        this.description = description;
        this.priority = priority;
        this.requestDate = requestDate;
        this.status = status;
        this.assignedTo = assignedTo;
        this.resolutionNotes = resolutionNotes;
        this.allocatedVehicle = allocatedVehicle;
        this.allocatedEquipment = allocatedEquipment;
        this.location = location;
        this.personnelCount = personnelCount;
        this.supplyItemId = supplyItemId;
        this.supplyQuantity = supplyQuantity;
        this.allocatedPersonnel = allocatedPersonnel;
        this.allocatedSupply = allocatedSupply;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getRequesterUnit() { return requesterUnit; }
    public void setRequesterUnit(String requesterUnit) { this.requesterUnit = requesterUnit; }
    public String getItemRequested() { return itemRequested; }
    public void setItemRequested(String itemRequested) { this.itemRequested = itemRequested; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public String getAllocatedVehicle() { return allocatedVehicle; }
    public void setAllocatedVehicle(String allocatedVehicle) { this.allocatedVehicle = allocatedVehicle; }
    public String getAllocatedEquipment() { return allocatedEquipment; }
    public void setAllocatedEquipment(String allocatedEquipment) { this.allocatedEquipment = allocatedEquipment; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getPersonnelCount() { return personnelCount; }
    public void setPersonnelCount(String personnelCount) { this.personnelCount = personnelCount; }
    public String getSupplyItemId() { return supplyItemId; }
    public void setSupplyItemId(String supplyItemId) { this.supplyItemId = supplyItemId; }
    public String getSupplyQuantity() { return supplyQuantity; }
    public void setSupplyQuantity(String supplyQuantity) { this.supplyQuantity = supplyQuantity; }
    public String getAllocatedPersonnel() { return allocatedPersonnel; }
    public void setAllocatedPersonnel(String allocatedPersonnel) { this.allocatedPersonnel = allocatedPersonnel; }
    public String getAllocatedSupply() { return allocatedSupply; }
    public void setAllocatedSupply(String allocatedSupply) { this.allocatedSupply = allocatedSupply; }
}
