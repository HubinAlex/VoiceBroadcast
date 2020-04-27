package com.example.voicebroadcast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText moneyEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moneyEdit = findViewById(R.id.ed_money);
    }

    public void speack(View view) {
        String money = moneyEdit.getText().toString();
        if (!TextUtils.isEmpty(money)) {
            List<String> list = new VoiceTemplate()
                    .prefix("success")
                    .numString(money)
                    .suffix("yuan")
                    .gen();
            VoiceSpeaker.getInstance().speak(list);
        }
    }
}
