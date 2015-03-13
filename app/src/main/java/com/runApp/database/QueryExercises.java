package com.runApp.database;

/**
 * Created by Rares on 14/12/14.
 */
public interface QueryExercises {

    String[] PROJECTION_SIMPLE = {
            GymDatabase.Tables.EXERCISES + "." + GymDBContract.ExercisesColumns.ID,
            GymDatabase.Tables.EXERCISES + "." + GymDBContract.ExercisesColumns.START_TIME,
            GymDatabase.Tables.EXERCISES + "." + GymDBContract.ExercisesColumns.END_TIME};

    int ID = 0;
    int START_TIME = ID + 1;
    int END_TIME = START_TIME + 1;

}
