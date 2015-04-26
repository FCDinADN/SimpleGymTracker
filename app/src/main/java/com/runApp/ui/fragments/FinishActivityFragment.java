package com.runApp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.database.GymDatabaseHelper;
import com.runApp.models.EverydayActivity;
import com.runApp.models.History;
import com.runApp.utils.UserUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 22/04/15.
 */
public class FinishActivityFragment extends Fragment {

    public static final String DATE = "finished_activity_date";
    public static final String START_TIME = "finished_activity_start_time";
    public static final String END_TIME = "finished_activity_end_time";
    public static final String CALORIES = "finished_activity_calories";
    public static final String DISTANCE = "finished_activity_distance";
    public static final String DURATION = "finished_activity_duration";
    public static final String AVERAGE_SPEED = "finished_activity_average_speed";
    public static final String MAXIMUM_SPEED = "finished_activity_maximum_speed";
    public static final String AVERAGE_HR = "finished_activity_average_ht";
    public static final String HEART_RATE_VALUES = "finished_activity_ht_values";

    @InjectView(R.id.finished_activity_date)
    TextView dateView;
    @InjectView(R.id.finished_activity_hour)
    TextView startTimeView;
    @InjectView(R.id.caloriesValue)
    TextView calories;
    @InjectView(R.id.distance_value)
    TextView distanceView;
    @InjectView(R.id.duration_value)
    TextView duration;
    @InjectView(R.id.speed_value)
    TextView averageSpeed;
    @InjectView(R.id.maximum_speed_value)
    TextView maxSpeed;
    @InjectView(R.id.heartRate_value)
    TextView heartRate;

    private String day;
    private ArrayList<Integer> heartRatesValues = new ArrayList<>();
    private float distance;
    private Date startTime;
    private Date endTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.finished_activity_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        Bundle bundle = getArguments();
        String startTime = "";
        String endTime = "";
        if (bundle != null) {
            day = bundle.getString(DATE);
            dateView.setText(day);
            startTime = bundle.getString(START_TIME);
            endTime = bundle.getString(END_TIME);
            startTimeView.setText(startTime.substring(0,5));
            calories.setText(bundle.getFloat(CALORIES) + "");
            distance = bundle.getFloat(DISTANCE);
            distanceView.setText(distance + " m");
            duration.setText(bundle.getString(DURATION));
            averageSpeed.setText(bundle.getFloat(AVERAGE_SPEED) + " m/s");
            maxSpeed.setText(bundle.getFloat(MAXIMUM_SPEED) + " m/s");
            heartRate.setText(bundle.getInt(AVERAGE_HR) + " bmp");
            heartRatesValues = bundle.getIntegerArrayList(HEART_RATE_VALUES);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            this.startTime = sdf.parse(day + " " + startTime);
            this.endTime = sdf.parse(day + " " + endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.intense_activity_show_graph)
    void showGraph() {
        CubicLineChartFragment historyChartFragment = new CubicLineChartFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt(CubicLineChartFragment.EXERCISE_NUMBER, UserUtils.getExerciseNumber());
        mBundle.putString(CubicLineChartFragment.EXERCISE_DATE, UserUtils.getTodayDateString());
        mBundle.putIntegerArrayList(CubicLineChartFragment.EXERCISE_VALUES, heartRatesValues);
        historyChartFragment.setArguments(mBundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.cardio_container, historyChartFragment)
                .addToBackStack("")
                .commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    @OnClick(R.id.finish_intense_activity_save)
    void saveActivity() {
//        TODO remove inserting everydayactivity from here!!!
        GymDatabaseHelper.getInst().insertEverydayActivity(new EverydayActivity(UserUtils.getStepsNumber(), UserUtils.getBurntCalories(), UserUtils.getTodayDateString()));
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        GymDatabaseHelper.getInst().insertExercise(new History(UserUtils.getExerciseNumber(), df.format(startTime.getTime()), df.format(endTime.getTime()), distance));
        getActivity().finish();
    }
}
