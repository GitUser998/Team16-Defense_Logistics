package com.vit.model;

public class Maintenance {

    private String maintenanceId;
    private String assetType;
    private String assetId;
    private String maintenanceType;
    private String priority;
    private String scheduledDate;
    private String description;
    private String technician;
    private String status;
    private String completedDate;
    private String workDone;

    public Maintenance() {
    }

    public Maintenance(String maintenanceId, String assetType, String assetId, String maintenanceType,
                       String priority, String scheduledDate, String description, String technician,
                       String status, String completedDate, String workDone) {
        this.maintenanceId = maintenanceId;
        this.assetType = assetType;
        this.assetId = assetId;
        this.maintenanceType = maintenanceType;
        this.priority = priority;
        this.scheduledDate = scheduledDate;
        this.description = description;
        this.technician = technician;
        this.status = status;
        this.completedDate = completedDate;
        this.workDone = workDone;
    }

    public String getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(String maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public String getWorkDone() {
        return workDone;
    }

    public void setWorkDone(String workDone) {
        this.workDone = workDone;
    }
}