package com.runApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.runApp.services.CaloriesService;

/**
 * Created by Rares on 04/04/15.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, CaloriesService.class);
            context.startService(pushIntent);
        }
    }
}
