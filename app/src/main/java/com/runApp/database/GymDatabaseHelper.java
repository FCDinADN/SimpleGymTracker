package com.runApp.database;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;

import com.runApp.models.ComplexLocation;
import com.runApp.models.History;
import com.runApp.models.HxMMessage;
import com.runApp.utils.Lists;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;
import com.runApp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Rares on 10/12/14.
 */
public class GymDatabaseHelper {

    private static GymDatabaseHelper mHelper;
    private static final String TAG = GymDatabaseHelper.class.getSimpleName();

    private GymDatabaseHelper() {

    }

    public static GymDatabaseHelper getInst() {
        if (mHelper == null) {
            mHelper = new GymDatabaseHelper();
        }
        return mHelper;
    }
//
//    public void insertExercise(Exercise exercise) {
//        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
//        ContentProviderOperation.Builder insertBuilder = ContentProviderOperation.newInsert(GymDBContract.Exercises.CONTENT_URI);
//
//        if (exercise.getId() > -1) {
//            insertBuilder.withValue(GymDBContract.Exercises.ID, exercise.getId());
//        }
//        insertBuilder.withValue(GymDBContract.Exercises.NAME, exercise.getName());
//        insertBuilder.withValue(GymDBContract.Exercises.TYPE, exercise.getType());
//        insertBuilder.withValue(GymDBContract.Exercises.NOTE, exercise.getNote());
//
//        batch.add(insertBuilder.build());
//        LogUtils.LOGE(TAG, "Database insert");
//        ContentResolver resolver = Utils.getContext().getContentResolver();
//        try {
//            resolver.applyBatch(GymDBContract.CONTENT_AUTHORITY, batch);
//        } catch (RemoteException | OperationApplicationException e) {
//            LogUtils.LOGE(TAG, "Database insertExercise", e);
//        }
//    }

//    public void insertRoutine(Routine routine) {
//        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
//        ContentProviderOperation.Builder insertBuilder = ContentProviderOperation.newInsert(GymDBContract.Routines.CONTENT_URI);
//
//        if (routine.getId() > -1) {
//            insertBuilder.withValue(GymDBContract.Routines.ID, routine.getId());
//        }
//        insertBuilder.withValue(GymDBContract.Routines.NAME, routine.getName());
//        insertBuilder.withValue(GymDBContract.Routines.TYPE, routine.getType());
//        String exercisesIds = "";
//
//        Collections.sort(routine.getSelectedExercisesIds());
//        for (int i = 0; i < routine.getSelectedExercisesIds().size(); i++) {
//            exercisesIds += routine.getSelectedExercisesIds().get(i) + ",";
//        }
//        insertBuilder.withValue(GymDBContract.Routines.EXERCISES_IDS, exercisesIds);
//
//        batch.add(insertBuilder.build());
//        ContentResolver resolver = Utils.getContext().getContentResolver();
//        try {
//            resolver.applyBatch(GymDBContract.CONTENT_AUTHORITY, batch);
//            LogUtils.LOGE(TAG, "Database insert Routine");
//        } catch (RemoteException | OperationApplicationException e) {
//            LogUtils.LOGE(TAG, "Database insertRoutine", e);
//        }
//    }

    public void insertHeartRate(HxMMessage hxMMessage) {
        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
        ContentProviderOperation.Builder insertBuilder = ContentProviderOperation.newInsert(GymDBContract.HeartRates.CONTENT_URI);

        insertBuilder.withValue(GymDBContract.HeartRatesColumns.VALUE, hxMMessage.getHeartRate());
//        insertBuilder.withValue(GymDBContract.HeartRatesColumns.START_DATE, UserUtils.getDate());
//        insertBuilder.withValue(GymDBContract.HeartRatesColumns.END_DATE, UserUtils.getDate());
        insertBuilder.withValue(GymDBContract.HeartRatesColumns.EXERCISE_ID, UserUtils.getExerciseNumber());

        batch.add(insertBuilder.build());
        ContentResolver resolver = Utils.getContext().getContentResolver();
        try {
            resolver.applyBatch(GymDBContract.CONTENT_AUTHORITY, batch);
            LogUtils.LOGD(TAG, "Database insert HeartRate");
        } catch (RemoteException | OperationApplicationException e) {
            LogUtils.LOGD(TAG, "Database insert HeartRate", e);
        }
    }

    public void insertLocation(ComplexLocation complexLocation) {
        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
        ContentProviderOperation.Builder insertBuilder = ContentProviderOperation.newInsert(GymDBContract.Locations.CONTENT_URI);

        if (complexLocation.getId() > -1) {
            insertBuilder.withValue(GymDBContract.LocationsColumns.ID, complexLocation.getId());
        }

        insertBuilder.withValue(GymDBContract.LocationsColumns.LATITUDE, complexLocation.getLatitude());
        insertBuilder.withValue(GymDBContract.LocationsColumns.LONGITUDE, complexLocation.getLongitude());
        insertBuilder.withValue(GymDBContract.LocationsColumns.SPEED, complexLocation.getSpeed());
        insertBuilder.withValue(GymDBContract.LocationsColumns.NUMBER, complexLocation.getExerciseNumber());
        insertBuilder.withValue(GymDBContract.LocationsColumns.GOOGLE_RESPONSE, complexLocation.getGoogle_url());

        batch.add(insertBuilder.build());
        ContentResolver resolver = Utils.getContext().getContentResolver();
        try {
            resolver.applyBatch(GymDBContract.CONTENT_AUTHORITY, batch);
            LogUtils.LOGD(TAG, "Database insert Location @ " + complexLocation.getExerciseNumber());
        } catch (RemoteException | OperationApplicationException e) {
            LogUtils.LOGD(TAG, "Database insert Location", e);
        }
    }


    public void insertExercise(History history) {
        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
        ContentProviderOperation.Builder insertBuilder = ContentProviderOperation.newInsert(GymDBContract.Exercises.CONTENT_URI);

        if (history.getId() > -1) {
            insertBuilder.withValue(GymDBContract.Exercises.ID, history.getId());
        }
        insertBuilder.withValue(GymDBContract.Exercises.START_TIME, history.getStartTime());
        insertBuilder.withValue(GymDBContract.Exercises.END_TIME, history.getEndTime());

        batch.add(insertBuilder.build());
        LogUtils.LOGE(TAG, "Database insert");
        ContentResolver resolver = Utils.getContext().getContentResolver();
        try {
            resolver.applyBatch(GymDBContract.CONTENT_AUTHORITY, batch);
        } catch (RemoteException | OperationApplicationException e) {
            LogUtils.LOGE(TAG, "Database insert Exercise", e);
        }
    }

//    public void updateExercise(Exercise exercise) {
//        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
//        ContentProviderOperation.Builder insertBuilder = ContentProviderOperation.newInsert(GymDBContract.Exercises.CONTENT_URI);
//
//        insertBuilder.withValue(GymDBContract.Exercises.NAME, exercise.getName());
//        insertBuilder.withValue(GymDBContract.Exercises.TYPE, exercise.getType());
//        insertBuilder.withValue(GymDBContract.Exercises.NOTE, exercise.getNote());
//
//        batch.add(insertBuilder.build());
//
//        ContentResolver resolver = Utils.getContext().getContentResolver();
//        try {
//            resolver.applyBatch(GymDBContract.CONTENT_AUTHORITY, batch);
//            LogUtils.LOGE(TAG, "exercise inserted!");
//        } catch (RemoteException | OperationApplicationException e) {
//            LogUtils.LOGE(TAG, "Database insertExercise", e);
//        }
//    }
//
//    public void deleteExercise(Exercise exercise) {
//        SQLiteDatabase mSqLiteDatabase = new GymDatabase(Utils.getContext()).getWritableDatabase();
//        mSqLiteDatabase.delete(GymDatabase.Tables.EXERCISES,
//                GymDBContract.ExercisesColumns.ID + " = " + exercise.getId(), null);
//    }
//
//    public void deleteRoutine(Routine routine) {
//        SQLiteDatabase mSqLiteDatabase = new GymDatabase(Utils.getContext()).getWritableDatabase();
//        mSqLiteDatabase.delete(GymDatabase.Tables.ROUTINES,
//                GymDBContract.RoutinesColumns.ID + " = " + routine.getId(), null);
//    }

    public void deleteExercise(History history) {
        SQLiteDatabase mSqLiteDatabase = new GymDatabase(Utils.getContext()).getWritableDatabase();
        mSqLiteDatabase.delete(GymDatabase.Tables.HEART_RATES,
                GymDBContract.HeartRatesColumns.ID + " = " + history.getId(), null);
        mSqLiteDatabase.delete(GymDatabase.Tables.EXERCISES,
                GymDBContract.ExercisesColumns.ID + " = " + history.getId(), null);
    }
}
