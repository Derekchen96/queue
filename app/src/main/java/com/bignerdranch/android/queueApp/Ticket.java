package com.bignerdranch.android.queueApp;

public class Ticket {

    private String Name;
    private String Contact;
    private String Time;
    private String Service;
    private String ServiceP;

    public Ticket() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }

    public String getServiceP() {
        return ServiceP;
    }

    public void setServiceP(String serviceP) {
        ServiceP = serviceP;
    }
}

