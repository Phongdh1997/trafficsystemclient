package com.hcmut.admin.bktrafficsystem.modules.probemodule.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.service.AppForegroundService;
import com.hcmut.admin.bktrafficsystem.ui.MapActivity;

public class TrafficNotificationFactory {

    private String CHANNEL_ID = "Notification1";

    public static final int SEARCH_WAY_NOTIFICATION_ID = 1;

    private static TrafficNotificationFactory serviceNotification;
    private Context context;
    private TrafficNotificationFactory(Context context) {
        this.context = context.getApplicationContext(); }

    public static TrafficNotificationFactory getInstance(Context context) {
        if (serviceNotification == null) {
            serviceNotification = new TrafficNotificationFactory(context);
            serviceNotification.createServiceNotificationChanel(context);
        }
        return serviceNotification;
    }

    public void createServiceNotificationChanel(Context context) {
        String CHANEL_NAME = "Chanel 1";
        String CHANEL_DISCRIPTION = "Day la channel 1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANEL_NAME, android.app.NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANEL_DISCRIPTION);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public Notification getForegroundServiceNotification(Context context) {
        String STOP_ACTION_TITLE = "STOP";
        String NOTIFICATION_CONTENT_TITLE = "Ứng dụng đang chạy";
        String NOTIFICATION_CONTENT_TEXT = "Dữ liệu đang được thu thập ...";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(NOTIFICATION_CONTENT_TITLE)
                .setContentText(NOTIFICATION_CONTENT_TEXT)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        // create "stop" action button to stop foreground service
        Intent stopServiceIntent = new Intent(context.getApplicationContext(), AppForegroundService.class);
        stopServiceIntent.setAction(AppForegroundService.STOP_FOREGROUND_ACTION);
        PendingIntent cancelPendingIntent = PendingIntent.getService(
                context.getApplicationContext(),
                AppForegroundService.SERVICE_STOP_REQUEST_CODE,
                stopServiceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.addAction(R.drawable.ic_arrow_back, STOP_ACTION_TITLE, cancelPendingIntent);

        return mBuilder.build();
    }

    public Notification getFoundNewWayNotification(Context context, Class activity) {
        // config string
        String NOTIFICATION_CONTENT_TITLE = "Cảnh báo";
        String NOTIFICATION_CONTENT_TEXT = "Đã tìm thấy đường đi mới";

        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // pandingIntent for touch notification
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(NOTIFICATION_CONTENT_TITLE)
                .setContentText(NOTIFICATION_CONTENT_TEXT)
                .setContentIntent(pendingIntent)
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(true);

        return mBuilder.build();
    }

    public Notification getNotificationMessage (Context context, Class activity, String title, String contentText) {
        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // pandingIntent for touch notification
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(true);

        return mBuilder.build();
    }

    public void sendNotification (Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(TrafficNotificationFactory.SEARCH_WAY_NOTIFICATION_ID, notification);
        }
    }
}
