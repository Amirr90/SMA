package com.example.sma.model;

public class Attendance {
    String name;
    Boolean isPresent;
    String image;


    public Attendance(String name, Boolean isPresent, String image) {
        this.name = name;
        this.isPresent = isPresent;
        this.image = image;
    }

    public String getName() {
        return name;
    }


    public Boolean getPresent() {
        return isPresent;
    }


    public String getImage() {
        return image;
    }
}
