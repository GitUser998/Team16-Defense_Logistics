package com.vit.model;

public class Personnel {
    private Long id;
    private String serviceNumber;
    private String fullName;
    private String rank;
    private String unitAssignment;
    private String status;

    public Personnel() {}

    public Personnel(Long id, String serviceNumber, String fullName, String rank, String unitAssignment, String status) {
        this.id = id;
        this.serviceNumber = serviceNumber;
        this.fullName = fullName;
        this.rank = rank;
        this.unitAssignment = unitAssignment;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServiceNumber() { return serviceNumber; }
    public void setServiceNumber(String serviceNumber) { this.serviceNumber = serviceNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public String getUnitAssignment() { return unitAssignment; }
    public void setUnitAssignment(String unitAssignment) { this.unitAssignment = unitAssignment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}