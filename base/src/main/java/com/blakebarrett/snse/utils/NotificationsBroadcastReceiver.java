package com.blakebarrett.snse.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("snse.broadcastreceiver", "onReceive: " + String.valueOf(intent));
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            final Intent service = new  Intent(context, NotificationService.class);
            context.startService(service);
        } else {
            NotificationUtils.handleIntent(context, intent);
        }
    }
}
