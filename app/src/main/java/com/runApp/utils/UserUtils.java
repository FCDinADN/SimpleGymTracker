package com.runApp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rares on 20/02/15.
 */
public class UserUtils {

    private static final String PREFERENCES = "user_pref";
    private static final String USER_AGE = "user_age";
    private static final String EXERCISE_NUMBER = "exercise_number";
    private static final String DATE = "today_date";
    private static final String DEVICE_BATTERY = "device_battery";
    private static final String VERY_HARD_LIMIT = "very_hard_limit";
    private static final String HARD_LIMIT = "hard_limit";
    private static final String MODERATE_LIMIT = "moderate_limit";
    private static final String LIGHT_LIMIT = "light_limit";
    private static final String ACTUAL_SPEED = "actual_speed";

    public static int getExerciseNumber() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(EXERCISE_NUMBER, 0);
    }

    public static void setExerciseNumber(int exerciseNumber) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(EXERCISE_NUMBER, exerciseNumber).apply();
    }

    public static int getUserAge() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(USER_AGE, 23);
    }

    public static void setUserAge(int userAge) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(USER_AGE, userAge).apply();
        setVeryHardLimit();
        setHardLimit();
        setModerateLimit();
        setLightLimit();
    }

    public static String getDate() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DATE, "");
    }

    private static void setDate(String date) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(DATE, date).apply();
    }

    public static void checkDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        if (!dateString.equals(getDate())) {
            setDate(dateString);
        }
    }

    public static int getDeviceBattery() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(DEVICE_BATTERY, -1);
    }

    public static void setDeviceBattery(int deviceBattery) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(DEVICE_BATTERY, deviceBattery).apply();
    }

    public static float getVeryHardLimit() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(VERY_HARD_LIMIT, 170.0f);
    }

    private static void setVeryHardLimit() {
        float veryHardLimit = (90 * (220 - getUserAge()) / 100.0f);
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(VERY_HARD_LIMIT, veryHardLimit).apply();
    }

    public static float getHardLimit() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(HARD_LIMIT, 152.0f);
    }

    private static void setHardLimit() {
        float veryHardLimit = (80 * (220 - getUserAge()) / 100.0f);
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(HARD_LIMIT, veryHardLimit).apply();
    }

    public static float getModerateLimit() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(MODERATE_LIMIT, 133.0f);
    }

    private static void setModerateLimit() {
        float veryHardLimit = (70 * (220 - getUserAge()) / 100.0f);
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(MODERATE_LIMIT, veryHardLimit).apply();
    }

    public static float getLightLimit() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(LIGHT_LIMIT, 114.0f);
    }

    private static void setLightLimit() {
        float veryHardLimit = (60 * (220 - getUserAge()) / 100.0f);
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(LIGHT_LIMIT, veryHardLimit).apply();
    }

    public static float getActualSpeed() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(ACTUAL_SPEED, 0.0f);
    }

    public static void setActualSpeed(float speed) {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(ACTUAL_SPEED, speed).apply();
    }

}
