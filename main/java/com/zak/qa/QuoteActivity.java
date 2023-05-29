package com.zak.qa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class QuoteActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        textView = this.findViewById(R.id.textView);
        QuoteAlarmApplication app = (QuoteAlarmApplication)this.getApplication();
        app.stopRingtone();
        if(app.getQuote()!= null) {
            textView.setText(app.getQuote());
        }
        app.speakQuote();
    }
}
///стоп рингтон , вкл проговор цитаты