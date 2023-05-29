package com.zak.qa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class ExactAlarmBroadcastReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1001;
    public static final String NOTIFICATION_CHANNEL_ID = "zak_alarm";
    public static final String NOTIFICATION_CHANNEL_NAME = "Zak Alarms";
    public static final String NOTIFICATION_TITLE = "New quote is waiting";

    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
      Log.d(this.getClass().getSimpleName(),"onReceive(..)");

      QuoteAlarmApplication app = (QuoteAlarmApplication) context.getApplicationContext();
      app.updateQuote(); //обнови цитату
      app.playRingtone();//вкл рингтон
      AlarmNotification.showNotification(context,NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME, NOTIFICATION_ID, NOTIFICATION_TITLE);// уведомление покажи
    }
}
