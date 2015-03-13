package com.runApp.database;

/**
 * Created by Rares on 20/02/15.
 */
public interface QueryHeartRates {


    String[] PROJECTION_SIMPLE = {
            GymDatabase.Tables.HEART_RATES + "." + GymDBContract.HeartRatesColumns.ID,
            GymDatabase.Tables.HEART_RATES + "." + GymDBContract.HeartRatesColumns.VALUE,
//            GymDatabase.Tables.HEART_RATES + "." + GymDBContract.HeartRatesColumns.START_DATE,
//            GymDatabase.Tables.HEART_RATES + "." + GymDBContract.HeartRatesColumns.END_DATE,
            GymDatabase.Tables.HEART_RATES + "." + GymDBContract.HeartRatesColumns.EXERCISE_ID};

    int ID = 0;
    int VALUE = ID + 1;
    //    int START_DATE = VALUE + 1;
//    int END_DATE = START_DATE + 1;
    int EXERCISE_ID = VALUE + 1;

}
