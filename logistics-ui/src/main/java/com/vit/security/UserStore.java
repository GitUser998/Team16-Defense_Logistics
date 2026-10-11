package com.vit.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// demo users come from application.properties (app.users)
@Component
public class UserStore {

    private Map<String, String[]> users = new HashMap<>();

    public UserStore(@Value("${app.users}") String config) {
        for (String entry : config.split(",")) {
            String[] parts = entry.trim().split(":");
            if (parts.length == 3) {
                users.put(parts[0].trim(), new String[] { parts[1].trim(), parts[2].trim() });
            }
        }
    }

    // gives the user if password is correct, else null
    public UserInfo login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String[] data = users.get(username.trim());
        if (data == null || !data[0].equals(password)) {
            return null;
        }
        return new UserInfo(username.trim(), data[1]);
    }
}
