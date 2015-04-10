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

import java.util.Date;

import static com.runApp.utils.Utils.getContext;

/**
 * Created by Rares on 10/04/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    // The app's AlarmManager, which provides access to the system alarm services.
    private static AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private static PendingIntent alarmIntent;

    private NotificationManager mNM;

    @Override
    public void onReceive(Context context, Intent intent) {
        mNM = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        showNotification(UserUtils.getStepsNumber(), UserUtils.getBurntCalories());
    }

    public static void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Date alarmDate = UserUtils.getAlarmTime();
        if (alarmDate != null) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmDate.getTime(), alarmIntent);
        }

        // Enable {@code UpdateWakeupReceiver} to automatically restart the alarm when the
        // device is rebooted.
//        UpdateWakeupReceiver.enableReceiver(context);
    }

    public static void cancelAlarm() {
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
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
