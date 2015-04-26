package com.runApp.pedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;
import com.runApp.utils.Utils;

/**
 * Created by Rares on 03/04/15.
 */
public class StepDetecterWithAPI implements SensorEventListener {

    private final static String TAG = StepDetecterWithAPI.class.getSimpleName();

    public final static String RESET_VALUES = "reset_values";

    private final static float TREADMILL_FACTOR = 0.84f;
    private final static int UPDATE_SLOPE_THRESHOLD = 5;//13 ~ 10 meters

    private SensorManager sensorManager;
    private int previousAltitude = 0;
    private float pressure;
    private boolean getPressure = false;
    private float actualCalories = 0;
    private int stepsForSlopeCounter = 0;
    private int previousSteps = 0;

    public StepDetecterWithAPI(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        Utils.getContext().registerReceiver(mResetReceiver, new IntentFilter(RESET_VALUES));

//        sensorManager = (SensorManager) com.runApp.utils.Utils.getContext().getSystemService(Context.SENSOR_SERVICE);
//
//        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//
//        if (countSensor != null) {
//            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
//            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//            LogUtils.LOGE("API", "error for sensor");
//        }
    }

    private int stepCounter = 0;
    private int counterSteps = 0;
    private float tempDistance = 0;
//    private int veryPreviousAltitude = 0;//used for big altitude differences

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                //Since it will return the total number since we registered we need to subtract the initial amount
                //for the current steps since we opened app
                if (counterSteps < 1) {
                    // initial value
                    counterSteps = (int) sensorEvent.values[0] - UserUtils.getStepsNumber();
                    stepsForSlopeCounter = 0;

                    actualCalories = UserUtils.getBurntCalories();

                    //get first altitude
                    getPressure = true;
                }

                // Calculate steps taken based on first counter value received.
                stepCounter = (int) sensorEvent.values[0] - counterSteps;
                LogUtils.LOGE("API stepCounter", stepCounter + "");

                if (previousSteps == 0) {
                    previousSteps = stepCounter;
                    LogUtils.LOGE(TAG, "setting previousSteps to " + previousSteps);
                }

                if (stepsForSlopeCounter == UPDATE_SLOPE_THRESHOLD) {
//                    tempDistance = Float.parseFloat(String.format("%.2f", (stepCounter - previousSteps) / 1.32f));
//                    LogUtils.LOGE(TAG, "new tempDistance" + tempDistance + " stepCounter " + stepCounter + " previousSteps " + previousSteps);
                    //update values for calories in optimal conditions
                    stepsForSlopeCounter = 0;
                    previousSteps = 0;

                    getCalories(3.81f);
                }
                stepsForSlopeCounter++;

                //getCalories
                getPressure = true;
//                Toast.makeText(com.runApp.utils.Utils.getContext(), "steps:" + stepCounter, Toast.LENGTH_SHORT).show();
                break;
            case Sensor.TYPE_PRESSURE:
                if (getPressure) {
                    pressure = sensorEvent.values[0];
                    if (previousAltitude == 0) {
                        previousAltitude = Math.round(sensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure));
                    }

//                    // check if threshold is reached to compute calories
//                    if (stepsForSlopeCounter == UPDATE_SLOPE_THRESHOLD) {
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
////                                getCalories(tempDistance);
////                            }
////                        }).start();
//                        //TODO tempDistance is always 3.81
//                        getCalories(3.81f);
//                    }

                    getPressure = false;
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public float getCalories(float distance) {
        if (distance == 0)
            return 0;

        int altitude = Math.round(sensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure));
        float slope = Float.valueOf(String.format("%.2f", (altitude - previousAltitude) / distance));
        LogUtils.LOGE("ALTITUDE", "previous " + previousAltitude + " actual " + altitude + " slope " + slope);
        float calories = (0.05f * slope + 0.95f) * UserUtils.getUserWeight() + TREADMILL_FACTOR;
        calories *= (distance / 1000);
        float VO2max = 15.3f * (UserUtils.getUserMaximumHeartRate() / UserUtils.getUserRestingHeartHeartRate());
        actualCalories += (float) Math.round(calories * 100.0f) / 100.0f;
        LogUtils.LOGE("altitude", altitude + " actualCalories " + actualCalories + " new caloeries " + calories + " VO2max " + VO2max + " distance " + distance + " slope " + ((altitude - previousAltitude) / distance));
        previousAltitude = altitude;

        //TODO move notify to getCalories???
        notifyListener();

        return (float) Math.round(calories) * 100.0f / 100.0f;
    }

    public interface Listener {
        void stepsChanged(int value, float calories);

        void resetValues(int value, float calories);
    }

    private Listener mListeners;

    public void setListener(Listener l) {
        mListeners = l;
    }

    public void notifyListener() {
        mListeners.stepsChanged(stepCounter, actualCalories);
    }

    public void resetCounterStep() {
        LogUtils.LOGE(TAG, "resetCounterStep");
        mListeners.resetValues(stepCounter, actualCalories);
        counterSteps = 0;
    }

    private BroadcastReceiver mResetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resetCounterStep();
        }
    };
}
