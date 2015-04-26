package com.runApp.ui.fragments;

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
import com.runApp.adapters.EverydayHistoryAdapter;
import com.runApp.database.GymDBContract;
import com.runApp.database.QueryStepsAndCalories;
import com.runApp.models.EverydayActivity;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 25/04/15.
 */
public class EverydayHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADER_STEPS_AND_CALORIES = 3000;

    @InjectView(R.id.linlaHeaderProgress)
    LinearLayout mProgress;
    @InjectView(R.id.calendarList)
    ExpandableListView calendarList;

    private ArrayList<EverydayActivity> mEverydayActivities;
    private EverydayHistoryAdapter mEverydayHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mEverydayActivities = new ArrayList<>();

        getLoaderManager().restartLoader(LOADER_STEPS_AND_CALORIES, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        loader = new CursorLoader(Utils.getContext(), GymDBContract.StepsAndCalories.CONTENT_URI, QueryStepsAndCalories.PROJECTION_SIMPLE, null, null, GymDBContract.StepsAndCalories.CONTENT_URI_ID_ORDER);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            mEverydayActivities = new ArrayList<>();
            while (cursor.moveToNext()) {
                EverydayActivity mEverydayActivity = new EverydayActivity(cursor.getInt(QueryStepsAndCalories.STEPS),
                        cursor.getFloat(QueryStepsAndCalories.CALORIES),
                        cursor.getString(QueryStepsAndCalories.DATE));
                mEverydayActivities.add(mEverydayActivity);
            }
            mProgress.setVisibility(View.GONE);
            mEverydayHistoryAdapter = new EverydayHistoryAdapter(getActivity(), mEverydayActivities, calendarList);
            calendarList.setAdapter(mEverydayHistoryAdapter);

//            calendarList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//                @Override
//                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                    CubicLineChartFragment historyChartFragment = new CubicLineChartFragment();
//                    Bundle mBundle = new Bundle();
//                    mBundle.putInt(CubicLineChartFragment.EXERCISE_NUMBER, historyList.get(groupPosition - 1).getId());
//                    mBundle.putString(CubicLineChartFragment.EXERCISE_DATE, historyList.get(groupPosition - 1).getStartTime());
//                    mBundle.putBoolean(CubicLineChartFragment.SHOW_MAP, true);
//                    historyChartFragment.setArguments(mBundle);
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .add(R.id.container, historyChartFragment)
//                            .addToBackStack("")
//                            .commit();
//                    getActivity().getSupportFragmentManager().executePendingTransactions();
//                    return true;
//                }
//            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
