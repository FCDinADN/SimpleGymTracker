package com.runApp.fragments;

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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
 * Created by Rares on 21/02/15.
 */
public class CubicLineChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXERCISE_NUMBER = "exercise_number";
    private final int LOADER_HEART_RATES = 2000;

    @InjectView(R.id.chart)
    LineChart mLineChart;

    ArrayList<Entry> chartEntries1 = new ArrayList<>();

    private int exerciseNumber;
    private int counter;
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
        // if enabled, the chart will always start at zero on the y-axis
        mLineChart.setStartAtZero(false);

        // disable the drawing of values into the chart
        mLineChart.setDrawYValues(false);

        mLineChart.setDrawBorder(true);

        mLineChart.setDrawLegend(false);

        // no description text
        mLineChart.setDescription(UserUtils.getDate());
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        String selection = GymDBContract.HeartRatesColumns.EXERCISE_ID + " = " + exerciseNumber;
        loader = new CursorLoader(Utils.getContext(), GymDBContract.HeartRates.CONTENT_URI, QueryHeartRates.PROJECTION_SIMPLE, selection, null, GymDBContract.HeartRates.CONTENT_URI_EXERCISE_ORDER);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            switch (cursorLoader.getId()) {
                case LOADER_HEART_RATES:
                    counter = 0;
                    ArrayList<Integer> colors = new ArrayList<>();
                    while (cursor.moveToNext()) {
//                        LogUtils.LOGE("got", cursor.getString(QueryHeartRates.VALUE));
                        float value = Float.parseFloat(cursor.getString(QueryHeartRates.VALUE));
                        Entry entry = new Entry(value, counter);
                        if (value > 90.0f) {
                            colors.add(Color.RED);
                        } else {
                            colors.add(Color.GREEN);
                        }
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

                    setData(chartEntries1);

                    mLineChart.invalidate();
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
            Entry barEntry = new BarEntry(value, counter);
            chartEntries1.add(barEntry);
            counter++;
            setData(chartEntries1);
            mLineChart.invalidate();
        }
    }

    private void setData(ArrayList<Entry> entries) {
        LineDataSet mLineDataSet1 = new LineDataSet(entries, "DataSet 1");
        ArrayList<String> xVals = new ArrayList<>();

        for (Entry ignored : entries) {
            xVals.add("");
        }

        mLineDataSet1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        mLineDataSet1.setDrawCubic(true);
        mLineDataSet1.setCubicIntensity(0.2f);
        mLineDataSet1.setDrawFilled(true);
        mLineDataSet1.setDrawCircles(false);
        mLineDataSet1.setLineWidth(2f);
        mLineDataSet1.setCircleSize(5f);
        mLineDataSet1.setHighLightColor(Color.rgb(244, 117, 117));

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
        LimitLine ll = new LimitLine(UserUtils.getVeryHardLimit());
        ll.setLineColor(Color.RED);
        ll.setLineWidth(2f);
        mLineData.addLimitLine(ll);

        ll = new LimitLine(UserUtils.getHardLimit());
        ll.setLineColor(Color.MAGENTA);
        ll.setLineWidth(2f);
        mLineData.addLimitLine(ll);

        ll = new LimitLine(UserUtils.getModerateLimit());
        ll.setLineColor(Color.GREEN);
        ll.setLineWidth(2f);
        mLineData.addLimitLine(ll);

        ll = new LimitLine(UserUtils.getLightLimit());
        ll.setLineColor(Color.BLUE);
        ll.setLineWidth(2f);
        mLineData.addLimitLine(ll);
    }
}
