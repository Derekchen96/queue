package com.bignerdranch.android.queueApp;

public class User {
    public String name, email, phone, userType;

    public User(){

    }

    public User(String name, String email, String phone, String userType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
    }
}