package com.runApp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.runApp.utils.LogUtils;

import static com.runApp.database.GymDBContract.*;

/**
 * Created by Rares on 10/12/14.
 */
public class GymDatabase extends SQLiteOpenHelper {

    private static final String TAG = GymDatabase.class.getSimpleName();

    public static final String DB_NAME = "simpple_gym_tracker_db.db";
    public static final int DB_VERSION = 1;

    public GymDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /* HEARTRATES TABLE */
        final String heartRatesTable = new StringBuffer()
                .append("CREATE TABLE IF NOT EXISTS ").append(Tables.HEART_RATES)
                .append(" ( ").append(HeartRatesColumns.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                .append(HeartRatesColumns.VALUE).append(" INTEGER, ")
//                .append(HeartRatesColumns.START_DATE).append(" TEXT, ")
//                .append(HeartRatesColumns.END_DATE).append(" TEXT, ")
                .append(HeartRatesColumns.EXERCISE_ID).append(" INTEGER )")
                .toString();
        LogUtils.LOGE(TAG, "onCreate HEARTRATES TABLE");
        db.execSQL(heartRatesTable);

         /* LOCATIONS TABLE */
        final String locationsTable = new StringBuffer()
                .append("CREATE TABLE IF NOT EXISTS ").append(Tables.LOCATIONS)
                .append(" ( ").append(LocationsColumns.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                .append(LocationsColumns.LATITUDE).append(" REAL, ")
                .append(LocationsColumns.LONGITUDE).append(" REAL, ")
                .append(LocationsColumns.SPEED).append(" REAL, ")
                .append(LocationsColumns.NUMBER).append(" INTEGER, ")
                .append(LocationsColumns.GOOGLE_RESPONSE).append(" TEXT )")
                .toString();
        LogUtils.LOGE(TAG, "onCreate LOCATIONS TABLE");
        db.execSQL(locationsTable);

        /* EXERCISES TABLE */
        final String exercisesTable = new StringBuffer()
                .append("CREATE TABLE IF NOT EXISTS ").append(Tables.EXERCISES)
                .append(" ( ").append(ExercisesColumns.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
                .append(ExercisesColumns.START_TIME).append(" TEXT, ")
                .append(ExercisesColumns.END_TIME).append(" TEXT )")
                .toString();

        LogUtils.LOGE(TAG, "onCreate EXERCISES TABLE");
        db.execSQL(exercisesTable);

        /* EXERCISES TABLE */
//        final String exercisesTable = new StringBuffer()
//                .append("CREATE TABLE IF NOT EXISTS ").append(Tables.EXERCISES)
//                .append(" ( ").append(ExercisesColumns.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
//                .append(ExercisesColumns.NAME).append(" TEXT, ")
//                .append(ExercisesColumns.TYPE).append(" TEXT, ")
//                .append(ExercisesColumns.NOTE).append(" TEXT )")
//                .toString();
//
//        LogUtils.LOGE(TAG, "onCreate EXERCISES TABLE");
//        db.execSQL(exercisesTable);
//
//        /* ROUTINES TABLE */
//        final String routinesTable = new StringBuffer()
//                .append("CREATE TABLE IF NOT EXISTS ").append(Tables.ROUTINES)
//                .append(" ( ").append(RoutinesColumns.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
//                .append(RoutinesColumns.NAME).append(" TEXT, ")
//                .append(RoutinesColumns.TYPE).append(" INTEGER, ")
//                .append(RoutinesColumns.EXERCISES_IDS).append(" TEXT )")
//                .toString();
//        LogUtils.LOGE(TAG, "onCreate ROUTINES TABLE");
//        db.execSQL(routinesTable);
//
//         /* WORKOUTS TABLE */
//        final String workoutsTable = new StringBuffer()
//                .append("CREATE TABLE IF NOT EXISTS ").append(Tables.WORKOUTS)
//                .append(" ( ").append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ")
//                .append(WorkoutsColumns.DATE).append(" TEXT, ")
//                .append(WorkoutsColumns.ROUTINE).append(" TEXT, ")
//                .append(WorkoutsColumns.EXERCISE).append(" TEXT, ")
//                .append(WorkoutsColumns.SETS).append(" INTEGER, ")
//                .append(WorkoutsColumns.REPS).append(" INTEGER, ")
//                .append(WorkoutsColumns.WEIGHTS).append(" REAL )")
//                .toString();
//        LogUtils.LOGE(TAG, "onCreate WORKOUTS TABLE");
//        db.execSQL(workoutsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void deleteDatabase(Context ctx) {
        ctx.deleteDatabase(DB_NAME);
    }

    public interface Tables {
        String EXERCISES = "EXERCISES";
        String ROUTINES = "ROUTINES";
        String WORKOUTS = "WORKOUTS";
        String HEART_RATES = "HEART_RATES";
        String LOCATIONS = "LOCATIONS_TABLE";
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase("/data/data/com.simplegymtracker/data/databases", null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }
}