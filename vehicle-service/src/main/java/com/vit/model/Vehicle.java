package com.vit.model;

public class Vehicle {

    private String vehicleId;
    private String vehicleType;
    private String registrationNo;
    private String driverName;
    private String location;
    private String status;

    public Vehicle() {
    }

    public Vehicle(String vehicleId, String vehicleType, String registrationNo,
                   String driverName, String location, String status) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.registrationNo = registrationNo;
        this.driverName = driverName;
        this.location = location;
        this.status = status;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}