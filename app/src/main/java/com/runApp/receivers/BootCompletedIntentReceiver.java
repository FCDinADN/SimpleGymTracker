package com.runApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.runApp.services.CaloriesService;
import com.runApp.utils.LogUtils;
import com.runApp.utils.Utils;

/**
 * Created by Rares on 04/04/15.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedIntentReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            LogUtils.LOGE(TAG, "setting alarms from reboot!");
            AlarmReceiver.setAlarm(Utils.getContext());
            AlarmReceiver.setResetAlarm(Utils.getContext());

            Intent pushIntent = new Intent(context, CaloriesService.class);
            context.startService(pushIntent);
        }
    }
}
