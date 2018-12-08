package com.blakebarrett.snse.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            NotificationUtils.scheduleAlarm(context);
        } else {
            NotificationUtils.notify(context, intent);
        }
    }
}
