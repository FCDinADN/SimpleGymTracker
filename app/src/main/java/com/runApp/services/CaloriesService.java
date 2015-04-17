package com.runApp.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.runApp.R;
import com.runApp.pedometer.StepDetecterWithAPI;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;

/**
 * Created by Rares on 03/04/15.
 */
public class CaloriesService extends Service {
    private static final String TAG = CaloriesService.class.getSimpleName();

    //    private PowerManager.WakeLock wakeLock;
//    private NotificationManager mNM;
    private StepDetecterWithAPI mStepDetecterWithAPI;
    private SensorManager mSensorManager;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class CaloriesBinder extends Binder {
        public CaloriesService getService() {
            return CaloriesService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        acquireWakeLock();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepDetecterWithAPI = new StepDetecterWithAPI(mSensorManager);
        mStepDetecterWithAPI.addListener(mListener);

        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        registerReceiver(mReceiver, filter);

        // Tell the user we started.
        Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        LogUtils.LOGE(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");

        // Unregister our receiver.
//        unregisterReceiver(mReceiver);
        unregisterDetector();
//        mNM.cancel(R.string.app_name);

//        wakeLock.release();

        super.onDestroy();

        // Stop detecting
        mSensorManager.unregisterListener(mStepDetecterWithAPI);

        // Tell the user we stopped.
        Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT).show();
    }

    private void registerDetector() {
        Log.i(TAG, "[SERVICE] registerDetector");

        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor pressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if (countSensor != null) {
            LogUtils.LOGE(TAG, "WIN for step sensor");
            mSensorManager.registerListener(mStepDetecterWithAPI, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            LogUtils.LOGE(TAG, "error for step sensor");
        }

        if (pressureSensor != null) {
            LogUtils.LOGE(TAG, "WIN for pressure sensor");
            mSensorManager.registerListener(mStepDetecterWithAPI, pressureSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            LogUtils.LOGE(TAG, "error for pressure sensor");
        }
    }

    private void unregisterDetector() {
        Log.i(TAG, "[SERVICE] unregisterDetector");
        mSensorManager.unregisterListener(mStepDetecterWithAPI);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new CaloriesBinder();

    public interface ICallback {
        public void stepsChanged(int value);

        public void caloriesChanged(float value);
    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                CaloriesService.this.unregisterDetector();
                CaloriesService.this.registerDetector();
//                wakeLock.release();
//                acquireWakeLock();
            }
        }
    };

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        wakeFlags = PowerManager.FULL_WAKE_LOCK;
//        wakeLock = pm.newWakeLock(wakeFlags, TAG);
//        wakeLock.acquire();
    }

    /**
     * Show a notification while this service is running.
     * //
     */
//    private void showNotification(int value, float calories) {
//        CharSequence text = getText(R.string.app_name);
//        Notification notification = new Notification(R.drawable.ic_notification, null,
//                System.currentTimeMillis());
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        //TODO uncomment the following lines
////        notification.defaults = Notification.DEFAULT_SOUND
////                | Notification.DEFAULT_LIGHTS
////                | Notification.DEFAULT_VIBRATE;
//        Intent pedometerIntent = new Intent();
//        pedometerIntent.setComponent(new ComponentName(this, MainActivity.class));
//        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                pedometerIntent, 0);
//        notification.setLatestEventInfo(this, text,
//                (getText(R.string.notification_subtitle) + "[" + value + "] " + calories), contentIntent);
//
//        mNM.notify(R.string.app_name, notification);
//    }

    private StepDetecterWithAPI.Listener mListener = new StepDetecterWithAPI.Listener() {
        @Override
        public void stepsChanged(int value, float calories) {
            LogUtils.LOGE(TAG, "[SERVICE] stepsChanged to " + value);
            UserUtils.setStepsNumber(value);
            UserUtils.setBurntCalories(calories);
//            if (value % 100 == 0)
//                showNotification(value, calories);
        }

        @Override
        public void passValue() {

        }
    };


}
