package com.example.sma.model;

public class PersonalDetailModel {

    String title;
    String description;

    public PersonalDetailModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
