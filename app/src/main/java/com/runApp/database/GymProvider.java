package com.runApp.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.runApp.utils.SelectionBuilder;
import com.runApp.utils.Utils;

import static com.runApp.database.GymDBContract.BASE_CONTENT_URI;
import static com.runApp.database.GymDBContract.CONTENT_AUTHORITY;
import static com.runApp.database.GymDBContract.Exercises;
import static com.runApp.database.GymDBContract.ExercisesColumns;
import static com.runApp.database.GymDBContract.HeartRates;
import static com.runApp.database.GymDBContract.HeartRatesColumns;
import static com.runApp.database.GymDBContract.Locations;
import static com.runApp.database.GymDBContract.LocationsColumns;
import static com.runApp.database.GymDBContract.PATH_EXERCISES;
import static com.runApp.database.GymDBContract.PATH_HEART_RATE;
import static com.runApp.database.GymDBContract.PATH_HEART_RATE_WITH_EXERCISE;
import static com.runApp.database.GymDBContract.PATH_LOCATIONS;
import static com.runApp.database.GymDatabase.Tables;

/**
 * Created by Rares on 10/12/14.
 */
public class GymProvider extends ContentProvider {
    private static final String TAG = GymProvider.class.getSimpleName();

    protected static GymDatabase mGymDatabase;
    protected static UriMatcher mUriMatcher = buildUriMatcher();

    private static final int HEARTRATES = 1000;
    private static final int LOCATIONS = 1001;
    private static final int EXERCISES = 1002;
    private static final int HEARTRATES_WITH_EXERCISE = 1003;
//    private static final int ROUTINES = 1004;
//    private static final int WORKOUTS = 1005;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        /* HEARTRATES TABLE */
        matcher.addURI(authority, PATH_HEART_RATE, HEARTRATES);

         /* LOCATIONS TABLE */
        matcher.addURI(authority, PATH_LOCATIONS, LOCATIONS);

        /* EXERCISES TABLE */
        matcher.addURI(authority, PATH_EXERCISES, EXERCISES);

         /* HEARTRATES WITH EXERCISE TABLE TABLE */
        matcher.addURI(authority, PATH_HEART_RATE + "/" + PATH_HEART_RATE_WITH_EXERCISE, HEARTRATES_WITH_EXERCISE);

        /* ROUTINES TABLE */
//        matcher.addURI(authority, PATH_ROUTINES, ROUTINES);

        /* WORKOUTS TABLE */
//        matcher.addURI(authority, PATH_WORKOUTS, WORKOUTS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mGymDatabase = new GymDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mGymDatabase.getReadableDatabase();

        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).query(true, db, projection, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case HEARTRATES:
                return Exercises.CONTENT_TYPE;
            case LOCATIONS:
                return Locations.CONTENT_TYPE;
            case EXERCISES:
                return Exercises.CONTENT_TYPE;
//            case ROUTINES:
//                return Routines.CONTENT_TYPE;
//            case WORKOUTS:
//                return Workouts.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mGymDatabase.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case HEARTRATES:
                insertOrUpdateById(db, uri, Tables.HEART_RATES, values, HeartRatesColumns.ID);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return HeartRates.CONTENT_URI;
            case LOCATIONS:
                insertOrUpdateById(db, uri, Tables.LOCATIONS, values, LocationsColumns.ID);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Locations.CONTENT_URI;
//            case ROUTINES:
//                insertOrUpdateById(db, uri, Tables.ROUTINES, values, RoutinesColumns.ID);
//                getContext().getContentResolver().notifyChange(uri, null, false);
//                return Routines.CONTENT_URI;
            case EXERCISES:
                insertOrUpdateById(db, uri, Tables.EXERCISES, values, ExercisesColumns.ID);
                getContext().getContentResolver().notifyChange(uri, null, false);
                return Exercises.CONTENT_URI;
//            case WORKOUTS:
//                insertOrUpdateById(db, uri, Tables.WORKOUTS, values, WorkoutsColumns.ROUTINE);
//                getContext().getContentResolver().notifyChange(uri, null, false);
//                return Workouts.CONTENT_URI;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private void insertOrUpdateById(SQLiteDatabase db, Uri uri, String table, ContentValues values, String column)
            throws SQLException {
        try {
            db.insertOrThrow(table, null, values);
        } catch (SQLiteConstraintException e) {
            int nrRows = update(uri, values, column + "=?", new String[]{values.getAsString(column)});
            if (nrRows == 0)
                throw e;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uri == BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            getContext().getContentResolver().notifyChange(uri, null, false);
            return 1;
        }
        final SQLiteDatabase db = mGymDatabase.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        // !ScheduleContract.hasCallerIsSyncAdapterParameter(uri);
        getContext().getContentResolver().notifyChange(uri, null, false);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mGymDatabase.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null, false);
        return retVal;
    }

    private void deleteDatabase() {
        // then tear down
        mGymDatabase.close();
        Context context = getContext();
        GymDatabase.deleteDatabase(context);
        mGymDatabase = new GymDatabase(Utils.getContext());
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link android.net.Uri}. This is usually enough to support
     * {@link #insert}, {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case HEARTRATES:
                return builder.table(Tables.HEART_RATES);
            case LOCATIONS:
                return builder.table(Tables.LOCATIONS);
//            case ROUTINES:
//                return builder.table(Tables.ROUTINES);
            case EXERCISES:
                return builder.table(Tables.EXERCISES);
            case HEARTRATES_WITH_EXERCISE:
                return builder.table(Tables.HEARTRATES_WITH_EXERCISE)
                        .mapToTable(HeartRatesColumns.EXERCISE_ID, Tables.HEART_RATES)
                        .mapToTable(ExercisesColumns.ID, Tables.EXERCISES);
//            case WORKOUTS:
//                return builder.table(Tables.WORKOUTS);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
