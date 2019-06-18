package com.example.demo.domain;

import java.io.Serializable;
import java.util.Set;

/**
 * @author MrBird
 */
public class User implements Serializable {
    private static final long serialVersionUID = -2731598327208972274L;

    private String username;

    private String password;

    private Set<String> role;

    private Set<String> permission;

    public User(String username, String password, Set<String> role, Set<String> permission) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.permission = permission;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public Set<String> getPermission() {
        return permission;
    }

    public void setPermission(Set<String> permission) {
        this.permission = permission;
    }
}
