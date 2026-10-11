package com.vit.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.vit.dao.RequestDAO;
import com.vit.model.Request;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestDAO dao;

    private RestTemplate rest = new RestTemplate();

    private String vehicleUrl = "http://localhost:8083/vehicles";
    private String equipmentUrl = "http://localhost:8082/equipment";
    private String personnelUrl = "http://localhost:8081/personnel";
    private String inventoryUrl = "http://localhost:8084/inventory";
    private String deploymentUrl = "http://localhost:8087/deployments";

    @GetMapping
    public List<Request> getAll() {
        return dao.findAll();
    }

    @GetMapping("/status/{status}")
    public List<Request> getByStatus(@PathVariable String status) {
        return dao.findByStatus(status.toUpperCase());
    }

    @PostMapping
    public String submitRequest(@RequestBody Request r) {
        dao.insert(r);
        return "Request submitted successfully";
    }

    // status order: SUBMITTED, APPROVED, ASSIGNED (optional), ALLOCATED, IN_PROGRESS, COMPLETED
    // every step checks the current status first

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable String id) {
        Request r = dao.findById(id);
        if (r == null) {
            return bad("Request not found");
        }
        if (!"SUBMITTED".equals(r.getStatus())) {
            return bad("Request must be SUBMITTED to approve, now it is " + r.getStatus());
        }
        dao.updateStatus(id, "APPROVED");
        return ResponseEntity.ok("Request approved");
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable String id, @RequestParam String reason) {
        Request r = dao.findById(id);
        if (r == null) {
            return bad("Request not found");
        }
        String st = r.getStatus();
        if (!"SUBMITTED".equals(st) && !"APPROVED".equals(st) && !"ASSIGNED".equals(st)) {
            return bad("Cannot reject, request is " + st + ". Use cancel after allocation");
        }
        dao.reject(id, reason);
        return ResponseEntity.ok("Request rejected");
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<String> assign(@PathVariable String id, @RequestParam String assignee) {
        Request r = dao.findById(id);
        if (r == null) {
            return bad("Request not found");
        }
        if (!"APPROVED".equals(r.getStatus()) && !"ASSIGNED".equals(r.getStatus())) {
            return bad("Approve the request first, now it is " + r.getStatus());
        }
        dao.assign(id, assignee);
        return ResponseEntity.ok("Request assigned to " + assignee);
    }

    // allocates vehicle, equipment, people and supplies for an approved request
    // first we only check everything, then we reserve, so nothing is half allocated
    @PutMapping("/{id}/allocate")
    public ResponseEntity<String> allocate(@PathVariable String id) {
        Request r = dao.findById(id);
        if (r == null) {
            return bad("Request not found");
        }
        if (!"APPROVED".equals(r.getStatus()) && !"ASSIGNED".equals(r.getStatus())) {
            return bad("Approve the request first");
        }

        String need = r.getItemRequested();
        boolean wantVehicle = "VEHICLE".equals(need) || "BOTH".equals(need);
        boolean wantEquipment = "EQUIPMENT".equals(need) || "BOTH".equals(need);
        if (!wantVehicle && !wantEquipment && !"NONE".equals(need)) {
            return bad("Vehicle / Equipment must be VEHICLE, EQUIPMENT, BOTH or NONE");
        }

        int people = 0;
        if (hasText(r.getPersonnelCount())) {
            try {
                people = Integer.parseInt(r.getPersonnelCount().trim());
            } catch (NumberFormatException e) {
                return bad("People needed must be a number");
            }
        }

        String supplyId = null;
        int supplyQty = 0;
        if (hasText(r.getSupplyItemId())) {
            supplyId = r.getSupplyItemId().trim();
            try {
                supplyQty = Integer.parseInt(r.getSupplyQuantity().trim());
            } catch (Exception e) {
                return bad("Supply quantity must be a number");
            }
            if (supplyQty <= 0) {
                return bad("Supply quantity must be more than 0");
            }
        }

        if (!wantVehicle && !wantEquipment && people <= 0 && supplyId == null) {
            return bad("Nothing to allocate: ask for a vehicle, equipment, people or supplies");
        }

        String where = r.getLocation();
        List<String> notes = new ArrayList<>();
        String vehicleId = null;
        String equipmentId = null;
        List<String> peopleIds = new ArrayList<>();
        boolean reserved = false;

        try {
            // step 1: check availability only, nothing reserved yet
            if (wantVehicle) {
                Map<String, Object> v = choose(getList(vehicleUrl + "/available"), "location", where, false);
                if (v == null) {
                    return bad("No vehicle is available");
                }
                vehicleId = (String) v.get("vehicleId");
                noteLocation(notes, "Vehicle " + vehicleId, (String) v.get("location"), where);
            }
            if (wantEquipment) {
                Map<String, Object> e = choose(getList(equipmentUrl + "/available"), "location", where, true);
                if (e == null) {
                    return bad("No equipment is available");
                }
                equipmentId = (String) e.get("equipmentId");
                noteLocation(notes, "Equipment " + equipmentId, (String) e.get("location"), where);
            }
            if (people > 0) {
                List<Map<String, Object>> chosen = choosePeople(getList(personnelUrl + "/available"), where, people);
                if (chosen.size() < people) {
                    return bad("Only " + chosen.size() + " personnel available, " + people + " needed");
                }
                for (Map<String, Object> p : chosen) {
                    String pid = (String) p.get("personnelId");
                    peopleIds.add(pid);
                    noteLocation(notes, "Person " + pid, (String) p.get("postingLocation"), where);
                }
            }
            if (supplyId != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> item = rest.getForObject(inventoryUrl + "/" + supplyId, Map.class);
                if (item == null) {
                    return bad("Supply item " + supplyId + " not found");
                }
                int stock = ((Number) item.get("quantity")).intValue();
                if (stock < supplyQty) {
                    return bad("Not enough stock of " + supplyId + ": have " + stock + ", need " + supplyQty);
                }
            }

            // step 2: reserve
            reserved = true;
            if (vehicleId != null) {
                rest.put(vehicleUrl + "/{id}/status?status=ALLOCATED", null, vehicleId);
            }
            if (equipmentId != null) {
                rest.put(equipmentUrl + "/{id}/status?status=ALLOCATED", null, equipmentId);
            }
            for (String pid : peopleIds) {
                rest.put(personnelUrl + "/{id}/availability?availability=DEPLOYED", null, pid);
            }

            // create the deployment record
            Map<String, String> dep = new HashMap<>();
            dep.put("deploymentId", "DEP-" + id);
            dep.put("unitCode", r.getRequesterUnit());
            String dest = r.getDescription();
            if (!hasText(dest)) {
                dest = hasText(where) ? where : "Field activity";
            }
            dep.put("destination", dest);
            dep.put("status", "ALLOCATED");
            dep.put("deploymentDate", LocalDate.now().toString());
            rest.postForObject(deploymentUrl, dep, String.class);

            // issue the supplies at the end
            if (supplyId != null) {
                String answer = rest.exchange(inventoryUrl + "/{id}/issue?quantity={q}", HttpMethod.PUT,
                        null, String.class, supplyId, supplyQty).getBody();
                if (answer == null || !answer.startsWith("Issued")) {
                    throw new RuntimeException("Stock could not be issued: " + answer);
                }
            }

            String supplyText = null;
            if (supplyId != null) {
                supplyText = supplyId + " x " + supplyQty;
            }
            String peopleText = null;
            if (!peopleIds.isEmpty()) {
                peopleText = String.join(",", peopleIds);
            }
            String noteText = null;
            if (!notes.isEmpty()) {
                noteText = String.join("; ", notes);
            }
            dao.setAllocation(id, vehicleId, equipmentId, peopleText, supplyText, noteText);
            return ResponseEntity.ok("Allocated");
        } catch (Exception e) {
            if (reserved) {
                undo(id, vehicleId, equipmentId, peopleIds);
            }
            return bad("Allocation failed, is a service down? " + e.getMessage());
        }
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<String> start(@PathVariable String id) {
        Request r = dao.findById(id);
        if (r == null) {
            return bad("Request not found");
        }
        if (!"ALLOCATED".equals(r.getStatus())) {
            return bad("Allocate resources first, now it is " + r.getStatus());
        }
        dao.updateStatus(id, "IN_PROGRESS");
        return ResponseEntity.ok("Request marked in progress");
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<String> complete(@PathVariable String id, @RequestParam String notes) {
        Request r = dao.findById(id);
        if (r == null) {
            return bad("Request not found");
        }
        if (!"ALLOCATED".equals(r.getStatus()) && !"IN_PROGRESS".equals(r.getStatus())) {
            return bad("Request must be ALLOCATED or IN_PROGRESS to complete, now it is " + r.getStatus());
        }
        dao.complete(id, notes);
        release(r, "COMPLETED", false);   // used supplies are not returned
        return ResponseEntity.ok("Request completed");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable String id) {
        Request r = dao.findById(id);
        if (r == null) {
            return bad("Request not found");
        }
        if (isFinished(r.getStatus())) {
            return bad("Request is already " + r.getStatus());
        }
        dao.updateStatus(id, "CANCELLED");
        release(r, "CANCELLED", true);    // supplies go back to stock
        return ResponseEntity.ok("Request cancelled");
    }

    // helper methods

    private ResponseEntity<String> bad(String message) {
        return ResponseEntity.badRequest().body(message);
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private boolean isFinished(String status) {
        return "COMPLETED".equals(status) || "CANCELLED".equals(status) || "REJECTED".equals(status);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getList(String url) {
        return rest.getForObject(url, List.class);
    }

    // take an item from the same location, if none then take the first one
    private Map<String, Object> choose(List<Map<String, Object>> list, String locationKey,
                                       String wanted, boolean skipPoor) {
        if (list == null) {
            return null;
        }
        Map<String, Object> fallback = null;
        for (Map<String, Object> item : list) {
            if (skipPoor && "POOR".equals(item.get("operationalCondition"))) {
                continue;
            }
            String loc = (String) item.get(locationKey);
            if (hasText(wanted) && wanted.equalsIgnoreCase(loc)) {
                return item;
            }
            if (fallback == null) {
                fallback = item;
            }
        }
        return fallback;
    }

    // same location people first, then others
    private List<Map<String, Object>> choosePeople(List<Map<String, Object>> free, String where, int count) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (free == null) {
            return result;
        }
        if (hasText(where)) {
            for (Map<String, Object> p : free) {
                if (result.size() < count && where.equalsIgnoreCase((String) p.get("postingLocation"))) {
                    result.add(p);
                }
            }
        }
        for (Map<String, Object> p : free) {
            if (result.size() < count && !result.contains(p)) {
                result.add(p);
            }
        }
        return result;
    }

    private void noteLocation(List<String> notes, String what, String actual, String wanted) {
        if (hasText(wanted) && !wanted.equalsIgnoreCase(actual)) {
            notes.add(what + " is from " + actual + ", not " + wanted);
        }
    }

    // one status update, if it fails we just print it and go on
    private void setStatus(String url, Object... vars) {
        try {
            rest.put(url, null, vars);
        } catch (Exception e) {
            System.out.println("Could not update " + url + ": " + e.getMessage());
        }
    }

    // free all resources of an allocated request
    private void release(Request r, String deploymentStatus, boolean returnSupplies) {
        boolean wasAllocated = "ALLOCATED".equals(r.getStatus()) || "IN_PROGRESS".equals(r.getStatus());
        if (!wasAllocated) {
            return;
        }
        boolean anything = false;
        if (hasText(r.getAllocatedVehicle())) {
            setStatus(vehicleUrl + "/{id}/status?status=AVAILABLE", r.getAllocatedVehicle());
            anything = true;
        }
        if (hasText(r.getAllocatedEquipment())) {
            setStatus(equipmentUrl + "/{id}/status?status=AVAILABLE", r.getAllocatedEquipment());
            anything = true;
        }
        if (hasText(r.getAllocatedPersonnel())) {
            for (String pid : r.getAllocatedPersonnel().split(",")) {
                setStatus(personnelUrl + "/{id}/availability?availability=AVAILABLE", pid.trim());
            }
            anything = true;
        }
        if (hasText(r.getAllocatedSupply())) {
            if (returnSupplies) {
                try {
                    restock(r.getSupplyItemId().trim(), Integer.parseInt(r.getSupplyQuantity().trim()));
                } catch (Exception e) {
                    System.out.println("Could not return supplies: " + e.getMessage());
                }
            }
            anything = true;
        }
        if (anything) {
            setStatus(deploymentUrl + "/{id}/status?status=" + deploymentStatus, "DEP-" + r.getRequestId());
        }
    }

    // if allocation fails in the middle, free everything
    private void undo(String requestId, String vehicleId, String equipmentId, List<String> peopleIds) {
        if (vehicleId != null) {
            setStatus(vehicleUrl + "/{id}/status?status=AVAILABLE", vehicleId);
        }
        if (equipmentId != null) {
            setStatus(equipmentUrl + "/{id}/status?status=AVAILABLE", equipmentId);
        }
        for (String pid : peopleIds) {
            setStatus(personnelUrl + "/{id}/availability?availability=AVAILABLE", pid);
        }
        setStatus(deploymentUrl + "/{id}/status?status=CANCELLED", "DEP-" + requestId);
    }

    @SuppressWarnings("unchecked")
    private void restock(String itemId, int qty) {
        Map<String, Object> item = rest.getForObject(inventoryUrl + "/" + itemId, Map.class);
        if (item == null) {
            return;
        }
        int stock = ((Number) item.get("quantity")).intValue();
        item.put("quantity", stock + qty);
        rest.put(inventoryUrl, item);
    }
}
