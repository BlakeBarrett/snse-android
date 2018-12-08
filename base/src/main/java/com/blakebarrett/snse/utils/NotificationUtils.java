package com.blakebarrett.snse.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.blakebarrett.snse.MainActivity;
import com.blakebarrett.snse.R;
import com.blakebarrett.snse.SettingsActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NotificationUtils {

    public static final String NOTIFICATION_NAME = "com.blakebarrett.snse.reminder";
    public static final String NOTIFICATION_ID = NOTIFICATION_NAME + "_ID";

    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;

    public static void scheduleAlarm(final Context context) {

        final SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
        final String notificationSchedule = manager.getString(SettingsActivity.NotificationPreferenceFragment.NOTIFICATION_FREQUENCY, "");
        final Boolean notificationsEnabled = manager.getBoolean(SettingsActivity.NotificationPreferenceFragment.NOTIFICATION_REMINDER, false);

        if (!notificationsEnabled) {
            if (alarmIntent != null) {
                alarmIntent.cancel();
            }
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 9);

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Intent intent = new Intent(context, MainActivity.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        final String[] intervalValues = context.getResources().getStringArray(R.array.pref_notification_frequency_values);
        final Map<String, Long> intervals = new HashMap<>();
        intervals.put(intervalValues[0], AlarmManager.INTERVAL_HALF_DAY);
        intervals.put(intervalValues[1], AlarmManager.INTERVAL_DAY);
        intervals.put(intervalValues[2], AlarmManager.INTERVAL_DAY * 7);
        intervals.put(intervalValues[3], (long) -1.0);
        intervals.put("", (long) -1.0);

        Long interval = intervals.get(notificationSchedule);
        if (interval < 0) {
            return;
        }

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                interval,
                alarmIntent);
    }

    public static void notify(final Context context, final Intent intent) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = intent.getParcelableExtra(NotificationUtils.NOTIFICATION_NAME);
        final int id = intent.getIntExtra(NotificationUtils.NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }
}
