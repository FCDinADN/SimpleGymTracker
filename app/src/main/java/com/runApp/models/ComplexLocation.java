package com.runApp.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rares on 21/02/15.
 */
public class ComplexLocation {

    private int id = -1;
    private double latitude;
    private double longitude;
    private float speed;
    private int exerciseNumber;
    private String google_url;

    public ComplexLocation(double latitude, double longitude, float speed, int exerciseNumber, String google_url) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.exerciseNumber = exerciseNumber;
        this.google_url = google_url;
    }

    public ComplexLocation(double latitude, double longitude, float speed, int exerciseNumber) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.exerciseNumber = exerciseNumber;
    }

    public ComplexLocation(double latitude, double longitude, int exerciseNumber) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.exerciseNumber = exerciseNumber;
    }

    public ComplexLocation(int id, double latitude, double longitude, float speed, int exerciseNumber, String google_url) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.exerciseNumber = exerciseNumber;
        this.google_url = google_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getExerciseNumber() {
        return exerciseNumber;
    }

    public void setExerciseNumber(int exerciseNumber) {
        this.exerciseNumber = exerciseNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGoogle_url() {
        return (google_url == null) ? "" : google_url;
    }

    public void setGoogle_url(String google_url) {
        this.google_url = google_url;
    }
}
