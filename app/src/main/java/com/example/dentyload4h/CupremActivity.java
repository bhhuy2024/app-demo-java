package com.example.dentyload4h;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class CupremActivity extends AppCompatActivity {

    Button mGoWashing;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cuprem);


        mGoWashing = findViewById(R.id.cuprem);

        mediaPlayer = MediaPlayer.create(CupremActivity.this, R.raw.wash6_new);
        mediaPlayer.start();

        mGoWashing.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                SerialSingleton.getInstance(getApplicationContext()).send("RB1");

            }

        });

    }
}