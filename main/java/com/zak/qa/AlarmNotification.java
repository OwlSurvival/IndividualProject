package com.zak.qa;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;


public class AlarmNotification {

    public static final void showNotification(Context context,
                                              String channelId,
                                              String channelName,
                                              int notificationId,
                                              String contentTitle) {

        Intent startAppIntent = new Intent(context, QuoteActivity.class);
        PendingIntent startAppPendingIntent = PendingIntent.getActivity(context,
                0, startAppIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent deleteIntent = new Intent(context,
                AlarmNotificationDismissedBroadcastReceiver.class);
        PendingIntent deleteBrodcastPendingIntent = PendingIntent.getBroadcast(context,
                0, deleteIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder nBuilder =
                (new NotificationCompat.Builder(context, channelId))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(contentTitle)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setFullScreenIntent(startAppPendingIntent, true)
                        .setDeleteIntent(deleteBrodcastPendingIntent);

        Notification notification = nBuilder.build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(
               new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH));
        }
        notificationManager.notify(notificationId, notification);

    }
}
