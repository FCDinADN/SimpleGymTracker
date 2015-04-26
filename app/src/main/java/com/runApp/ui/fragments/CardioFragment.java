package com.runApp.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runApp.R;
import com.runApp.database.GymDatabaseHelper;
import com.runApp.models.HxMMessage;
import com.runApp.services.CaloriesService;
import com.runApp.ui.activities.CardioActivity;
import com.runApp.ui.activities.FinishIntenseActivity;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.HxMConnection;
import com.runApp.utils.HxMListener;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;
import com.runApp.utils.Utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 27/11/14.
 */
public class CardioFragment extends Fragment implements HxMListener, SensorEventListener {
    private final static String TAG = CardioFragment.class.getSimpleName();

    private final static int TIME_TO_UPDATE_CALORIES_COUNTER = 5;//seconds
    private final static float TREADMILL_FACTOR = 0.84f;//@typical running at 2.5 m/s
    private int secondsCounter = 0;
    private float previousAltitude;
    private float distanceToBeSubtracted;
    private float actualCalories;

    @InjectView(R.id.caloriesValue)
    TextView caloriesValue;
    @InjectView(R.id.heartRate_value)
    TextView heartRateValue;
    @InjectView(R.id.distance_value)
    TextView distanceValue;
    @InjectView(R.id.speed_value)
    TextView speedValue;
    @InjectView(R.id.finish_intense_activity)
    TextView connect;
    @InjectView(R.id.linlaHeaderProgress)
    LinearLayout mProgress;
    @InjectView(R.id.intense_activity_status)
    TextView status;

    private boolean isShort;

    private boolean pressed;
    private HxMConnection mHxMConnection;
    // value user when starting app and it was started before
    private short initialValue = -1;
    //the very previous value sent from server
    private short previousValue = -1;
    //    private float maxSpeed = -1;
    private float valueTravelledBefore = 0;
    private float intermediateValue = 0;
    private boolean showBattery = false;
    private boolean dataInitialised = false;
    //    private boolean mapShown;
    private CubicLineChartFragment historyChartFragment;
    private int seconds = 0;
    private int minutes = 0;
    private Timer timer;
    private TimerTask timerTask;

    private SensorManager mSensorManager;
    private Sensor sensor;
    float pressure;

    private ImageView showMapImage;


    //values used for chart
    private ArrayList<Integer> entryValues;

    private Date startTime;

    private Handler mHandler;

    private float averageSpeed;
    private int averageHeartRate;
    private float maxSpeed;
    private int messagesNr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_intense_activity_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
//        setHasOptionsMenu(true);

        historyChartFragment = new CubicLineChartFragment();

        mHxMConnection = new HxMConnection(this);
        mHxMConnection.setHxMListener(this);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

//        TODO connect to HxM
        tryToConnectToHxM();

//        isShort = getArguments().getBoolean(CardioActivity.SHORT_ACTIVITY);

//        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                if (getActivity() != null) {
//                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
//                        if (mapShown) {
//                            setHasOptionsMenu(false);
////                            ((CardioActivity) getActivity()).setToolbarTitle(getString(R.string.map_selection));
//                        } else {
//                            setHasOptionsMenu(true);
////                            ((CardioActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection));
//                        }
//                    } else if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 2) {
//                        mapShown = false;
//                        setHasOptionsMenu(false);
//                    } else {
//                        mapShown = false;
//                        setHasOptionsMenu(true);
////                        ((CardioActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection));
//                    }
//                }
//            }
//        });

        showMapImage = ((ImageView) getActivity().findViewById(R.id.cardio_show_map));
        showMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable()) {
//                    mapShown = true;
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.cardio_container, new PathGoogleMapFragment())
                            .addToBackStack("")
                            .commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                } else {
                    Toast.makeText(Utils.getContext(), "network unavailable!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_PRESSURE);
        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @OnClick(R.id.finish_intense_activity)
    void finishActivity() {
//        if (!pressed) {
//            tryToConnectToHxM();
//        } else {
//        }
        closeConnection(true);
        Intent intent = new Intent(getActivity(), FinishIntenseActivity.class);
        DateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dfHour = new SimpleDateFormat("HH:mm:ss");
        intent.putExtra(FinishActivityFragment.DATE, dfDate.format(startTime.getTime()));
        intent.putExtra(FinishActivityFragment.START_TIME, dfHour.format(startTime.getTime()));
        intent.putExtra(FinishActivityFragment.END_TIME, dfHour.format(new Date().getTime()));
        intent.putExtra(FinishActivityFragment.CALORIES, Math.round(actualCalories * 100.0f) / 100.0f);
        intent.putExtra(FinishActivityFragment.DISTANCE, intermediateValue);
        intent.putExtra(FinishActivityFragment.DURATION, ((CardioActivity) getActivity()).getTotalTime());
        intent.putExtra(FinishActivityFragment.AVERAGE_SPEED, Math.round(averageSpeed * 100.0f) / 100.0f);
        intent.putExtra(FinishActivityFragment.MAXIMUM_SPEED, Math.round(maxSpeed * 100.0f) / 100.0f);
        intent.putExtra(FinishActivityFragment.AVERAGE_HR, averageHeartRate);
        intent.putExtra(FinishActivityFragment.HEART_RATE_VALUES, entryValues);
        startActivity(intent);
        getActivity().finish();

    }

    private void tryToConnectToHxM() {
//        ((MainActivity) getActivity()).startTracker();
        mProgress.setVisibility(View.VISIBLE);
//        connect.setText("CONNECTING...");
        status.setText("STATUS:CONNECTING...");
        status.setBackgroundColor(getResources().getColor(R.color.save_btn_background));
        status.setTextColor(getResources().getColor(R.color.black));
        pressed = true;
        mHxMConnection.findBT();
    }

    private void initData() {
//        if (UserUtils.getDeviceBattery() < 0) {
//            showBattery = true;
//        } else {
//            hxMBattery.setText(UserUtils.getDeviceBattery() + " %");
//        }
//        ((CardioActivity) getActivity()).showShowMap();
        ((CardioActivity) getActivity()).startTimer();
        maxSpeed = -1;
        initialValue = -1;
        previousValue = -1;
        valueTravelledBefore = 0.0f;
        intermediateValue = 0;
        caloriesValue.setText("0.0");
        UserUtils.setExerciseNumber(UserUtils.getExerciseNumber() + 1);
//        exerciseNumber.setText(UserUtils.getExerciseNumber() + "");
//        date.setText(UserUtils.getTodayDateString());
        startTime = new Date();
        entryValues = new ArrayList<>();

        previousAltitude = mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);

        LogUtils.LOGE(TAG,"STOP SERVICE");
        UserUtils.setIsServiceRunning(false);
        getActivity().stopService(new Intent(getActivity(), CaloriesService.class));
    }

//    @OnClick(R.id.mapButton)
//    void showMap() {
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, new PathGoogleMapFragment())
//                .addToBackStack("")
//                .commit();
//        getActivity().getSupportFragmentManager().executePendingTransactions();
//    }

    @OnClick(R.id.intense_activity_show_graph)
    void statsClicked() {
        Bundle mBundle = new Bundle();
        mBundle.putInt(CubicLineChartFragment.EXERCISE_NUMBER, UserUtils.getExerciseNumber());
        mBundle.putString(CubicLineChartFragment.EXERCISE_DATE, UserUtils.getTodayDateString());
        mBundle.putIntegerArrayList(CubicLineChartFragment.EXERCISE_VALUES, entryValues);
        historyChartFragment.setArguments(mBundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.cardio_container, historyChartFragment)
                .addToBackStack("")
                .commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void receiveValues(final HxMMessage hxMMessage) {
        if (!UserUtils.isTracking()) {
            UserUtils.setIsTracking();
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                //it executes only once
                if (!dataInitialised && getActivity() != null) {
                    initData();
                    dataInitialised = true;
                } else {
                    mProgress.setVisibility(View.GONE);
                    status.setText("STATUS:CONNECTED.");
                    status.setBackgroundColor(getResources().getColor(R.color.actionbar_text_color));
                    status.setTextColor(getResources().getColor(R.color.white));
//                    connect.setText("FINISH");
                    new HandleValues().execute(hxMMessage);
                }
            }
        });
    }

    @Override
    public void socketClosed() {
        if (pressed) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.GONE);
                    DialogHandler.showSimpleDialog(getActivity(), R.string.dialog_socket_closed_title, R.string.dialog_socket_closed_text,
                            R.string.dialog_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    closeConnection(true);
                                    getActivity().finish();
                                }
                            });
                }
            });
        }
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

//    @Override
//    public void setStatusMessage(String s) {
//        status.setText(s);
//    }

    @Override
    public void resetValues() {
        caloriesValue.setText("?");
        heartRateValue.setText(getString(R.string.nothing_sign));
        distanceValue.setText(getString(R.string.nothing_sign));
        speedValue.setText(getString(R.string.nothing_sign));
//        exerciseNumber.setText("");
//        date.setText("");
//        hxMBattery.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.LOGE("onPause", "yep");
    }

    @Override
    public void setProgressVisibility(int visibility) {
        mProgress.setVisibility(visibility);
    }

    @OnClick(R.id.linlaHeaderProgress)
    void progressClicked() {
        //do nothing
    }

    public void closeConnection(boolean saveToDB) {
        minutes = 0;
        seconds = 0;
        ((CardioActivity) getActivity()).stopTracker();
//        connect.setText("CONNECT");
        ((CardioActivity) getActivity()).stopTimer();
//        ((CardioActivity) getActivity()).hideShowMap();
        pressed = false;
        dataInitialised = false;
//        UserUtils.setActualSpeed(0.0f);
        historyChartFragment.resetValues();
        if (initialValue != -1 && saveToDB) {
            UserUtils.setIsTracking();
        }
        try {
            mHxMConnection.closeBT();
//            if (!saveToDB) {
            if (UserUtils.isTracking()) {
                UserUtils.setIsTracking();
            }
//            getActivity().finish();
            LogUtils.LOGE(TAG, "burnt calories: " + (UserUtils.getBurntCalories() + actualCalories)
                    + "new steps " + UserUtils.getStepsNumber() + " <- "
                    + (UserUtils.getStepsNumber() + (int) Math.round(intermediateValue / 1.32)));
            UserUtils.setBurntCalories(UserUtils.getBurntCalories() + actualCalories);
            UserUtils.setStepsNumber(UserUtils.getStepsNumber() + (int) Math.round(intermediateValue / 1.32));
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float getCalories(float distance) {
        LogUtils.LOGE("getCalories", "distance " + distance);
        if (distance == 0)
            return actualCalories;//0;

        int altitude = Math.round(mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure));
        float slope = Float.valueOf(String.format("%.2f", (altitude - previousAltitude) / distance));
        float calories = (0.05f * slope + 0.95f) * UserUtils.getUserWeight() + TREADMILL_FACTOR;
        calories *= (distance / 1000);
        float VO2max = 15.3f * (UserUtils.getUserMaximumHeartRate() / UserUtils.getUserRestingHeartHeartRate());
        actualCalories += (float) Math.round(calories * 100.0f) / 100.0f;
        LogUtils.LOGE("altitude", altitude + " actualCalories " + actualCalories + " new caloeries " + calories + " VO2max " + VO2max + " distance " + distance + " slope " + ((altitude - previousAltitude) / distance));
        previousAltitude = altitude;

        //change intermediate distance value
        distanceToBeSubtracted = intermediateValue;

        return actualCalories;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        pressure = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private class HandleValues extends AsyncTask<HxMMessage, Void, Void> {
        private HxMMessage hxMMessage;
        private boolean updateCalories;
        private float intermediateDistance;

        @Override
        protected Void doInBackground(HxMMessage... hxMMessages) {
            hxMMessage = hxMMessages[0];
            int heartRate = hxMMessage.getHeartRate();
            short distance = hxMMessage.getDistance();
            float speed = hxMMessage.getSpeed();

            if (hxMMessage.getBattery() < UserUtils.getDeviceBattery()) {
                UserUtils.setDeviceBattery(hxMMessage.getBattery());
            }

            if (initialValue == -1) {
                initialValue = distance;
            }

//            if (isShort) {

            //used for map
//                    UserUtils.setActualSpeed(hxMMessage.getSpeed());
//            historyChartFragment.addEntry((float) hxMMessage.getHeartRate());
//
//            entryValues.add(hxMMessage.getHeartRate());

//                    Log.e("@receiveValues", "hxm [" + hxMMessage.getDistance() + "], previous [" + previousValue + " ], traveledBefore [" + valueTravelledBefore + "], " +
//                            "intermediate value [" + intermediateValue + "]");
            if (distance < previousValue) {
                previousValue = distance;
                initialValue = 0;
                //TODO here was
//                        valueTravelledBefore += intermediateValue;
                valueTravelledBefore = intermediateValue;
            } else {
                previousValue = distance;
                intermediateValue = valueTravelledBefore + (((distance - initialValue) * 6.25f) / 100.0f);
//                        intermediateValue += (((hxMMessage.getDistance() - initialValue) * 6.25f) / 100.0f);
//                    distanceValue.setText(new DecimalFormat("##.##").format(intermediateValue));
//                        LogUtils.LOGE(TAG, "final value:[" + intermediateValue + "]");
            }

            secondsCounter++;
            if (secondsCounter == TIME_TO_UPDATE_CALORIES_COUNTER) {
                secondsCounter = 0;
                updateCalories = true;

                //do insertion to DB
                GymDatabaseHelper.getInst().insertHeartRate(hxMMessage);
            } else {
                updateCalories = false;
            }

            if (speed > maxSpeed) {
                maxSpeed = speed;
            }

            messagesNr++;
            averageSpeed = (averageSpeed * (messagesNr - 1) + speed) / messagesNr;
            averageHeartRate = (averageHeartRate * (messagesNr - 1) + heartRate) / messagesNr;

//            } else {
//                if (hxMMessage.getDistance() < previousValue) {
//                    previousValue = hxMMessage.getDistance();
//                    initialValue = 0;
//                    valueTravelledBefore = intermediateValue;
//                } else {
//                    previousValue = hxMMessage.getDistance();
//                    intermediateValue = valueTravelledBefore + (((hxMMessage.getDistance() - initialValue) * 6.25f) / 100.0f);
//                }
//
//                secondsCounter++;
//                if (secondsCounter == TIME_TO_UPDATE_CALORIES_COUNTER) {
//                    secondsCounter = 0;
//                    caloriesValue.setText(getCalories(intermediateValue - distanceToBeSubtracted) + "");
//                    distanceToBeSubtracted = intermediateValue;
//                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int heartRate = hxMMessage.getHeartRate();
            heartRateValue.setText(heartRate + " bmp");
            distanceValue.setText(new DecimalFormat("##.##").format(intermediateValue) + " m");

            historyChartFragment.addEntry((float) heartRate);
            entryValues.add(heartRate);

//            if (showBattery) {
//                showBattery = false;
//                hxMBattery.setText(hxMMessage.getBattery() + " %");
//            }
            if (updateCalories) {
                caloriesValue.setText(getCalories(intermediateValue - distanceToBeSubtracted) + "");
                speedValue.setText(new DecimalFormat("##.##").format(averageSpeed) + " m/s");
                LogUtils.LOGE(TAG, "show " + speedValue.getText().toString());
            }
//            status.setText("maximum speed " + new DecimalFormat("##.##").format(maxSpeed));
            status.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        LogUtils.LOGE(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LogUtils.LOGE(TAG, "onDestroy");
        super.onDestroy();
    }
}
