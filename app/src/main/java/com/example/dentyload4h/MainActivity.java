package com.example.dentyload4h;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    Button mGocupremove;
    MediaPlayer mediaPlayer;


    //private Button mDoWashing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mGocupremove = findViewById(R.id.goremove);


         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             SingletonTest.getInstance(getApplicationContext());
             SerialSingleton.getInstance(getApplicationContext());
          }

        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.power);
        mediaPlayer.start();
        SystemClock.sleep(3000);
        mediaPlayer.stop();


        mGocupremove.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                SerialSingleton.getInstance(getApplicationContext()).send("RA1");

                //Intent intent = new Intent( MainActivity.this, CupremActivity.class);
                //startActivity(intent);
            }

        });

    }
}