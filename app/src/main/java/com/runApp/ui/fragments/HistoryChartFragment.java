package com.runApp.ui.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.LimitLine;
import com.runApp.R;
import com.runApp.database.GymDBContract;
import com.runApp.database.QueryExercises;
import com.runApp.database.QueryHeartRates;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 12/01/15.
 */
public class HistoryChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXERCISE_NUMBER = "exercise_number";
    private final int LOADER_HEART_RATES = 2000;

    @InjectView(R.id.chart)
    BarChart mLineChart;

    ArrayList<BarEntry> chartEntries1 = new ArrayList<>();
    ArrayList<Entry> chartEntries2 = new ArrayList<>();

    private int exerciseNumber;
    private int counter;
    private ArrayList<Integer> colors = new ArrayList<>();
    private ArrayList<String> xVals = new ArrayList<>();
    private ArrayList<BarDataSet> lineDataSets;
    private BarDataSet mLineDataSet1;
    private BarData mLineData;
    private boolean created;

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
        }

        mLineChart.setDescription("Average weights lifted.");

//        Entry entry2 = new Entry(200.0f, 0);
//        chartEntries2.add(entry2);
//        entry2 = new Entry(100.0f, 1);
//        chartEntries2.add(entry2);
//
//
//        LineDataSet mLineDataSet1 = new LineDataSet(chartEntries1, "");
//        LineDataSet mLineDataSet2 = new LineDataSet(chartEntries2, "29/12/2014");
//
//        mLineDataSet1.setColors(new int[]{android.R.color.holo_red_dark, android.R.color.holo_orange_light, android.R.color.holo_green_dark, android.R.color.holo_green_dark, android.R.color.holo_green_dark}, getActivity().getApplicationContext());
//
//        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
//        lineDataSets.add(mLineDataSet1);
////        lineDataSets.add(mLineDataSet2);
//
//        ArrayList<String> xVals = new ArrayList<>();
//        xVals.add("27/12/20014");
//        xVals.add("29/12/20014");
//        xVals.add("03/01/2015");
//        xVals.add("06/01/2015");
//        xVals.add("07/01/2015");
//
//        LineData mLineData = new LineData(xVals, lineDataSets);
//        mLineChart.setData(mLineData);
//        mLineChart.animateX(1500);
//        mLineChart.setDrawLegend(false);
//        mLineChart.animateXY(3000, 3000);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        String selection = GymDBContract.HeartRatesColumns.EXERCISE_ID + " = " + exerciseNumber;
        loader = new CursorLoader(Utils.getContext(), GymDBContract.HeartRates.CONTENT_URI_WITH_EXERCISE, QueryHeartRates.PROJECTION_SIMPLE, selection, null, GymDBContract.HeartRates.CONTENT_URI_EXERCISE_ORDER);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            switch (cursorLoader.getId()) {
                case LOADER_HEART_RATES:
                    counter = 0;
                    colors = new ArrayList<>();
                    xVals = new ArrayList<>();
                    while (cursor.moveToNext()) {
//                        LogUtils.LOGE("got", cursor.getString(QueryHeartRates.VALUE));
                        float value = Float.parseFloat(cursor.getString(QueryHeartRates.VALUE));
                        BarEntry entry = new BarEntry(value, counter);
                        if (value > 90.0f) {
                            colors.add(Color.RED);
                        } else {
                            colors.add(Color.GREEN);
                        }
                        xVals.add(cursor.getString(QueryExercises.START_TIME));
                        //chartEntries - list of Type Entry that will hold the values
                        chartEntries1.add(entry);
                        counter++;
                    }
                    //LineDataSet object
                    mLineDataSet1 = new BarDataSet(chartEntries1, "");
                    mLineDataSet1.setColors(colors);
                    //array to hold the data sets
                    lineDataSets = new ArrayList<>();
                    lineDataSets.add(mLineDataSet1);
                    mLineData = new BarData(xVals, lineDataSets);

                    LimitLine ll = new LimitLine(70f);
                    ll.setLineColor(Color.GREEN);
                    ll.setLineWidth(4f);
                    // .. and more styling options
//                    mLineData.addLimitLine(ll);

                    // Add data to the chart
                    mLineChart.setData(mLineData);
                    mLineChart.animateX(1500);
//                    mLineChart.setDrawLegend(false);

                    created = true;
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_HEART_RATES, null, this);
    }

    public void addEntry(Float value) {
        if (created) {
            BarEntry barEntry = new BarEntry(value, counter);
//            mLineDataSet1.addEntry(barEntry);
            mLineData.addEntry(barEntry, 1);
            mLineChart.setData(mLineData);


////            chartEntries1.add(barEntry);
//            counter++;
////            mLineDataSet1.addEntry(barEntry);
//            lineDataSets.add(mLineDataSet1);
////            mLineData = new BarData(xVals, lineDataSets);
//            mLineData.addDataSet();


//            mLineDataSet1.addEntry(barEntry);
//            mLineData.addDataSet(mLineDataSet1);

//            mLineData.addDataSet(mLineDataSet1);

//            mLineData.addEntry(barEntry, counter);

//            chartEntries1.add(barEntry);
//            counter++;
//            if (value > 90.0f) {
//                colors.add(Color.RED);
//            } else {
//                colors.add(Color.GREEN);
//            }
//            BarDataSet mLineDataSet1 = new BarDataSet(chartEntries1, "");
//            mLineDataSet1.setColors(colors);
//            xVals.add("todo");
//            lineDataSets.add(mLineDataSet1);
//            BarData mLineData = new BarData(xVals, lineDataSets);

//            LimitLine ll = new LimitLine(70f);
//            ll.setLineColor(Color.GREEN);
//            ll.setLineWidth(4f);
//            // .. and more styling options
//            mLineData.addLimitLine(ll);
//
//            mLineChart.setData(mLineData);
//            mLineChart.animateX(1500);
//            mLineChart.setDrawLegend(false);
        }
    }
}
