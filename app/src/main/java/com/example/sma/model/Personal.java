package com.example.sma.model;

public class Personal {
    String date_of_birth;
    String religion;
    String phone_number;
    String email;
    String present_address;
    String permanent_address;
    String father_name;
    String mother_name;
    String blood_group;


    public Personal(String date_of_birth, String religion, String phone_number, String email, String present_address, String permanent_address, String father_name, String mother_name, String blood_group) {
        this.date_of_birth = date_of_birth;
        this.religion = religion;
        this.phone_number = phone_number;
        this.email = email;
        this.present_address = present_address;
        this.permanent_address = permanent_address;
        this.father_name = father_name;
        this.mother_name = mother_name;
        this.blood_group = blood_group;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPresent_address() {
        return present_address;
    }

    public void setPresent_address(String present_address) {
        this.present_address = present_address;
    }

    public String getPermanent_address() {
        return permanent_address;
    }

    public void setPermanent_address(String permanent_address) {
        this.permanent_address = permanent_address;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getMother_name() {
        return mother_name;
    }

    public void setMother_name(String mother_name) {
        this.mother_name = mother_name;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }
}
