package com.torres.companionshipapp;

import android.app.Application;

public class User extends Application {

    private String userId;
    private String name;
    private String email;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
