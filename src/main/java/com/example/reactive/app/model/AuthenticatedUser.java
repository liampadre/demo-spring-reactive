package com.example.reactive.app.model;

public class AuthenticatedUser {
    private User user;
    private Permission permission;

    public AuthenticatedUser(User user, Permission permission) {
        this.user = user;
        this.permission = permission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
