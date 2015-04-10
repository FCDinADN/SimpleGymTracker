package com.runApp.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import com.runApp.ui.activities.CardioActivity;
import com.runApp.database.GymDatabaseHelper;
import com.runApp.models.History;
import com.runApp.models.HxMMessage;
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
    @InjectView(R.id.connectButton)
    TextView connect;
    @InjectView(R.id.linlaHeaderProgress)
    LinearLayout mProgress;
    @InjectView(R.id.cardio_device_status)
    TextView status;
    @InjectView(R.id.cardio_date)
    TextView date;
    @InjectView(R.id.cardio_exercise_number)
    TextView exerciseNumber;
    @InjectView(R.id.cardio_battery)
    TextView hxMBattery;

    private boolean isShort;

    private boolean pressed;
    private HxMConnection mHxMConnection;
    // value user when starting app and it was started before
    private short initialValue = -1;
    //the very previous value sent from server
    private short previousValue = -1;
    private float maxSpeed = -1;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardio, container, false);
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

        tryToConnectToHxM();

        isShort = getArguments().getBoolean(CardioActivity.SHORT_ACTIVITY);

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

    @OnClick(R.id.connectButton)
    void connectClicked() {
        if (!pressed) {
            tryToConnectToHxM();
        } else {
            closeConnection(true);
        }
    }

    private void tryToConnectToHxM() {
//        ((MainActivity) getActivity()).startTracker();
        mProgress.setVisibility(View.VISIBLE);
        connect.setText("CONNECTING...");
        pressed = true;
        mHxMConnection.findBT();
    }

    private void initData() {
        if (UserUtils.getDeviceBattery() < 0) {
            showBattery = true;
        } else {
            hxMBattery.setText(UserUtils.getDeviceBattery() + " %");
        }
//        ((CardioActivity) getActivity()).showShowMap();
        ((CardioActivity) getActivity()).startTimer();
        maxSpeed = -1;
        initialValue = -1;
        previousValue = -1;
        valueTravelledBefore = 0.0f;
        intermediateValue = 0;
        caloriesValue.setText("0.0");
        UserUtils.setExerciseNumber(UserUtils.getExerciseNumber() + 1);
        exerciseNumber.setText(UserUtils.getExerciseNumber() + "");
        date.setText(UserUtils.getDate());
        startTime = new Date();
        entryValues = new ArrayList<>();

        previousAltitude = mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
    }

//    @OnClick(R.id.mapButton)
//    void showMap() {
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, new PathGoogleMapFragment())
//                .addToBackStack("")
//                .commit();
//        getActivity().getSupportFragmentManager().executePendingTransactions();
//    }

    @OnClick(R.id.cardio_stats)
    void statsClicked() {
        Bundle mBundle = new Bundle();
        mBundle.putInt(CubicLineChartFragment.EXERCISE_NUMBER, UserUtils.getExerciseNumber());
        mBundle.putString(CubicLineChartFragment.EXERCISE_DATE, UserUtils.getDate());
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
                    connect.setText("FINISH");
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

    @Override
    public void setStatusMessage(String s) {
        status.setText(s);
    }

    @Override
    public void resetValues() {
        caloriesValue.setText("?");
        heartRateValue.setText("?");
        distanceValue.setText("?");
        speedValue.setText("?");
        exerciseNumber.setText("");
        date.setText("");
        hxMBattery.setText("");
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
        connect.setText("CONNECT");
        ((CardioActivity) getActivity()).stopTimer();
//        ((CardioActivity) getActivity()).hideShowMap();
        pressed = false;
        dataInitialised = false;
//        UserUtils.setActualSpeed(0.0f);
        historyChartFragment.resetValues();
        if (initialValue != -1 && saveToDB) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            GymDatabaseHelper.getInst().insertExercise(new History(UserUtils.getExerciseNumber(), df.format(startTime.getTime()), df.format(new Date().getTime()), intermediateValue));
            UserUtils.setIsTracking();
        }
        try {
            mHxMConnection.closeBT();
//            if (!saveToDB) {
            if (UserUtils.isTracking()) {
                UserUtils.setIsTracking();
            }
            getActivity().finish();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float getCalories(float distance) {
        if (distance == 0)
            return actualCalories;//0;

        float altitude = mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
        float calories = (0.05f * ((altitude - previousAltitude) / distance) + 0.95f) * UserUtils.getUserWeight() + TREADMILL_FACTOR;
        calories *= (distance / 1000);
        float VO2max = 15.3f * (UserUtils.getUserMaximumHeartRate() / UserUtils.getUserRestingHeartHeartRate());
        actualCalories += calories;
        LogUtils.LOGE("altitude", altitude + " actualCalories " + actualCalories + " new caloeries " + calories + " VO2max " + VO2max + " distance " + distance + " slope " + ((altitude - previousAltitude) / distance));
        previousAltitude = altitude;
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

        @Override
        protected Void doInBackground(HxMMessage... hxMMessages) {
            hxMMessage = hxMMessages[0];

            if (hxMMessage.getBattery() < UserUtils.getDeviceBattery()) {
                UserUtils.setDeviceBattery(hxMMessage.getBattery());
            }

            if (initialValue == -1) {
                initialValue = hxMMessage.getDistance();
            }

//            if (isShort) {

            //used for map
//                    UserUtils.setActualSpeed(hxMMessage.getSpeed());
//            historyChartFragment.addEntry((float) hxMMessage.getHeartRate());
//
//            entryValues.add(hxMMessage.getHeartRate());

//                    Log.e("@receiveValues", "hxm [" + hxMMessage.getDistance() + "], previous [" + previousValue + " ], traveledBefore [" + valueTravelledBefore + "], " +
//                            "intermediate value [" + intermediateValue + "]");
            if (hxMMessage.getDistance() < previousValue) {
                previousValue = hxMMessage.getDistance();
                initialValue = 0;
                //TODO here was
//                        valueTravelledBefore += intermediateValue;
                valueTravelledBefore = intermediateValue;
            } else {
                previousValue = hxMMessage.getDistance();
                intermediateValue = valueTravelledBefore + (((hxMMessage.getDistance() - initialValue) * 6.25f) / 100.0f);
//                        intermediateValue += (((hxMMessage.getDistance() - initialValue) * 6.25f) / 100.0f);
//                    distanceValue.setText(new DecimalFormat("##.##").format(intermediateValue));
//                        LogUtils.LOGE(TAG, "final value:[" + intermediateValue + "]");
            }

            secondsCounter++;
            if (secondsCounter == TIME_TO_UPDATE_CALORIES_COUNTER) {
                secondsCounter = 0;
                updateCalories = true;
                distanceToBeSubtracted = intermediateValue;

                //do insertion to DB
                GymDatabaseHelper.getInst().insertHeartRate(hxMMessage);
            } else {
                updateCalories = false;
            }

            if (hxMMessage.getSpeed() > maxSpeed) {
                maxSpeed = hxMMessage.getSpeed();
            }

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
            heartRateValue.setText(hxMMessage.getHeartRate() + "");
            distanceValue.setText(new DecimalFormat("##.##").format(intermediateValue));

            historyChartFragment.addEntry((float) hxMMessage.getHeartRate());
            entryValues.add(hxMMessage.getHeartRate());

            if (showBattery) {
                showBattery = false;
                hxMBattery.setText(hxMMessage.getBattery() + " %");
            }
            speedValue.setText(new DecimalFormat("##.##").format(hxMMessage.getSpeed()));
            if (updateCalories) {
                caloriesValue.setText(getCalories(intermediateValue - distanceToBeSubtracted) + "");
            }
            status.setText("maximum speed " + new DecimalFormat("##.##").format(maxSpeed));
        }
    }
}
