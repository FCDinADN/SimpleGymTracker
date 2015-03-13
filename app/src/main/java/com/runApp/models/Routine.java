package com.runApp.models;

import java.util.ArrayList;

/**
 * Created by Rares on 13/12/14.
 */
public class Routine {

    private int id = -1;
    private String name;
    private String type;
    private ArrayList<Integer> selectedExercisesIds;

    public Routine(String name, String type, ArrayList<Integer> exercises) {
        this.name = name;
        this.type = type;
        this.selectedExercisesIds = exercises;
    }

    public Routine(int id, String name, String type, ArrayList<Integer> exercises) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.selectedExercisesIds = exercises;
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

    public void setSelectedExercisesIds(ArrayList<Integer> selectedExercises) {
        this.selectedExercisesIds = selectedExercises;
    }

    public ArrayList<Integer> getSelectedExercisesIds() {
        return selectedExercisesIds;
    }

    public int getTypeInt() {
        switch (type) {
            case "Workout":
                return 0;
            case "Running":
                return 1;
            case "Cycling":
                return 2;
            case "Walking":
                return 3;
            default:
                return 0;
        }
    }

}
