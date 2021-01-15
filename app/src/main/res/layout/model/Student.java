package com.example.myapplication.model;

public class Student {

    String name;
    boolean isChecked;

    public Student(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public boolean isPresent() {
        return isChecked;
    }
}
