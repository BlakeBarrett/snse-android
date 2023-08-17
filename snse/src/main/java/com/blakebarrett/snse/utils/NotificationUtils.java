package com.blakebarrett.snse.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blakebarrett.snse.MainActivity;
import com.blakebarrett.snse.R;
import com.blakebarrett.snse.SettingsActivity;

import java.util.Calendar;

public class NotificationUtils {

    public static final String TYPE = "type";
    public static final String TWO_WEEK_REMINDER = "two_week_reminder_notification";
    private static final String CHANNEL_NAME = "snse-notification-channel";
    private static final String CHANNEL_ID = CHANNEL_NAME + "_ID";
    private static final String CHANNEL_DESCRIPTION = "Reminders to check in with yourself.";
    private static final String APPLICATION_LAUNCH_TIME = "applicationLastLaunchedMillis";
    private static final String TWO_WEEK_NOTIFICATION_TIME = "twoWeeksFromLastTimeAppWasLaunched";
    private static final int TWO_WEEK_NOTIFICATION_ID = 12345;
    //    private static final long TWO_WEEKS_WORTH_OF_MILLISECONDS = (60_000);
    private static final long TWO_WEEKS_WORTH_OF_MILLISECONDS = (60_000 * 60 * 24 * 14);

    private static AlarmManager getAlarmManager(final Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static void handleIntent(final Context context, final Intent intent) {
        final Bundle extras = intent.getExtras();
        final String type = extras != null ? extras.getString(TYPE) : "";
        if (TWO_WEEK_REMINDER.equals(type)) {
            onShowReminderNotification(context);
        } else {
            showNotification(context);
        }
    }

    public static void scheduleAlarm(final Context context) {
        final PreferenceUtil manager = PreferenceUtil.getInstance(context);
        final boolean notificationsEnabled = manager.getBool(SettingsActivity.NotificationPreferenceFragment.NOTIFICATION_REMINDER);
        final String notificationSchedule = manager.getString(SettingsActivity.NotificationPreferenceFragment.NOTIFICATION_FREQUENCY);
        final long interval = Long.parseLong((notificationSchedule.equals("")) ? "0" : notificationSchedule);
        final AlarmManager alarmManager = getAlarmManager(context);

        final Intent intent = new Intent(context.getApplicationContext(), NotificationsBroadcastReceiver.class);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (!notificationsEnabled || (interval <= 0)) {
            if (alarmIntent != null) {
                alarmIntent.cancel();
            }
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                interval,
                alarmIntent);

        manager.setPreferencesClean();
    }

    public static void showNotification(final Context context) {
        showNotification(context, 0);
    }

    private static void showNotification(final Context context, final int id) {
        final Notification notification = getNotification(context);
        NotificationManagerCompat.from(context).notify(id, notification);
    }

    private static void createNotificationChannel(final Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static PendingIntent getMainActivityPendingIntent(final Context context) {
        // Create an explicit intent for an Activity in your app
        final Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private static Notification getNotification(final Context context) {
        createNotificationChannel(context);
        final Bitmap largeIcon = BitmapFactory.decodeResource(
                context.getResources(),
                R.mipmap.ic_launcher_foreground);
        final PendingIntent pendingIntent = getMainActivityPendingIntent(context);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(largeIcon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.feeling))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    public static void applicationDidLaunch(final Context context) {
        final PreferenceUtil prefs = PreferenceUtil.getInstance(context);
        // when was the last time the app was launched
        final long now = System.currentTimeMillis();
        prefs.savePref(APPLICATION_LAUNCH_TIME, now);

        // two weeks from now
        final long twoWeeksFromNow = now + TWO_WEEKS_WORTH_OF_MILLISECONDS;
        prefs.savePref(TWO_WEEK_NOTIFICATION_TIME, twoWeeksFromNow);
        scheduleReminderNotification(context, twoWeeksFromNow);
    }

    private static void scheduleReminderNotification(final Context context, final long when) {
        final Intent intent = new Intent(context.getApplicationContext(), NotificationsBroadcastReceiver.class);
        intent.putExtra(TYPE, TWO_WEEK_REMINDER);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                when,
                alarmIntent);
    }

    public static void onShowReminderNotification(final Context context) {
        final PreferenceUtil prefs = PreferenceUtil.getInstance(context);
        final boolean enabled = prefs.getBool(SettingsActivity.NotificationPreferenceFragment.NOTIFICATION_TWO_WEEK_REMINDER);
        final long twoWeeksFromLastLaunch = prefs.getLong(TWO_WEEK_NOTIFICATION_TIME);

        final long now = System.currentTimeMillis();

        final long lastTimeTheAppWasLaunched = prefs.getLong(APPLICATION_LAUNCH_TIME);
        final boolean hasLaunchedInTheLastTwoWeeks = (now - lastTimeTheAppWasLaunched) < TWO_WEEKS_WORTH_OF_MILLISECONDS;
        final boolean weAreTooSoon = (twoWeeksFromLastLaunch > now);

        // this really shouldn't have triggered yet.
        if (hasLaunchedInTheLastTwoWeeks || weAreTooSoon) {
            return;
        }

        if (enabled) {
            NotificationUtils.showNotification(context, TWO_WEEK_NOTIFICATION_ID);
        }
    }
}
