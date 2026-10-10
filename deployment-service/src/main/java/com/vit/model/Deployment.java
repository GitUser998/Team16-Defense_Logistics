package com.vit.model;

public class Deployment {

    private String deploymentId;
    private String unitCode;
    private String destination;
    private String status;
    private String deploymentDate;

    public Deployment() {
    }

    public Deployment(String deploymentId, String unitCode, String destination,
                      String status, String deploymentDate) {
        this.deploymentId = deploymentId;
        this.unitCode = unitCode;
        this.destination = destination;
        this.status = status;
        this.deploymentDate = deploymentDate;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeploymentDate() {
        return deploymentDate;
    }

    public void setDeploymentDate(String deploymentDate) {
        this.deploymentDate = deploymentDate;
    }
}