package com.runApp.database;

import android.provider.BaseColumns;

/**
 * Created by Rares on 25/04/15.
 */
public interface QueryStepsAndCalories {

    String[] PROJECTION_SIMPLE = {
            GymDatabase.Tables.STEPS_AND_CALORIES + "." + BaseColumns._ID,
            GymDatabase.Tables.STEPS_AND_CALORIES + "." + GymDBContract.StepsAndCaloriesColumns.STEPS,
            GymDatabase.Tables.STEPS_AND_CALORIES + "." + GymDBContract.StepsAndCaloriesColumns.CALORIES,
            GymDatabase.Tables.STEPS_AND_CALORIES + "." + GymDBContract.StepsAndCaloriesColumns.DATE};

    int _ID = 0;
    int STEPS = _ID + 1;
    int CALORIES = STEPS + 1;
    int DATE = CALORIES + 1;

}
