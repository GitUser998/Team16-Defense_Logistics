package com.vit.security;

public class UserInfo {

    private String username;
    private String role;   // ADMIN, OFFICER or UNIT

    public UserInfo(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
}
