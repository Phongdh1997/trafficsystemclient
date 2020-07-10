package com.hcmut.admin.bktrafficsystem.model;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ext.AndroidExt;
import com.hcmut.admin.bktrafficsystem.modules.data.utils.TrafficNotificationFactory;
import com.hcmut.admin.bktrafficsystem.ui.map.MapActivity;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import java.util.Map;
import java.util.Objects;

import static com.hcmut.admin.bktrafficsystem.modules.data.utils.TrafficNotificationFactory.DIRECTION_NOTIFICATION_ID;
import static com.hcmut.admin.bktrafficsystem.modules.data.utils.TrafficNotificationFactory.NORMAL_NOTIFICATION_ID;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    AndroidExt androidExt = new AndroidExt();

    private static final String NOTI_TYPE_FEILD = "notiType";
    private static final String REPORT_NOTI_TYPE = "reportNotification";
    private static final String DIRECTION_NOTI_TYPE = "directionNotification";

    // report field
    private static final String REPORT_ID_FIELD = "reportId";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> remoteData = remoteMessage.getData();
        String notiType = remoteData.get(NOTI_TYPE_FEILD);
        if (notiType == null) {
            // handle notification message
            pushNotificationMessage();
        } else {
            // handel data message
            switch (notiType) {
                case REPORT_NOTI_TYPE:
                    pushReportNotification(remoteMessage);
                    break;
                case DIRECTION_NOTI_TYPE:
                    pushDirectionNotification(remoteData);
            }
        }
    }

    private void pushNotificationMessage() {
        TrafficNotificationFactory trafficNotificationFactory = TrafficNotificationFactory.getInstance(getApplicationContext());
        Notification notification = trafficNotificationFactory.getNotificationMessage(
                getApplicationContext(),
                MapActivity.class,
                "Notification message",
                "Have a message");
        trafficNotificationFactory.sendNotification(notification, NORMAL_NOTIFICATION_ID);
    }

    private void pushDirectionNotification(Map<String, String> remoteData) {
        TrafficNotificationFactory trafficNotificationFactory = TrafficNotificationFactory.getInstance(getApplicationContext());
        Notification notification = trafficNotificationFactory.getFoundNewWayNotification(
                getApplicationContext(), MapActivity.class);
        trafficNotificationFactory.sendNotification(notification, DIRECTION_NOTIFICATION_ID);
    }

    // TODO: start Detail Report Fragment
    private void pushReportNotification(RemoteMessage remoteMessage) {
        String title = "Đánh giá";
        String reportId = remoteMessage.getData().get(REPORT_ID_FIELD);
        PendingIntent pendingIntent = null;
        if (reportId != null) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("REPORT_ID", Integer.parseInt(reportId));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText(Objects.requireNonNull(remoteMessage.getNotification()).getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);
        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name_01";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPrefUtils.saveNotiToken(getApplicationContext(), s);
    }
}

