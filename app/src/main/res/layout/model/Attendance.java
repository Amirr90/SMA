package com.example.myapplication.model;

public class Attendance {
    String name;
    String status;

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Attendance(String name, String status) {
        this.name = name;
        this.status = status;
    }
}
