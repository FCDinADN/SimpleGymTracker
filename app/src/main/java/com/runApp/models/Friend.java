package com.runApp.models;

/**
 * Created by Rares on 04/01/15.
 */
public class Friend {

    private int rank;
    private String name;
    private float score;

    public Friend(int rank, String name, float score) {
        this.rank = rank;
        this.name = name;
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
