package com.zak.qa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmNotificationDismissedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(),"onReceive(..)");
        QuoteAlarmApplication app = (QuoteAlarmApplication) context.getApplicationContext();
        app.stopRingtone();
    }
}
