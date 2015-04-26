package com.runApp.ui.fragments;

import android.content.DialogInterface;
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
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.LimitLine;
import com.runApp.R;
import com.runApp.database.GymDBContract;
import com.runApp.database.QueryExercises;
import com.runApp.database.QueryHeartRates;
import com.runApp.models.History;
import com.runApp.utils.Constants;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;
import com.runApp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Class taking care of graph.
 * Created by Rares on 21/02/15.
 */
public class CubicLineChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = CubicLineChartFragment.class.getSimpleName();

    public static final String SHOW_MAP = "show_map";
    public static final String EXERCISE_NUMBER = "exercise_number";
    public static final String EXERCISE_DATE = "exercise_date";
    public static final String EXERCISE_VALUES = "exercise_values";
    private final int LOADER_HEART_RATES = 2000;
    private final int LOADER_HEART_RATES_1 = 2001;
    private final int LOADER_INTENSE_EXERCISES = 2002;

    @InjectView(R.id.chart)
    LineChart mLineChart;
    @InjectView(R.id.linlaHeaderProgress)
    LinearLayout mProgress;
    @InjectView(R.id.chart_date_to_compare)
    TextView compareDate;

    ArrayList<Entry> chartEntries1 = new ArrayList<>();
    ArrayList<Integer> entryValues = new ArrayList<>();

    private int exerciseNumber;
    private int exerciseNumber1;
    private String exerciseDate;
    private String comparisonExerciseDate;
    private boolean fromHistory;
    private int counter;
    private boolean created;
    private float maxValue = -1;
    private ArrayList<History> historyList;

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
            LogUtils.LOGE("exercise number ", exerciseNumber + "");
            exerciseDate = getArguments().getString(EXERCISE_DATE);
            fromHistory = getArguments().getBoolean(SHOW_MAP);
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
            getLoaderManager().restartLoader(LOADER_INTENSE_EXERCISES, null, this);
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
            showGraph(null);
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
        Loader<Cursor> loader = null;
        String selection = "";
        switch (id) {
            case LOADER_HEART_RATES:
                selection = GymDBContract.HeartRatesColumns.EXERCISE_ID + " = " + exerciseNumber;//+ 10;
                loader = new CursorLoader(Utils.getContext(), GymDBContract.HeartRates.CONTENT_URI, QueryHeartRates.PROJECTION_SIMPLE, selection, null, GymDBContract.HeartRates.CONTENT_URI_EXERCISE_ORDER);
                break;
            case LOADER_HEART_RATES_1:
                mProgress.setVisibility(View.VISIBLE);
                selection = GymDBContract.HeartRatesColumns.EXERCISE_ID + " = " + exerciseNumber1;//+ 10;
                loader = new CursorLoader(Utils.getContext(), GymDBContract.HeartRates.CONTENT_URI, QueryHeartRates.PROJECTION_SIMPLE, selection, null, GymDBContract.HeartRates.CONTENT_URI_EXERCISE_ORDER);
                break;
            case LOADER_INTENSE_EXERCISES:
                loader = new CursorLoader(Utils.getContext(), GymDBContract.Exercises.CONTENT_URI, QueryExercises.PROJECTION_SIMPLE, null, null, GymDBContract.Exercises.CONTENT_URI_ID_ORDER);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            switch (cursorLoader.getId()) {
                case LOADER_HEART_RATES:
                    counter = 0;
                    chartEntries1 = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        float value = Float.parseFloat(cursor.getString(QueryHeartRates.VALUE));
                        if (value > maxValue) {
                            maxValue = value;
                        }
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
                    showGraph(null);
                    break;
                case LOADER_HEART_RATES_1:
                    ArrayList<Entry> entries2 = new ArrayList<>();
                    int counter2 = 0;
                    while (cursor.moveToNext()) {
                        float value = Float.parseFloat(cursor.getString(QueryHeartRates.VALUE));

                        if (value > maxValue) {
                            maxValue = value;
                        }
                        Entry entry = new Entry(value, counter2);
                        entries2.add(entry);
                        counter2++;
                    }
                    showGraph(entries2);
                    break;
                case LOADER_INTENSE_EXERCISES:
                    historyList = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        History history = new History(cursor.getInt(QueryExercises.ID),
                                cursor.getString(QueryExercises.START_TIME),
                                cursor.getString(QueryExercises.END_TIME));
                        if (history.getId() != exerciseNumber) {
                            historyList.add(history);
                        } else {
                            exerciseDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(history.getStartTimeDate());
                        }
                    }
                    break;
            }
        }
    }

    private void showGraph(ArrayList<Entry> entries2) {
        setData(chartEntries1, entries2);
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
            setData(chartEntries1, null);
            mProgress.setVisibility(View.GONE);
            mLineChart.setVisibility(View.VISIBLE);
            mLineChart.invalidate();
            if (value > maxValue) {
                maxValue = value;
            }
        }
    }

    private void setData(ArrayList<Entry> entries, ArrayList<Entry> entries2) {
        LineDataSet mLineDataSet1 = new LineDataSet(entries, exerciseDate);
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
        mLineChart.setDescription(exerciseDate.substring(0, 10));

        mLineChart.setDrawLegend(false);

        if (entries2 != null) {
            LineDataSet mLineDataSet2 = new LineDataSet(entries2, comparisonExerciseDate);

            if (entries.size() < entries2.size()) {
                xVals = new ArrayList<>();
                for (Entry ignored : entries2) {
                    xVals.add("");
                }
            }

            mLineDataSet2.setColors(new int[]{Color.parseColor("#133860")});
            mLineDataSet2.setDrawCubic(true);
            mLineDataSet2.setCubicIntensity(0.2f);
            mLineDataSet2.setDrawFilled(false);
            mLineDataSet2.setDrawCircles(false);
            mLineDataSet2.setLineWidth(2f);
            mLineDataSet2.setCircleSize(5f);

            mLineDataSet1.setDrawFilled(false);

            mLineChart.setDrawLegend(true);
            mLineChart.setDescription("");

            lineDataSets.add(mLineDataSet2);
        }

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

    private int dialogSelection = 0;

    @OnClick(R.id.charts_compare_to_layout)
    void showDatePicker() {
//        final DatePickerDialogFragment datePickerDialog = new DatePickerDialogFragment();
//        try {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new SimpleDateFormat(Constants.DATE_FORMAT_WITHOUT_HOUR).parse(exerciseDate));
//            datePickerDialog.setCalendar(calendar);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar calendar = new GregorianCalendar();
//                calendar.set(Calendar.YEAR, year);
//                calendar.set(Calendar.MONTH, monthOfYear);
//                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                compareDate.setText(new SimpleDateFormat("dd MMM yyyy").format(calendar.getTime()));
//            }
//        });
//        datePickerDialog.show(getActivity().getSupportFragmentManager(), "date");

        final CharSequence[] items = new CharSequence[historyList.size()];
        int counter = 0;
        for (int i = 0; i < historyList.size(); i++) {
            History history = historyList.get(i);
            LogUtils.LOGE("ID", history.getId() + "");
            items[i] = new SimpleDateFormat(Constants.DATE_FORMAT).format(history.getStartTimeDate());
        }
        items[historyList.size() - 1] = "No comparison";

        DialogHandler.showSimpleSelectionDialog(getActivity(), R.string.chart_compare_date, items, R.string.dialog_ok, dialogSelection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogSelection = which;
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogSelection < historyList.size() - 1) {
                    exerciseNumber1 = historyList.get(dialogSelection).getId();
                    comparisonExerciseDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(historyList.get(dialogSelection).getStartTimeDate());
                    compareDate.setText(historyList.get(dialogSelection).getStartTime());
                    compareDate.setTextColor(getResources().getColor(R.color.actionbar_background));
                    restartLoader(LOADER_HEART_RATES_1);
                } else {
                    restartLoader(LOADER_HEART_RATES);
                    compareDate.setText(getString(R.string.chart_compare_date));
                    compareDate.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
    }

    private void restartLoader(int id) {
        getLoaderManager().restartLoader(id, null, this);
    }
}
