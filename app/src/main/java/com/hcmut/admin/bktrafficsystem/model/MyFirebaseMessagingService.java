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
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.ext.AndroidExt;
import com.hcmut.admin.bktrafficsystem.modules.probemodule.utils.TrafficNotificationFactory;
import com.hcmut.admin.bktrafficsystem.ui.MapActivity;
import com.hcmut.admin.bktrafficsystem.ui.rating.detailReport.DetailReportActivity;
import com.hcmut.admin.bktrafficsystem.util.SharedPrefUtils;

import java.util.Objects;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    AndroidExt androidExt = new AndroidExt();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        if (false) {
            pushReportNotification(remoteMessage);
        } else {
            pushDirectionNotification(remoteMessage);
        }
    }

    private void pushDirectionNotification(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Notification notification = TrafficNotificationFactory.getInstance(getApplicationContext())
                    .getFoundNewWayNotification(getApplicationContext(), MapActivity.class);
            notificationManager.notify(TrafficNotificationFactory.SEARCH_WAY_NOTIFICATION_ID, notification);
        }
    }

    private void pushReportNotification(RemoteMessage remoteMessage) {
        String title = "Đánh giá";
        Intent intent = new Intent(this, DetailReportActivity.class);
        intent.putExtra("REPORT_ID", Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("reportId"))));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText(Objects.requireNonNull(remoteMessage.getNotification()).getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

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

