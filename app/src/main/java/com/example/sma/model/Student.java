package com.example.sma.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    String name;
    boolean isChecked;
    String image;
    String son_of;
    String teacherName;
    Long roll_no;
    String _class;
    String year;
    List<BioData> bioData = new ArrayList<>();

    public boolean isChecked() {
        return isChecked;
    }





    public String getSon_of() {
        if (son_of == null)
            return "";
        else
            return son_of;
    }

    public String getTeacherName() {
        if (teacherName == null)
            return "";
        else
            return teacherName;
    }

    public List<BioData> getBioData() {
        return bioData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student(String name, boolean isChecked, String image) {
        this.name = name;
        this.isChecked = isChecked;
        this.image = image;
    }

    public Long getRoll_no() {
        return roll_no;
    }

    public void setRoll_no(Long roll_no) {
        this.roll_no = roll_no;
    }

    public String getImage() {
        return image;
    }

    public Student() {
    }

    public String getName() {
        if (name == null)
            return "Student Name";
        else
            return name;
    }

    public boolean isPresent() {
        return isChecked;
    }

    public String get_class() {
        return _class;
    }

    public String getYear() {
        return year;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSon_of(String son_of) {
        this.son_of = son_of;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setBioData(List<BioData> bioData) {
        this.bioData = bioData;
    }

    private class BioData {
        private String fatherName;
        String MotherName;
        String localGuardianName;
        String fatherMob;
        String motherMob;
        String localGuardianMob;

        public BioData(String fatherName, String motherName, String localGuardianName, String fatherMob, String motherMob, String localGuardianMob) {
            this.fatherName = fatherName;
            MotherName = motherName;
            this.localGuardianName = localGuardianName;
            this.fatherMob = fatherMob;
            this.motherMob = motherMob;
            this.localGuardianMob = localGuardianMob;
        }

        public BioData() {
        }

        public String getFatherName() {
            return fatherName;
        }

        public String getMotherName() {
            return MotherName;
        }

        public String getLocalGuardianName() {
            return localGuardianName;
        }

        public String getFatherMob() {
            return fatherMob;
        }

        public String getMotherMob() {
            return motherMob;
        }

        public String getLocalGuardianMob() {
            return localGuardianMob;
        }


    }
}
