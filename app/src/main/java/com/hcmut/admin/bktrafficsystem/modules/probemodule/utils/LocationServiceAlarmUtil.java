package com.hcmut.admin.bktrafficsystem.modules.probemodule.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.hcmut.admin.bktrafficsystem.modules.probemodule.broadcast.LocationWakefulReceiver;

import static com.hcmut.admin.bktrafficsystem.modules.probemodule.broadcast.LocationWakefulReceiver.WAKEUP_DELAY_MILLIS;
import static com.hcmut.admin.bktrafficsystem.modules.probemodule.broadcast.LocationWakefulReceiver.WAKEUP_ID;
import static com.hcmut.admin.bktrafficsystem.modules.probemodule.broadcast.LocationWakefulReceiver.WAKEUP_LOCATION_SERVICE_ID;

public class LocationServiceAlarmUtil {

    public static void setLocationAlarm(Context context) {
        // create intent to start broadcast
        Intent notificationIntent = new Intent(context, LocationWakefulReceiver.class);
        notificationIntent.putExtra(WAKEUP_ID, WAKEUP_LOCATION_SERVICE_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, WAKEUP_LOCATION_SERVICE_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + WAKEUP_DELAY_MILLIS;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static void cancelLocationAlarm (Context context) {
        // create intent to start broadcast
        Intent notificationIntent = new Intent(context, LocationWakefulReceiver.class);
        notificationIntent.putExtra(WAKEUP_ID, WAKEUP_LOCATION_SERVICE_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, WAKEUP_LOCATION_SERVICE_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {}
    }
}
