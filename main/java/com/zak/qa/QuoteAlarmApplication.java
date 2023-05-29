package com.zak.qa;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class QuoteAlarmApplication extends Application  implements TextToSpeech.OnInitListener {

    public static final int EXACT_ALARM_INTENT_REQUEST_CODE = 1001;

    private AlarmManager alarmManager;
    private Ringtone ringtone;
    private TextToSpeech textToSpeech;
    private String quote;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(),"onCreate()");

        final Uri pathToRignton= RingtoneManager.getActualDefaultRingtoneUri(
                       this,
                        RingtoneManager.TYPE_ALARM);

        //this.ringtone =RingtoneManager.getRingtone(this,
        //        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        this.ringtone =RingtoneManager.getRingtone(this, pathToRignton);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        textToSpeech = new TextToSpeech(this, this);
        Log.d(this.getClass().getSimpleName()," ========== version 1.4 ================");
    }

    public final void setExactAlarmSetAlarmClock(long triggerAtMillis) {
        PendingIntent pendingIntent = this.createExactBroadcastAlarmIntent();
        AlarmManager.AlarmClockInfo alarmClockInfo =
                new AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent);
        this.alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
    }

    private PendingIntent createExactBroadcastAlarmIntent()  {
        Intent intent = new Intent(this, ExactAlarmBroadcastReceiver.class);
        return PendingIntent.getBroadcast(
                this,
                EXACT_ALARM_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.getDefault());//Устанавливает язык преобразования текста в речь.
        } else {
            Log.e(this.getClass().getSimpleName(), "TextToSpeech is not working!");
        }
    }

    public void updateQuote() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    QuoteAlarmApplication.this.quote =
                            QuoteAlarmApplication.this.getQuoteFromNet(Locale.getDefault().getLanguage());
                    Intent intent = new Intent(QuoteAlarmApplication.this, QuoteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    QuoteAlarmApplication.this.startActivity(intent);
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
                }
            }
        });
     thread.start();
    }

    public String getQuoteFromNet(final String lang) {           // .............
        String quote = null;
        HttpURLConnection urlConnection = null;
        try {
            String urlParameters = "format=json&method=getQuote&lang=" + lang;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            URL url = new URL("http://www.forismatic.com/api/1.0/");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            urlConnection.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                wr.write(postData);
            }
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                final StringBuffer buffer = new StringBuffer();
                try (InputStream inputStream = urlConnection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));) {
                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line + "\n");
                    if (buffer.length() == 0) {
                        return null;
                    }
                    Log.d(this.getClass().getCanonicalName(), "buffer:" + buffer);
                    JSONObject json = new JSONObject(buffer.toString());
                    quote = json.getString("quoteText");
                    quote += "\n\n    " + json.getString("quoteAuthor")+".";
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "IO Exception", e);
            return null;
} finally {
        if (urlConnection != null) {
        urlConnection.disconnect();
        }
        }
        return quote == null ? "Кто рано встает тому Бох подает. Народная мудрость" : quote;
        }

    public void stopRingtone() {
        if(ringtone != null && ringtone.isPlaying()){
          ringtone.stop();
        }
    }

    public void playRingtone() {
        if(ringtone != null && !ringtone.isPlaying()){
            this.ringtone.play();
        }
    }

    public void speakQuote(){
        textToSpeech.speak(quote, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public String getQuote() {
        return this.quote;
    }
}