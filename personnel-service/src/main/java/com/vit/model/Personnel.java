package com.vit.model;

public class Personnel {

    private String personnelId;
    private String fullName;
    private String personnelRole;
    private String unitCode;
    private String postingLocation;
    private String availability;

    public Personnel() {
    }

    public Personnel(String personnelId, String fullName, String personnelRole,
                     String unitCode, String postingLocation, String availability) {
        this.personnelId = personnelId;
        this.fullName = fullName;
        this.personnelRole = personnelRole;
        this.unitCode = unitCode;
        this.postingLocation = postingLocation;
        this.availability = availability;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPersonnelRole() {
        return personnelRole;
    }

    public void setPersonnelRole(String personnelRole) {
        this.personnelRole = personnelRole;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getPostingLocation() {
        return postingLocation;
    }

    public void setPostingLocation(String postingLocation) {
        this.postingLocation = postingLocation;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}