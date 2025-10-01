package com.example.dentyload4h;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.AsyncTask;
import java.util.Random;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SubActivity extends AppCompatActivity {
    private static final String TAG = "Sub_Activity";

    Button btn_return;
    static TextView progText;
    TextView progressText;
    private ProgressBar progressBar;
    private int progressStatus=0;
    private int progressPercent=0;
    MediaPlayer mediaPlayer;


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what ==0) {
                progressBar.setProgress(progressStatus);
                progressText.setText(progressPercent + "%");
            }
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        btn_return = findViewById(R.id.btn_return);
        progressBar=findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        progText = findViewById(R.id.prog1);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 1000) {
                    progressStatus++;
                    progressPercent = progressStatus/10;
                    handler.sendEmptyMessage(0);
                    SystemClock.sleep(2400);
                    //handler.postDelayed(this, 100);
                    btn_return.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mediaPlayer = MediaPlayer.create(SubActivity.this, R.raw.popup_putout_activity);
                            mediaPlayer.start();
                            SystemClock.sleep(200);
                            SerialSingleton.getInstance(getApplicationContext()).send("RE0");
                            SystemClock.sleep(200);
                            Intent intent = new Intent(SubActivity.this, PjtgoActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                btn_return.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaPlayer = MediaPlayer.create(SubActivity.this, R.raw.popup_putout_activity);
                        mediaPlayer.start();
                        SystemClock.sleep(200);
                        SerialSingleton.getInstance(getApplicationContext()).send("RE0");
                        SystemClock.sleep(200);
                        Intent intent = new Intent(SubActivity.this, PjtgoActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
        thread.start();
    }
}