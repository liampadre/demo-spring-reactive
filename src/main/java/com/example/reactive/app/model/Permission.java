package com.example.reactive.app.model;

import java.util.List;

public class Permission {
    private List<String> roles;

    public Permission(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
