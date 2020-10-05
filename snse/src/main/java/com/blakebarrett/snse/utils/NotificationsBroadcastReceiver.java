package com.blakebarrett.snse.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("snse.broadcastreceiver", "onReceive: " + intent);
        NotificationUtils.handleIntent(context.getApplicationContext(), intent);
    }
}
