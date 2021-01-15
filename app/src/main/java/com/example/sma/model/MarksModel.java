package com.example.sma.model;

import java.util.HashMap;
import java.util.Map;

public class MarksModel {

    public String subject;
    public String full_marks;
    public String pass_marks;
    public String obtained;
    public String result;
    public long marks;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFull_marks() {
        return full_marks;
    }

    public void setFull_marks(String full_marks) {
        this.full_marks = full_marks;
    }

    public String getPass_marks() {
        return pass_marks;
    }

    public void setPass_marks(String pass_marks) {
        this.pass_marks = pass_marks;
    }

    public String getObtained() {
        return obtained;
    }

    public void setObtained(String obtained) {
        this.obtained = obtained;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getMarks() {
        return marks;
    }

    public void setMarks(long marks) {
        this.marks = marks;
    }
}
