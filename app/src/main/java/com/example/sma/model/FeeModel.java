package com.example.sma.model;

import android.content.Intent;

import com.google.firebase.database.annotations.NotNull;

public class FeeModel {

    private Integer monthly_fee;
    private Integer month;
    private Integer year;
    private long timestamp;
    private Integer paid;
    private Integer status;

    public FeeModel(Integer monthly_fee, Integer month, Integer year, long timestamp, Integer paid, Integer status) {
        this.monthly_fee = monthly_fee;
        this.month = month;
        this.year = year;
        this.timestamp = timestamp;
        this.paid = paid;
        this.status = status;
    }

    public Integer getMonthly_fee() {
        return monthly_fee;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Integer getPaid() {
        return paid;
    }

    public Integer getStatus() {
        return status;
    }
}
