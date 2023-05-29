package com.zak.qa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private Button setAlarm;
    private TextView nextStartTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAlarm = findViewById(R.id.alarm_button);
        nextStartTxt = findViewById(R.id.next_start_text);

        setAlarm.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(0)
                    .setMinute(0)
                    .setTitleText("Select a time for Alarm pls")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();

            timePicker.addOnPositiveButtonClickListener(view -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                if(System.currentTimeMillis() > calendar.getTimeInMillis()){
                    Toast.makeText(this, "Error, time can be in the past!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
             QuoteAlarmApplication app =(QuoteAlarmApplication) this.getApplication();
                app.setExactAlarmSetAlarmClock(calendar.getTimeInMillis());
             this.updateNextLaunchTime();
            });
            timePicker.show(getSupportFragmentManager(), "tag_picker");
        });
        this.updateNextLaunchTime();
        Log.d(this.getClass().getSimpleName(),"onCreate(Bundle)");
    }

    private void updateNextLaunchTime(){
        Log.d(this.getClass().getCanonicalName(),"updateNextLaunchTime()!!!");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo info = alarmManager.getNextAlarmClock();
        Log.d(this.getClass().getCanonicalName(),"updateNextLaunchTime info: " + info);
        if (info != null){
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(info.getTriggerTime());
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM HH:mm");
            nextStartTxt.setText("Будильник сработает:" + sdf.format(calendar.getTime()));
            Log.d(this.getClass().getCanonicalName(),"NEXT START:" + sdf.format(calendar.getTime()));
            // отображение надписи : будильник сработае ...
        }
    }

}