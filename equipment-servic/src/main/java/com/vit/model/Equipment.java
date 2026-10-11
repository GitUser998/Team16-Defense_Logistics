package com.vit.model;

public class Equipment {
    private Long id;
    private String equipmentName;
    private String serialNumber;
    private String category;
    private String assignedUnit;
    private String operationalStatus;

    public Equipment() {}

    public Equipment(Long id, String equipmentName, String serialNumber, String category, String assignedUnit, String operationalStatus) {
        this.id = id;
        this.equipmentName = equipmentName;
        this.serialNumber = serialNumber;
        this.category = category;
        this.assignedUnit = assignedUnit;
        this.operationalStatus = operationalStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAssignedUnit() { return assignedUnit; }
    public void setAssignedUnit(String assignedUnit) { this.assignedUnit = assignedUnit; }

    public String getOperationalStatus() { return operationalStatus; }
    public void setOperationalStatus(String operationalStatus) { this.operationalStatus = operationalStatus; }
}