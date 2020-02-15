package com.hcmut.admin.googlemapapitest.model;


import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hcmut.admin.googlemapapitest.MyApplication;
import com.hcmut.admin.googlemapapitest.R;
import com.hcmut.admin.googlemapapitest.ext.AndroidExt;
import com.hcmut.admin.googlemapapitest.ui.MapActivity;
import com.hcmut.admin.googlemapapitest.ui.rating.detailReport.DetailReportActivity;

import java.util.Objects;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    AndroidExt androidExt = new AndroidExt();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String title = "";
        Intent intent;
        if (false) {
            title = "Đánh giá";
            intent = new Intent(this, DetailReportActivity.class);
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
        } else {
//            title = "Sự cố giao thông";
//            intent = new Intent(this, MapActivity.class);
//            androidExt.showNotifyDialog(this, "Phía trước ?m có kẹt xe", new ClickDialogListener.Yes() {
//                @Override
//                public void onCLickYes() {
//
//                }
//            });
            Activity activity = ((MyApplication) this.getApplicationContext()).getCurrentActivity();

            if (activity instanceof MapActivity) {
                Intent intnt = new Intent("some_custom_id");
                String body = "";
                if (remoteMessage.getNotification() != null)
                    body = remoteMessage.getNotification().getBody();
                intnt.putExtra("BODY_NOTI", body);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intnt);

//            intent.putExtra("REPORT_ID", Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("reportId"))));
                String channelId = getString(R.string.default_notification_channel_id);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.app_icon)
                                .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                                .setContentText(Objects.requireNonNull(remoteMessage.getNotification()).getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "channel_name_01";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
                    notificationManager.createNotificationChannel(mChannel);
                }

                notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());

            }
        }

    }
}

