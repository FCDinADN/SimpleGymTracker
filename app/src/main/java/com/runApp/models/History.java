package com.runApp.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rares on 12/01/15.
 */
public class History {

    private int id;
    private String startTime;
    private String endTime;

    public History(int id, String startTime, String endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public History(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Date getStartTimeDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return formatter.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public Date getEntTimeDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return formatter.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
