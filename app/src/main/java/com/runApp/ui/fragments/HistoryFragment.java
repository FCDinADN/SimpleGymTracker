package com.runApp.ui.fragments;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.runApp.R;
import com.runApp.adapters.HistoryAdapter;
import com.runApp.database.GymDBContract;
import com.runApp.database.GymDatabaseHelper;
import com.runApp.database.QueryExercises;
import com.runApp.models.History;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 12/01/15.
 */
public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, HistoryAdapter.CallBack {

    private final int LOADER_HEART_RATES = 2000;

    @InjectView(R.id.linlaHeaderProgress)
    LinearLayout mProgress;
    @InjectView(R.id.calendarList)
    ExpandableListView calendarList;

    private ArrayList<History> historyList;
    private HistoryAdapter mHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        historyList = new ArrayList<>();

        getLoaderManager().restartLoader(LOADER_HEART_RATES, null, this);
//        Routine mRoutine = new Routine("27/12/20014 - Chest", "Workout", null);
//        historyList.add(mRoutine);
//        mRoutine = new Routine("29/12/2014 - Triceps", "Workout", null);
//        historyList.add(mRoutine);
//        mRoutine = new Routine("03/01/2015 - Legs", "Workout", null);
//        historyList.add(mRoutine);
//        mRoutine = new Routine("05/01/2015 - Cardio", "Running", null);
//        historyList.add(mRoutine);
//
//        mFriendRoutinesAdapter = new HistoryAdapter(this, getActivity(), hListView, historyList);
//        hListView.setAdapter(mFriendRoutinesAdapter);
    }

//    @OnClick(R.id.history_charts)
//    void showCharts() {
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, new HistoryChartFragment())
//                .addToBackStack("")
//                .commit();
//        getActivity().getSupportFragmentManager().executePendingTransactions();
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        loader = new CursorLoader(Utils.getContext(), GymDBContract.Exercises.CONTENT_URI, QueryExercises.PROJECTION_SIMPLE, null, null, GymDBContract.Exercises.CONTENT_URI_ID_ORDER);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            historyList = new ArrayList<>();
            while (cursor.moveToNext()) {
                History history = new History(cursor.getInt(QueryExercises.ID),
                        cursor.getString(QueryExercises.START_TIME),
                        cursor.getString(QueryExercises.END_TIME));
                historyList.add(history);
            }
            mProgress.setVisibility(View.GONE);
            mHistoryAdapter = new HistoryAdapter(getActivity(), historyList, calendarList);
            mHistoryAdapter.setCallBack(this);
            calendarList.setAdapter(mHistoryAdapter);

            calendarList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    CubicLineChartFragment historyChartFragment = new CubicLineChartFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putInt(CubicLineChartFragment.EXERCISE_NUMBER, historyList.get(groupPosition - 1).getId());
                    mBundle.putString(CubicLineChartFragment.EXERCISE_DATE, historyList.get(groupPosition - 1).getStartTime());
                    mBundle.putBoolean(CubicLineChartFragment.FROM_HISTORY, true);
                    historyChartFragment.setArguments(mBundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, historyChartFragment)
                            .addToBackStack("")
                            .commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                    return true;
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void deleteItem(final int position) {
        DialogHandler.showConfirmDialog(getActivity(),
                R.string.dialog_delete_title,
                R.string.dialog_delete_exercise_text,
                R.string.dialog_delete,
                R.string.dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            GymDatabaseHelper.getInst().deleteExercise(historyList.get(position));
                            mHistoryAdapter.removeItem(position + 1);
                        }
                    }
                }
        );
    }
}
