package com.vit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.vit.model.Modules;

@Controller
public class DashboardController {

    private RestTemplate restTemplate = new RestTemplate();

    // number of records from a url, -1 if the service is not running
    private int count(String url) {
        try {
            Object[] array = restTemplate.getForObject(url, Object[].class);
            if (array == null) {
                return 0;
            }
            return array.length;
        } catch (Exception e) {
            return -1;
        }
    }

    private String show(int n) {
        if (n < 0) {
            return "offline";
        }
        return String.valueOf(n);
    }

    private int percent(int part, int total) {
        if (part < 0 || total <= 0) {
            return 0;
        }
        return part * 100 / total;
    }

    @GetMapping("/")
    public String home(Model model) {
        int personnelAvailable = count(Modules.PERSONNEL_URL + "/available");
        int personnelTotal = count(Modules.PERSONNEL_URL);
        int vehiclesAvailable = count(Modules.VEHICLE_URL + "/available");
        int vehiclesTotal = count(Modules.VEHICLE_URL);
        int equipmentAvailable = count(Modules.EQUIPMENT_URL + "/available");
        int equipmentTotal = count(Modules.EQUIPMENT_URL);

        model.addAttribute("personnelAvailable", show(personnelAvailable));
        model.addAttribute("personnelTotal", show(personnelTotal));
        model.addAttribute("personnelPercent", percent(personnelAvailable, personnelTotal));

        model.addAttribute("vehiclesAvailable", show(vehiclesAvailable));
        model.addAttribute("vehiclesTotal", show(vehiclesTotal));
        model.addAttribute("vehiclesPercent", percent(vehiclesAvailable, vehiclesTotal));

        model.addAttribute("equipmentAvailable", show(equipmentAvailable));
        model.addAttribute("equipmentTotal", show(equipmentTotal));
        model.addAttribute("equipmentPercent", percent(equipmentAvailable, equipmentTotal));

        int overall = (percent(personnelAvailable, personnelTotal)
                + percent(vehiclesAvailable, vehiclesTotal)
                + percent(equipmentAvailable, equipmentTotal)) / 3;
        model.addAttribute("overallReadiness", overall);

        model.addAttribute("lowStock", show(count(Modules.INVENTORY_URL + "/low-stock")));
        model.addAttribute("pendingRequests", show(count(Modules.REQUEST_URL + "/status/SUBMITTED")));
        model.addAttribute("upcomingMaintenance", show(count(Modules.MAINTENANCE_URL + "/upcoming")));
        model.addAttribute("overdueMaintenance", show(count(Modules.MAINTENANCE_URL + "/overdue")));
        model.addAttribute("totalDeployments", show(count(Modules.DEPLOYMENT_URL)));

        return "home";
    }
}