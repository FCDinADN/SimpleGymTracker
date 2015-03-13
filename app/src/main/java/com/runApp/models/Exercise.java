package com.runApp.models;

/**
 * Created by Rares on 28/11/14.
 */
public class Exercise {

    private int id = -1;
    private String name;
    private String type;
    private String note;

    public Exercise(String name, String type, String note) {
        this.name = name;
        this.type = type;
        this.note = note;
    }

    public Exercise(int id, String name, String type, String note) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTypeInt() {
        switch (type) {
            case "Repetitions":
                return 0;
            case "Repetitions - weight":
                return 1;
            case "Distance":
                return 2;
            case "Time":
                return 3;
            default:
                return 0;
        }
    }

}
