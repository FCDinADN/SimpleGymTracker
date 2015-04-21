package com.runApp.ui.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.LimitLine;
import com.runApp.R;
import com.runApp.database.GymDBContract;
import com.runApp.database.QueryHeartRates;
import com.runApp.utils.UserUtils;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Class taking care of graph.
 * Created by Rares on 21/02/15.
 */
public class CubicLineChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = CubicLineChartFragment.class.getSimpleName();

    public static final String FROM_HISTORY = "from_history";
    public static final String EXERCISE_NUMBER = "exercise_number";
    public static final String EXERCISE_DATE = "exercise_date";
    public static final String EXERCISE_VALUES = "exercise_values";
    private final int LOADER_HEART_RATES = 2000;

    @InjectView(R.id.chart)
    LineChart mLineChart;
    @InjectView(R.id.linlaHeaderProgress)
    LinearLayout mProgress;

    ArrayList<Entry> chartEntries1 = new ArrayList<>();
    ArrayList<Integer> entryValues = new ArrayList<>();

    private int exerciseNumber;
    private String exerciseDate;
    private boolean fromHistory;
    private int counter;
    private boolean created;
    private float maxValue = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        if (getArguments() != null) {
            exerciseNumber = getArguments().getInt(EXERCISE_NUMBER);
            exerciseDate = getArguments().getString(EXERCISE_DATE);
            fromHistory = getArguments().getBoolean(FROM_HISTORY);
        }
        // if enabled, the chart will always start at zero on the y-axis
        mLineChart.setStartAtZero(false);

        // disable the drawing of values into the chart
        mLineChart.setDrawYValues(false);

        mLineChart.setDrawBorder(true);

        mLineChart.setDrawLegend(false);

        // no description text
        mLineChart.setDescription(exerciseDate.substring(0, 10));
        mLineChart.setUnit(" BMP");

        // enable value highlighting
        mLineChart.setHighlightEnabled(true);

        // enable touch gestures
        mLineChart.setTouchEnabled(true);

        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        mLineChart.animateX(1500);

        mLineChart.setDrawVerticalGrid(false);

        if (fromHistory) {
            getLoaderManager().restartLoader(LOADER_HEART_RATES, null, this);
            getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (getActivity() != null) {
                        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
                            setHasOptionsMenu(true);
                            //TODO set title from below
//                            ((CardioActivity) getActivity()).setToolbarTitle(getString(R.string.history_selection));
                        } else {//if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 2) {
                            setHasOptionsMenu(false);
                        }
                    }
                }
            });
        } else if (!created) {
//            setHasOptionsMenu(false);
            entryValues = getArguments().getIntegerArrayList(EXERCISE_VALUES);
            for (int i = 0; i < entryValues.size(); i++) {
                Entry entry = new Entry(entryValues.get(i), i);
                chartEntries1.add(entry);
            }
            counter = entryValues.size();
            showGraph();
        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        created = false;
//        chartEntries1 = new ArrayList<>();
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        String selection = GymDBContract.HeartRatesColumns.EXERCISE_ID + " = " + exerciseNumber;//+ 10;
        loader = new CursorLoader(Utils.getContext(), GymDBContract.HeartRates.CONTENT_URI, QueryHeartRates.PROJECTION_SIMPLE, selection, null, GymDBContract.HeartRates.CONTENT_URI_EXERCISE_ORDER);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            switch (cursorLoader.getId()) {
                case LOADER_HEART_RATES:
                    counter = 0;
//                    ArrayList<Integer> colors = new ArrayList<>();
                    while (cursor.moveToNext()) {
//                        LogUtils.LOGE("got", cursor.getString(QueryHeartRates.VALUE));
                        float value = Float.parseFloat(cursor.getString(QueryHeartRates.VALUE));

                        if (value > maxValue) {
                            maxValue = value;
                        }
//                        Log.e(TAG, "value " + value);
                        Entry entry = new Entry(value, counter);
//                        if (value > 90.0f) {
//                            colors.add(Color.RED);
//                        } else {
//                            colors.add(Color.GREEN);
//                        }
                        //chartEntries - list of Type Entry that will hold the values
                        chartEntries1.add(entry);
                        counter++;
                    }
//                    //LineDataSet object
//                    mLineDataSet1 = new LineDataSet(chartEntries1, "DataSet 1");
//                    mLineDataSet1.setColors(ColorTemplate.VORDIPLOM_COLORS);
//                    mLineDataSet1.setDrawCubic(true);
//                    mLineDataSet1.setCubicIntensity(0.2f);
//                    mLineDataSet1.setDrawFilled(true);
//                    mLineDataSet1.setDrawCircles(false);
//                    mLineDataSet1.setLineWidth(2f);
//                    mLineDataSet1.setCircleSize(5f);
//                    mLineDataSet1.setHighLightColor(Color.rgb(244, 117, 117));
//                    //array to hold the data sets
//                    lineDataSets = new ArrayList<>();
//                    lineDataSets.add(mLineDataSet1);
//
//                    //create a data object with the datasets
//                    mLineData = new LineData(xVals, lineDataSets);
//
//
//                    // Add data to the chart
//                    mLineChart.setData(mLineData);

                    showGraph();
                    break;
            }
        }
    }

    private void showGraph() {
        setData(chartEntries1);
        mProgress.setVisibility(View.GONE);
        mLineChart.setVisibility(View.VISIBLE);
        mLineChart.invalidate();
        created = true;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addEntry(Float value) {
        if (created) {
//            Log.e(TAG, "new values @ " + counter + " -> " + value);
            Entry barEntry = new BarEntry(value, counter);
            chartEntries1.add(barEntry);
            counter++;
            setData(chartEntries1);
            mProgress.setVisibility(View.GONE);
            mLineChart.setVisibility(View.VISIBLE);
            mLineChart.invalidate();
            if (value > maxValue) {
                maxValue = value;
            }
        }
    }

    private void setData(ArrayList<Entry> entries) {
        LineDataSet mLineDataSet1 = new LineDataSet(entries, "DataSet 1");
        ArrayList<String> xVals = new ArrayList<>();
        for (Entry ignored : entries) {
            xVals.add("");
        }

        mLineDataSet1.setColors(new int[]{Color.parseColor("#D5B855")});
        mLineDataSet1.setDrawCubic(true);
        mLineDataSet1.setCubicIntensity(0.2f);
        mLineDataSet1.setDrawFilled(true);
        mLineDataSet1.setDrawCircles(false);
        mLineDataSet1.setLineWidth(2f);
        mLineDataSet1.setCircleSize(5f);
        mLineDataSet1.setHighLightColor(Color.parseColor("#133860"));

        //array to hold the data sets
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(mLineDataSet1);

        //create a data object with the datasets
        LineData lineData = new LineData(xVals, lineDataSets);

        setLimitLines(lineData);

        // Add data to the chart
        mLineChart.setData(lineData);
    }

    private void setLimitLines(LineData mLineData) {
        //subtract 10 to show lines before values reach the limit
        maxValue -= 10;

        float veryHardLimit = UserUtils.getVeryHardLimit();
        float hardLimit = UserUtils.getHardLimit();
        float moderateLimit = UserUtils.getModerateLimit();
        float lightLimit = UserUtils.getLightLimit();
        LimitLine ll;
        if (maxValue > veryHardLimit) {
            ll = new LimitLine(veryHardLimit);
            ll.setLineColor(Color.RED);
            ll.setLineWidth(2f);
            mLineData.addLimitLine(ll);
        } else if (maxValue > hardLimit) {
            ll = new LimitLine(hardLimit);
            ll.setLineColor(Color.MAGENTA);
            ll.setLineWidth(2f);
            mLineData.addLimitLine(ll);
        } else if (maxValue > moderateLimit) {
            ll = new LimitLine(moderateLimit);
            ll.setLineColor(Color.GREEN);
            ll.setLineWidth(2f);
            mLineData.addLimitLine(ll);
        } else if (maxValue > lightLimit) {
            ll = new LimitLine(lightLimit);
            ll.setLineColor(Color.BLUE);
            ll.setLineWidth(2f);
            mLineData.addLimitLine(ll);
        }
    }

    public void resetValues() {
        created = false;
        chartEntries1 = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.maps_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_maps) {
            PathGoogleMapFragment googleMapFragment = new PathGoogleMapFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(PathGoogleMapFragment.EXERCISE_NUMBER, exerciseNumber);
            googleMapFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, googleMapFragment)
                    .addToBackStack("")
                    .commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();
        }
        return super.onOptionsItemSelected(item);
    }
}
