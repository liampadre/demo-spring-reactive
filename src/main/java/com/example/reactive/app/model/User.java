package com.example.reactive.app.model;

public class User {

    private String name;

    private String lastname;

    private String pass;

    public User(String name, String lastname, String pass) {
        this.name = name;
        this.lastname = lastname;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
