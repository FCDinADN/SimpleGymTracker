package com.runApp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.runApp.R;
import com.runApp.adapters.RoutineAdapter;
import com.runApp.models.Routine;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 27/11/14.
 */
public class RoutinesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RoutinesFragment.class.getSimpleName();

    @InjectView(R.id.routines_recycler_view)
    RecyclerView mRecyclerView;

    public final static String RESTART_LOADER = "restart_routines_loader";

    private final int LOADER_ROUTINES = 2000;
    private ArrayList<Routine> mRoutines;
    private RecyclerView.LayoutManager mLayoutManager;
    private RoutineAdapter mRoutineAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routines, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);
        getLoaderManager().initLoader(LOADER_ROUTINES, null, this);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(Utils.getContext());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRoutines = new ArrayList<>();
        mRoutineAdapter = new RoutineAdapter(this, mRoutines, getActivity());
        mRecyclerView.setAdapter(mRoutineAdapter);
        LocalBroadcastManager.getInstance(Utils.getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(RESTART_LOADER));
    }

    @OnClick(R.id.routines_add_new)
    void addNewRoutine() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = new RoutineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RoutineFragment.FRAGMENT_TYPE, RoutineFragment.NEW_ROUTINE);
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_routines, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(R.string.routines_selection);
            getActivity().getActionBar().setIcon(R.drawable.app_icon);
        }
        getLoaderManager().restartLoader(LOADER_ROUTINES, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(Utils.getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_add) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = new RoutineFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(RoutineFragment.FRAGMENT_TYPE, RoutineFragment.NEW_ROUTINE);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                    .replace(R.id.container, fragment)
                    .addToBackStack("")
                    .commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Loader<Cursor> loader;
//        loader = new CursorLoader(Utils.getContext(), Routines.CONTENT_URI, QueryRoutines.PROJECTION_SIMPLE, null, null, null);
//        return loader;
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
//            mRoutines = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                Routine routine = new Routine(cursor.getInt(QueryRoutines.ID),
//                        cursor.getString(QueryRoutines.NAME),
//                        cursor.getString(QueryRoutines.TYPE),
//                        getSelectedExercisesIds(cursor.getString(QueryRoutines.EXERCISE)));
//                mRoutines.add(routine);
//            }
//            if (mRoutines.size() > 0) {
//                mRoutineAdapter.setRoutines(mRoutines);
//                mRoutineAdapter.notifyDataSetChanged();
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    private ArrayList<Integer> getSelectedExercisesIds(String selectedExercises) {
        ArrayList<Integer> selectedExercisesArray = new ArrayList<>();
        int commaPosition = selectedExercises.indexOf(',');
        while (commaPosition != -1) {
            selectedExercisesArray.add(Integer.parseInt(selectedExercises.substring(0, commaPosition)));
            selectedExercises = selectedExercises.substring(commaPosition + 1);
            commaPosition = selectedExercises.indexOf(',');
        }
        return selectedExercisesArray;
    }

}