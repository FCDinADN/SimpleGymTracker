package com.runApp.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Rares on 10/12/14.
 */
public class GymDBContract {

    public static final String CONTENT_AUTHORITY = "com.runApp.database";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
            + CONTENT_AUTHORITY);

    protected static final String PATH_HEART_RATE = "heart_rate_path";
    protected static final String PATH_LOCATIONS = "locations_table_path";
    protected static final String PATH_EXERCISES = "exercises_path";
    protected static final String PATH_HEART_RATE_WITH_EXERCISE = "heart_rate_with_exercise_path";
    protected static final String PATH_STEPS_AND_CALORIES = "steps_and_calories__path";
//    protected static final String PATH_ROUTINES = "routines_path";
//    protected static final String PATH_WORKOUTS = "workouts_path";

//    public interface ExercisesColumns {
//        String ID = "exercise_id";
//        String NAME = "exercise_name";
//        String TYPE = "exercise_type";
//        String NOTE = "exercise_note";
//    }

//    public interface RoutinesColumns {
//        String ID = "routine_id";
//        String NAME = "routine_name";
//        String TYPE = "routine_type";
//        String EXERCISES_IDS = "routine_exercises_ids";
//    }

//    public interface ExercisesTypes {
//        String WORKOUT = "type_workout";
//        String RUNNING = "type_running";
//    }
//
//    public interface WorkoutsColumns {
//        String DATE = "workout_day";
//        String ROUTINE = "workout_routine";
//        String EXERCISE = "workout_exercise";
//        String SETS = "workout_sets";
//        String REPS = "workout_reps";
//        String WEIGHTS = "workout_weights";
//        String DISTANCE = "workout_distance";
//    }

    public interface HeartRatesColumns {
        String ID = "heart_rate_id";
        String VALUE = "heart_rate_value";
        //        String START_DATE = "heart_rate_start_date";
//        String END_DATE = "heart_rate_end_date";
//        String NUMBER = "heart_rate_number";
        String EXERCISE_ID = "heart_rate_exercise_id";
    }

    public interface LocationsColumns {
        String ID = "location_id";
        String LATITUDE = "location_latitude";
        String LONGITUDE = "lcoaton_longitude";
        String SPEED = "location_speed";
        String NUMBER = "location_number";
        String GOOGLE_RESPONSE = "location_url";
    }

    public interface ExercisesColumns {
        String ID = "exercise_id";
        String START_TIME = "exercise_start_time";
        String END_TIME = "exercise_end_time";
        String DISTANCE = "exercise_distance";
    }

    public interface StepsAndCaloriesColumns {
        String STEPS = "steps";
        String CALORIES = "calories";
        String DATE = "date";
    }

    public static class HeartRates implements HeartRatesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_HEART_RATE).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.runApp.heartrates";
        public static final String CONTENT_URI_EXERCISE_ORDER = GymDatabase.Tables.HEART_RATES
                + "." + HeartRatesColumns.EXERCISE_ID + " DESC";

        public static final Uri CONTENT_URI_WITH_EXERCISE = Uri.withAppendedPath(CONTENT_URI, "with_exercise");
    }

    public static class Locations implements LocationsColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_LOCATIONS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.runApp.lcoations";
        public static final String CONTENT_URI_DATE_ORDER = GymDatabase.Tables.LOCATIONS
                + "." + LocationsColumns.NUMBER + " DESC";
    }

    public static class Exercises implements ExercisesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EXERCISES).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.runApp.exercises";
        public static final String CONTENT_URI_ID_ORDER = GymDatabase.Tables.EXERCISES
                + "." + Exercises.ID + " DESC";
    }

    public static class StepsAndCalories implements StepsAndCaloriesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STEPS_AND_CALORIES).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.runApp.stepsAndCalories";
        public static final String CONTENT_URI_ID_ORDER= GymDatabase.Tables.STEPS_AND_CALORIES
                + "." + BaseColumns._ID + " DESC";
    }

//    public static class Exercises implements ExercisesColumns, BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
//                .appendPath(PATH_EXERCISES).build();
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.runningapp.exercises";
//        public static final String RATES_DAY_SORT = RatesDatabase.Tables.RATES
//                + "." + RateColumns.DAY + " DESC " + " , " + RatesDatabase.Tables.ORDER + "." + OrderColumns.ORDER + " ASC";

//        public static final Uri CONTENT_URI_WITH_ORDER = Uri.withAppendedPath(CONTENT_URI, "with_order");
//    }
//
//    public static class Routines implements RoutinesColumns, BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
//                .appendPath(PATH_ROUTINES).build();
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.simplegymtracker.routines";
////        public static final String ORDER_SORT = RatesDatabase.Tables.ORDER
////                + "." + OrderColumns.ORDER + " ASC";
//    }
//
//    public static class Workouts implements WorkoutsColumns, BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
//                .appendPath(PATH_WORKOUTS).build();
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.simplegymtracker.workouts";
//    }
}
