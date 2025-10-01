package com.example.dentyload4h;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class Pjtgo_bActivity extends AppCompatActivity {

    LinearLayout mDoWashing;
    static  Button waterQty;
    static ImageView disinStat, uvClean;
    MediaPlayer mediaPlayer;
    //private final Handler handler = new Handler(Looper.getMainLooper());


    //private Button mDoWashing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pjtgo_b);


        mDoWashing = findViewById(R.id.dowash);
        waterQty = findViewById(R.id.dayButton1);
        disinStat = findViewById(R.id.weekButton);
        uvClean =  findViewById(R.id.monthButton);
        //mWashLog = findViewById(R.id.washlog);


        mDoWashing.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                mediaPlayer = MediaPlayer.create(Pjtgo_bActivity.this, R.raw.wash4_new);
                mediaPlayer.start();

                SerialSingleton.getInstance(getApplicationContext()).send("RE1");

            }

        });

    }
}