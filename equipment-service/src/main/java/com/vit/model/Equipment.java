package com.vit.model;

public class Equipment {

    private String equipmentId;
    private String equipmentName;
    private String category;
    private String ownerUnit;
    private String location;
    private String operationalCondition;
    private String status;

    public Equipment() {
    }

    public Equipment(String equipmentId, String equipmentName, String category, String ownerUnit,
                     String location, String operationalCondition, String status) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.category = category;
        this.ownerUnit = ownerUnit;
        this.location = location;
        this.operationalCondition = operationalCondition;
        this.status = status;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwnerUnit() {
        return ownerUnit;
    }

    public void setOwnerUnit(String ownerUnit) {
        this.ownerUnit = ownerUnit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOperationalCondition() {
        return operationalCondition;
    }

    public void setOperationalCondition(String operationalCondition) {
        this.operationalCondition = operationalCondition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}