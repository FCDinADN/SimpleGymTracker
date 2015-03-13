package com.runApp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runApp.R;
import com.runApp.adapters.ExerciseAdapter;
import com.runApp.database.GymDBContract;
import com.runApp.database.QueryExercises;
import com.runApp.models.Exercise;
import com.runApp.utils.LogUtils;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 03/01/15.
 */
public class WorkoutFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = WorkoutFragment.class.getSimpleName();

    @InjectView(R.id.workout_routine_exercises_list)
    RecyclerView mListView;

    private View mView;

    public static final String ROUTINE_NAME = "routine_workout_name";
    public static final String ROUTINE_EXERCISES_IDS = "routine_exercises_ids";

    private final int LOADER_EXERCISES = 5000;
    private String routineName;
    private ArrayList<Integer> routineExercisesIds;
    private ArrayList<Exercise> mExercises;
    private ExerciseAdapter mExerciseAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_routine_workout, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mListView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(Utils.getContext());
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setLayoutManager(mLayoutManager);

        routineName = getArguments().getString(ROUTINE_NAME);
        routineExercisesIds = getArguments().getIntegerArrayList(ROUTINE_EXERCISES_IDS);

        getLoaderManager().initLoader(LOADER_EXERCISES, null, this);

//        if (getActivity() != null && getActivity().getActionBar() != null) {
//            getActivity().getActionBar().setTitle(routineName);
//        }

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(routineName);

        mExercises = new ArrayList<>();
        mExerciseAdapter = new ExerciseAdapter(this, mExercises, getActivity(), null, true);
        mListView.setAdapter(mExerciseAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(routineName);
            getActivity().getActionBar().setIcon(R.drawable.app_icon);
        }
        getLoaderManager().restartLoader(LOADER_EXERCISES, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        String selection;
        selection = GymDBContract.Exercises.ID + " IN " + getExercisesIdsForQuery();
        loader = new CursorLoader(Utils.getContext(), GymDBContract.Exercises.CONTENT_URI, QueryExercises.PROJECTION_SIMPLE, selection, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
//            while (cursor.moveToNext()) {
//                LogUtils.LOGE("EXERCISE ", cursor.getString(QueryExercises.NAME));
//                Exercise exercise = new Exercise(cursor.getInt(QueryExercises.ID),
//                        cursor.getString(QueryExercises.NAME),
//                        cursor.getString(QueryExercises.TYPE),
//                        cursor.getString(QueryExercises.NOTE));
//                mExercises.add(exercise);
//            }
//            mExerciseAdapter.setExercises(mExercises);
//            mExerciseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private String getExercisesIdsForQuery() {
        String result = "(";
        for (int i = 0; i < routineExercisesIds.size(); i++) {
            result += routineExercisesIds.get(i) + ",";
        }
        result = result.substring(0, result.length() - 1) + ")";
        LogUtils.LOGE(TAG, "result: " + result);
        return result;
    }
}
