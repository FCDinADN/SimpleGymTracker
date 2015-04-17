package com.runApp.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.runApp.R;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.runApp.utils.Utils.getContext;

/**
 * Created by Rares on 10/04/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    private static final String ALARM_EXTRAS = "alarm_extras";
    private static final int NOTIFICATION_ALARM_ID = 0;
    private static final int RESET_ALARM_ID = 1;

    // The app's AlarmManager, which provides access to the system alarm services.
    private static AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private static PendingIntent notificationAlarmIntent;
    private static PendingIntent resetAlarmIntent;

    private NotificationManager mNM;

    @Override
    public void onReceive(Context context, Intent intent) {
        mNM = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        LogUtils.LOGE(TAG, "received! [intent] ");
        LogUtils.LOGE(TAG, "received! [intent] " + intent.getExtras().getInt(ALARM_EXTRAS));

        switch (intent.getExtras().getInt(ALARM_EXTRAS)) {
            case NOTIFICATION_ALARM_ID:
                LogUtils.LOGE(TAG, "! ! show notifications ! !");
                showNotification(UserUtils.getStepsNumber(), UserUtils.getBurntCalories());
                break;
            case RESET_ALARM_ID:
                LogUtils.LOGE(TAG, "! ! ! reseting values ! ! !");
                UserUtils.setBurntCalories(0);
                UserUtils.setStepsNumber(0);
                //TODO announce stepDetector that values have been reset
                break;
        }

//        if (intent.getAction().equals(RESET_ALARM)) {
//            LogUtils.LOGE(TAG, "! ! ! reseting values ! ! !");
//            UserUtils.setBurntCalories(0);
//            UserUtils.setStepsNumber(0);
//        } else {
//            LogUtils.LOGE(TAG, "! ! show notifications ! !");
//            showNotification(UserUtils.getStepsNumber(), UserUtils.getBurntCalories());
//        }

        //check date to reset calories and steps
//        UserUtils.checkDate();

//        LogUtils.LOGE(TAG, "received! [todayDate] " + UserUtils.getTodayDateString());
//        if (new Date().equals(UserUtils.getTodayDate())) {
//            LogUtils.LOGE(TAG, "! ! ! reseting values ! ! !");
//            UserUtils.setBurntCalories(0);
//            UserUtils.setStepsNumber(0);
//        } else {
//        LogUtils.LOGE(TAG, "! ! show notifications ! !");
//        showNotification(UserUtils.getStepsNumber(), UserUtils.getBurntCalories());
//        }
    }

    public static void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra(ALARM_EXTRAS, NOTIFICATION_ALARM_ID);
        notificationAlarmIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ALARM_ID, notificationIntent, 0);

        Date alarmDate = UserUtils.getAlarmTime();
        if (alarmDate != null) {
            LogUtils.LOGE(TAG, "setting for " + SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(UserUtils.getAlarmTime()));
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmDate.getTime(), AlarmManager.INTERVAL_DAY, notificationAlarmIntent);
        }

        // Enable {@code UpdateWakeupReceiver} to automatically restart the alarm when the
        // device is rebooted.
//        UpdateWakeupReceiver.enableReceiver(context);
    }

    public static void setResetAlarm(Context context) {
        LogUtils.LOGE("UserUtils", "setResetAlarm");

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent resetIntent = new Intent(context, AlarmReceiver.class);
        resetIntent.putExtra(ALARM_EXTRAS, RESET_ALARM_ID);
        resetAlarmIntent = PendingIntent.getBroadcast(context, RESET_ALARM_ID, resetIntent, 0);

        //set alarm for resetting calories and steps
        Date resetAlarmDate = UserUtils.getTodayDate();
        if (resetAlarmDate != null) {
            LogUtils.LOGE(TAG, "setting for reseting " + SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(UserUtils.getTodayDate()) + " == " + UserUtils.getTodayDateString());
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, resetAlarmDate.getTime(), AlarmManager.INTERVAL_DAY, resetAlarmIntent);
        }

        // Enable {@code UpdateWakeupReceiver} to automatically restart the alarm when the
        // device is rebooted.
//        UpdateWakeupReceiver.enableReceiver(context);
    }

    public static void cancelAlarm() {
        if (alarmMgr != null) {
            alarmMgr.cancel(notificationAlarmIntent);
        }
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(int value, float calories) {
        LogUtils.LOGE("AlarmReceiver", "showNOtification");

        CharSequence text = getContext().getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_notification, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //TODO uncomment the following lines
        notification.defaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_LIGHTS
                | Notification.DEFAULT_VIBRATE;
        Intent pedometerIntent = new Intent();
        pedometerIntent.setComponent(new ComponentName(getContext(), com.runApp.ui.activities.MainActivity.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0,
                pedometerIntent, 0);
        notification.setLatestEventInfo(getContext(), text,
                (getContext().getText(R.string.notification_subtitle) + "[" + value + "] " + calories), contentIntent);

        mNM.notify(R.string.app_name, notification);
    }
}
