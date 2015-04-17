package com.runApp.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.runApp.utils.*;

import java.util.ArrayList;

/**
 * Created by Rares on 03/04/15.
 */
public class StepDetecterWithAPI implements SensorEventListener {

    private final static float TREADMILL_FACTOR = 0.84f;

    private SensorManager sensorManager;
    private float previousAltitude = 0;
    private float pressure;
    private boolean getPressure = false;
    private float actualCalories = 0;

    public StepDetecterWithAPI(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

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
    private int stepDetector = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                //Since it will return the total number since we registered we need to subtract the initial amount
                //for the current steps since we opened app
                if (counterSteps < 1) {
                    // initial value
                    counterSteps = (int) sensorEvent.values[0] - UserUtils.getStepsNumber();
                }

                // Calculate steps taken based on first counter value received.
                stepCounter = (int) sensorEvent.values[0] - counterSteps;
                LogUtils.LOGE("API stepCounter", stepCounter + "");
                getPressure = true;

                Toast.makeText(com.runApp.utils.Utils.getContext(), "steps:" + stepCounter, Toast.LENGTH_SHORT).show();
                break;
            case Sensor.TYPE_PRESSURE:
                if (getPressure) {

//                    if (stepCounter > 10) {
//                    }

                    pressure = sensorEvent.values[0];
                    if (previousAltitude == 0) {
                        previousAltitude = (float) Math.round(sensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure) * 100.0f) / 100.0f;
                        LogUtils.LOGE("FIRST TIME PREVIOUS", "" + previousAltitude);
                    }
                    getPressure = false;
                    LogUtils.LOGE("API DISTANCE", String.format("%.2f", stepCounter / 1.32f));
                    getCalories(Float.parseFloat(String.format("%.2f", stepCounter / 1.32f)));
                    notifyListener();
                }
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public float getCalories(float distance) {
        if (distance == 0)
            return 0;//0;

        float altitude = (float) Math.round(sensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure) * 100.0f) / 100.0f;
        float slope = Float.valueOf(String.format("%.2f", (altitude - previousAltitude) / distance));
        LogUtils.LOGE("ALTITUDE", "previous " + previousAltitude + " actual " + altitude + " slope " + slope);
        float calories = (0.05f * ((altitude - previousAltitude) / distance) + 0.95f) * UserUtils.getUserWeight() + TREADMILL_FACTOR;
        calories *= (distance / 1000);
        float VO2max = 15.3f * (UserUtils.getUserMaximumHeartRate() / UserUtils.getUserRestingHeartHeartRate());
        actualCalories = calories;
        LogUtils.LOGE("altitude", altitude + " actualCalories " + /*actualCalories + */" new caloeries " + calories + " VO2max " + VO2max + " distance " + distance + " slope " + ((altitude - previousAltitude) / distance));
        previousAltitude = altitude;

        return calories;
    }

    //-----------------------------------------------------
    // Listener

    public interface Listener {
        public void stepsChanged(int value, float calories);

        public void passValue();
    }

    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged(stepCounter, actualCalories);
        }
    }

    public void resetCounterStep(){
        counterSteps = 0;
    }
}
