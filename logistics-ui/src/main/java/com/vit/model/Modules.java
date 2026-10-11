package com.vit.model;

import java.util.LinkedHashMap;
import java.util.Map;

// settings of all 7 modules: urls, table columns and form fields
public class Modules {

    public static final String PERSONNEL_URL = "http://localhost:8081/personnel";
    public static final String EQUIPMENT_URL = "http://localhost:8082/equipment";
    public static final String VEHICLE_URL = "http://localhost:8083/vehicles";
    public static final String INVENTORY_URL = "http://localhost:8084/inventory";
    public static final String REQUEST_URL = "http://localhost:8085/requests";
    public static final String MAINTENANCE_URL = "http://localhost:8086/maintenance";
    public static final String DEPLOYMENT_URL = "http://localhost:8087/deployments";

    private static final Map<String, ModuleInfo> ALL = new LinkedHashMap<>();

    static {
        // Personnel
        ModuleInfo personnel = new ModuleInfo("personnel", "Personnel", PERSONNEL_URL, "personnelId", true, true);
        personnel.addColumn("personnelId", "ID");
        personnel.addColumn("fullName", "Name");
        personnel.addColumn("personnelRole", "Role");
        personnel.addColumn("unitCode", "Unit");
        personnel.addColumn("postingLocation", "Posting");
        personnel.addColumn("availability", "Availability");
        personnel.addField("personnelId", "ID", "text");
        personnel.addField("fullName", "Full Name", "text");
        personnel.addField("personnelRole", "Role", "text");
        personnel.addField("unitCode", "Unit", "text");
        personnel.addField("postingLocation", "Posting Location", "text");
        personnel.addField("availability", "Availability", "select:AVAILABLE,DEPLOYED,ON_LEAVE");
        ALL.put("personnel", personnel);

        // Equipment
        ModuleInfo equipment = new ModuleInfo("equipment", "Equipment", EQUIPMENT_URL, "equipmentId", true, true);
        equipment.addColumn("equipmentId", "ID");
        equipment.addColumn("equipmentName", "Name");
        equipment.addColumn("category", "Category");
        equipment.addColumn("ownerUnit", "Owner Unit");
        equipment.addColumn("location", "Location");
        equipment.addColumn("operationalCondition", "Condition");
        equipment.addColumn("status", "Status");
        equipment.addField("equipmentId", "ID", "text");
        equipment.addField("equipmentName", "Name", "text");
        equipment.addField("category", "Category", "text");
        equipment.addField("ownerUnit", "Owner Unit", "text");
        equipment.addField("location", "Location", "text");
        equipment.addField("operationalCondition", "Condition", "select:GOOD,FAIR,POOR");
        equipment.addField("status", "Status", "select:AVAILABLE,ALLOCATED,MAINTENANCE");
        ALL.put("equipment", equipment);

        // Vehicles
        ModuleInfo vehicles = new ModuleInfo("vehicles", "Vehicles", VEHICLE_URL, "vehicleId", true, true);
        vehicles.addColumn("vehicleId", "ID");
        vehicles.addColumn("vehicleType", "Type");
        vehicles.addColumn("registrationNo", "Registration No");
        vehicles.addColumn("driverName", "Driver");
        vehicles.addColumn("location", "Location");
        vehicles.addColumn("status", "Status");
        vehicles.addField("vehicleId", "ID", "text");
        vehicles.addField("vehicleType", "Type", "text");
        vehicles.addField("registrationNo", "Registration No", "text");
        vehicles.addField("driverName", "Driver", "optional");
        vehicles.addField("location", "Location", "text");
        vehicles.addField("status", "Status", "select:AVAILABLE,ALLOCATED,MAINTENANCE");
        ALL.put("vehicles", vehicles);

        // Inventory
        ModuleInfo inventory = new ModuleInfo("inventory", "Inventory", INVENTORY_URL, "itemId", true, true);
        inventory.addColumn("itemId", "ID");
        inventory.addColumn("itemName", "Item");
        inventory.addColumn("category", "Category");
        inventory.addColumn("quantity", "Quantity");
        inventory.addColumn("reorderLevel", "Reorder Level");
        inventory.addColumn("unit", "Unit");
        inventory.addColumn("location", "Location");
        inventory.addField("itemId", "ID", "text");
        inventory.addField("itemName", "Item", "text");
        inventory.addField("category", "Category", "text");
        inventory.addField("quantity", "Quantity", "number");
        inventory.addField("reorderLevel", "Reorder Level", "number");
        inventory.addField("unit", "Unit", "text");
        inventory.addField("location", "Location", "text");
        inventory.addAction("issue", "Issue stock (type quantity)", "quantity");
        ALL.put("inventory", inventory);

        // Requests (no edit / delete in the service)
        ModuleInfo requests = new ModuleInfo("requests", "Logistics Requests", REQUEST_URL, "requestId", false, false);
        requests.addColumn("requestId", "ID");
        requests.addColumn("requesterUnit", "Unit");
        requests.addColumn("itemRequested", "Vehicle / Equipment");
        requests.addColumn("location", "Location");
        requests.addColumn("personnelCount", "People");
        requests.addColumn("description", "Description");
        requests.addColumn("priority", "Priority");
        requests.addColumn("status", "Status");
        requests.addColumn("assignedTo", "Assigned To");
        requests.addColumn("allocatedVehicle", "Vehicle");
        requests.addColumn("allocatedEquipment", "Equipment");
        requests.addColumn("allocatedPersonnel", "Personnel");
        requests.addColumn("allocatedSupply", "Supplies");
        requests.addColumn("resolutionNotes", "Notes");
        requests.addField("requestId", "ID", "text");
        requests.addField("requesterUnit", "Requesting Unit", "text");
        requests.addField("itemRequested", "Vehicle / Equipment", "select:VEHICLE,EQUIPMENT,BOTH,NONE");
        requests.addField("location", "Location needed", "optional");
        requests.addField("personnelCount", "People needed", "select:0,1,2,3,4,5");
        requests.addField("supplyItemId", "Supply item ID", "optional");
        requests.addField("supplyQuantity", "Supply quantity", "optional");
        requests.addField("description", "Description", "optional");
        requests.addField("priority", "Priority", "select:LOW,MEDIUM,HIGH,URGENT");
        requests.addAction("approve", "Approve", "");
        requests.addAction("reject", "Reject (type reason)", "reason");
        requests.addAction("assign", "Assign (type name)", "assignee");
        requests.addAction("allocate", "Allocate resources", "");
        requests.addAction("start", "Start", "");
        requests.addAction("complete", "Complete (type notes)", "notes");
        requests.addAction("cancel", "Cancel", "");
        ALL.put("requests", requests);

        // Maintenance
        ModuleInfo maintenance = new ModuleInfo("maintenance", "Maintenance", MAINTENANCE_URL, "maintenanceId", true, true);
        maintenance.addColumn("maintenanceId", "ID");
        maintenance.addColumn("assetType", "Asset Type");
        maintenance.addColumn("assetId", "Asset ID");
        maintenance.addColumn("maintenanceType", "Type");
        maintenance.addColumn("priority", "Priority");
        maintenance.addColumn("scheduledDate", "Scheduled");
        maintenance.addColumn("description", "Description");
        maintenance.addColumn("technician", "Technician");
        maintenance.addColumn("status", "Status");
        maintenance.addColumn("completedDate", "Completed");
        maintenance.addColumn("workDone", "Work Done");
        maintenance.addField("maintenanceId", "ID", "text");
        maintenance.addField("assetType", "Asset Type", "select:VEHICLE,EQUIPMENT");
        maintenance.addField("assetId", "Asset ID (e.g. VEH001)", "text");
        maintenance.addField("maintenanceType", "Maintenance Type", "select:PREVENTIVE,CORRECTIVE");
        maintenance.addField("priority", "Priority", "select:LOW,MEDIUM,HIGH,URGENT");
        maintenance.addField("scheduledDate", "Scheduled Date", "date");
        maintenance.addField("description", "Description", "optional");
        maintenance.addField("technician", "Technician", "optional");
        maintenance.addField("status", "Status", "select:SCHEDULED,IN_PROGRESS,COMPLETED,CANCELLED");
        maintenance.addAction("start", "Start", "");
        maintenance.addAction("complete", "Complete (type work done)", "workDone");
        maintenance.addAction("cancel", "Cancel", "");
        ALL.put("maintenance", maintenance);

        // Deployments
        ModuleInfo deployments = new ModuleInfo("deployments", "Deployments", DEPLOYMENT_URL, "deploymentId", true, true);
        deployments.addColumn("deploymentId", "ID");
        deployments.addColumn("unitCode", "Unit");
        deployments.addColumn("destination", "Destination");
        deployments.addColumn("status", "Status");
        deployments.addColumn("deploymentDate", "Date");
        deployments.addField("deploymentId", "ID", "text");
        deployments.addField("unitCode", "Unit Code", "text");
        deployments.addField("destination", "Destination", "text");
        deployments.addField("status", "Status", "select:PENDING,APPROVED,ALLOCATED,COMPLETED,CANCELLED");
        deployments.addField("deploymentDate", "Date", "date");
        ALL.put("deployments", deployments);
    }

    public static ModuleInfo get(String key) {
        return ALL.get(key);
    }
}