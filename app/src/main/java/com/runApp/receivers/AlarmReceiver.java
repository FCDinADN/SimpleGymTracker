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
import com.runApp.pedometer.StepDetecterWithAPI;
import com.runApp.utils.Constants;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hugo.weaving.DebugLog;

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

    private NotificationManager mNM;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getExtras().getInt(ALARM_EXTRAS)) {
            case NOTIFICATION_ALARM_ID:
                mNM = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                showNotification(UserUtils.getStepsNumber(), UserUtils.getBurntCalories());
                break;
            case RESET_ALARM_ID:
                //Send broadcast to reset stepsNumber and burntCalories
                context.sendBroadcast(new Intent(StepDetecterWithAPI.RESET_VALUES));
                break;
        }
    }

    @DebugLog
    public static void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra(ALARM_EXTRAS, NOTIFICATION_ALARM_ID);
        notificationAlarmIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ALARM_ID, notificationIntent, 0);

        Date alarmDate = UserUtils.getAlarmTime();
        if (alarmDate != null) {
//            LogUtils.LOGE(TAG, "setting for " + new SimpleDateFormat(Constants.DATE_FORMAT).format(UserUtils.getAlarmTime()));

            /* Check if alarm time is set to time before actual time
            If the time is before, a notification is shown BUG */
            Date now = new Date();
            if (now.after(alarmDate)){
                Calendar c = Calendar.getInstance();
                c.setTime(alarmDate);
                c.add(Calendar.DATE, 1);
                alarmDate = c.getTime();
                LogUtils.LOGE(TAG, "changing " + UserUtils.getAlarmTime() + " -> " + new SimpleDateFormat(Constants.DATE_FORMAT).format(alarmDate));
            }
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmDate.getTime(), AlarmManager.INTERVAL_DAY, notificationAlarmIntent);
        }
    }

    @DebugLog
    public static void setResetAlarm(Context context) {
        LogUtils.LOGE("UserUtils", "setResetAlarm");

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent resetIntent = new Intent(context, AlarmReceiver.class);
        resetIntent.putExtra(ALARM_EXTRAS, RESET_ALARM_ID);
        PendingIntent resetAlarmIntent = PendingIntent.getBroadcast(context, RESET_ALARM_ID, resetIntent, 0);

        //set alarm for resetting calories and steps
        Date resetAlarmDate = UserUtils.getTodayDate();
        if (resetAlarmDate != null) {
            LogUtils.LOGE(TAG, "setting for reseting " + SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(UserUtils.getTodayDate()) + " == " + UserUtils.getTodayDateString());
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, resetAlarmDate.getTime(), AlarmManager.INTERVAL_DAY, resetAlarmIntent);
        }
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
