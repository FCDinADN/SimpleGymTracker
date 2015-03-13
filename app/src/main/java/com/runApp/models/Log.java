package com.runApp.models;

/**
 * Created by Rares on 11/01/15.
 */
public class Log {

    private String Exercise;
    private int logNumber;
    private float reps;
    private float weight;

    public Log(String exercise, int logNumber, float reps, float weight) {
        Exercise = exercise;
        this.logNumber = logNumber;
        this.reps = reps;
        this.weight = weight;
    }

    public String getExercise() {
        return Exercise;
    }

    public void setExercise(String exercise) {
        Exercise = exercise;
    }

    public int getLogNumber() {
        return logNumber;
    }

    public void setLogNumber(int logNumber) {
        this.logNumber = logNumber;
    }

    public float getReps() {
        return reps;
    }

    public void setReps(float reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
