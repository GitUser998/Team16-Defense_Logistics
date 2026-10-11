package com.vit.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vit.model.ModuleInfo;
import com.vit.model.Modules;
import com.vit.security.UserInfo;

import jakarta.servlet.http.HttpSession;

@Controller
public class ModuleController {

    private RestTemplate restTemplate = new RestTemplate();

    // roles:
    //   ADMIN   - everything including delete
    //   OFFICER - add, edit and actions (approve, allocate...), no delete
    //   UNIT    - only view and submit requests

    private String roleOf(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user instanceof UserInfo) {
            return ((UserInfo) user).getRole();
        }
        return "";
    }

    private boolean canAdd(String role, String key) {
        if (role.equals("UNIT")) {
            return key.equals("requests");
        }
        return role.equals("ADMIN") || role.equals("OFFICER");
    }

    private boolean canEdit(String role) {
        return role.equals("ADMIN") || role.equals("OFFICER");
    }

    private boolean canAct(String role) {
        return role.equals("ADMIN") || role.equals("OFFICER");
    }

    private boolean canDelete(String role) {
        return role.equals("ADMIN");
    }

    private String notAllowed(String key, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "You are not authorised to do this");
        return "redirect:/m/" + key;
    }

    // make the json body from form values
    private Map<String, Object> buildBody(ModuleInfo module, Map<String, String> params) {
        Map<String, Object> body = new LinkedHashMap<>();
        for (String[] f : module.getFields()) {
            String value = params.get(f[0]);
            if (value == null) {
                value = "";
            }
            if (f[2].equals("number")) {
                body.put(f[0], value.isEmpty() ? 0 : Integer.valueOf(value));
            } else {
                body.put(f[0], value);
            }
        }
        return body;
    }

    // list page
    @SuppressWarnings("unchecked")
    @GetMapping("/m/{key}")
    public String list(@PathVariable String key, Model model, HttpSession session) {
        ModuleInfo module = Modules.get(key);
        if (module == null) {
            return "redirect:/";
        }
        String role = roleOf(session);
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            List<Map<String, Object>> result = restTemplate.getForObject(module.getBaseUrl(), List.class);
            if (result != null) {
                rows = result;
            }
        } catch (Exception e) {
            model.addAttribute("error", module.getTitle() + " service is not running (" + module.getBaseUrl() + ")");
        }
        model.addAttribute("module", module);
        model.addAttribute("rows", rows);
        model.addAttribute("showAdd", canAdd(role, key));
        model.addAttribute("showEdit", module.isCanEdit() && canEdit(role));
        model.addAttribute("showDelete", module.isCanDelete() && canDelete(role));
        model.addAttribute("showActions", canAct(role));
        return "list";
    }

    // add
    @PostMapping("/m/{key}/save")
    public String save(@PathVariable String key, @RequestParam Map<String, String> params,
                       RedirectAttributes ra, HttpSession session) {
        ModuleInfo module = Modules.get(key);
        if (module == null) {
            return "redirect:/";
        }
        if (!canAdd(roleOf(session), key)) {
            return notAllowed(key, ra);
        }
        try {
            restTemplate.postForObject(module.getBaseUrl(), buildBody(module, params), String.class);
            ra.addFlashAttribute("message", "Saved");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not save: " + e.getMessage());
        }
        return "redirect:/m/" + key;
    }

    // edit page
    @SuppressWarnings("unchecked")
    @GetMapping("/m/{key}/edit/{id}")
    public String edit(@PathVariable String key, @PathVariable String id, Model model,
                       RedirectAttributes ra, HttpSession session) {
        ModuleInfo module = Modules.get(key);
        if (module == null || !module.isCanEdit()) {
            return "redirect:/";
        }
        if (!canEdit(roleOf(session))) {
            return notAllowed(key, ra);
        }
        try {
            Map<String, Object> row = restTemplate.getForObject(module.getBaseUrl() + "/" + id, Map.class);
            model.addAttribute("module", module);
            model.addAttribute("row", row);
            return "edit";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not open record: " + e.getMessage());
            return "redirect:/m/" + key;
        }
    }

    // update
    @PostMapping("/m/{key}/update")
    public String update(@PathVariable String key, @RequestParam Map<String, String> params,
                         RedirectAttributes ra, HttpSession session) {
        ModuleInfo module = Modules.get(key);
        if (module == null) {
            return "redirect:/";
        }
        if (!canEdit(roleOf(session))) {
            return notAllowed(key, ra);
        }
        try {
            restTemplate.put(module.getBaseUrl(), buildBody(module, params));
            ra.addFlashAttribute("message", "Updated");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not update: " + e.getMessage());
        }
        return "redirect:/m/" + key;
    }

    // delete
    @GetMapping("/m/{key}/delete/{id}")
    public String delete(@PathVariable String key, @PathVariable String id,
                         RedirectAttributes ra, HttpSession session) {
        ModuleInfo module = Modules.get(key);
        if (module == null || !module.isCanDelete()) {
            return "redirect:/";
        }
        if (!canDelete(roleOf(session))) {
            return notAllowed(key, ra);
        }
        try {
            restTemplate.delete(module.getBaseUrl() + "/" + id);
            ra.addFlashAttribute("message", "Deleted " + id);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not delete: " + e.getMessage());
        }
        return "redirect:/m/" + key;
    }

    // row actions (approve, reject, start, complete, issue...)
    @PostMapping("/m/{key}/action/{id}")
    public String action(@PathVariable String key, @PathVariable String id,
                         @RequestParam String action,
                         @RequestParam(required = false) String value,
                         RedirectAttributes ra, HttpSession session) {
        ModuleInfo module = Modules.get(key);
        if (module == null) {
            return "redirect:/";
        }
        if (!canAct(roleOf(session))) {
            return notAllowed(key, ra);
        }
        String paramName = "";
        for (String[] a : module.getActions()) {
            if (a[0].equals(action)) {
                paramName = a[2];
            }
        }
        String url = module.getBaseUrl() + "/" + id + "/" + action;
        try {
            if (paramName.isEmpty()) {
                restTemplate.put(url, null);
            } else {
                if (value == null || value.isBlank()) {
                    ra.addFlashAttribute("error", "Please type the " + paramName + " for " + action);
                    return "redirect:/m/" + key;
                }
                restTemplate.put(url + "?" + paramName + "={v}", null, value);
            }
            ra.addFlashAttribute("message", "Done: " + action + " on " + id);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Action failed: " + e.getMessage());
        }
        return "redirect:/m/" + key;
    }
}
