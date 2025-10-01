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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Sub_bActivity extends AppCompatActivity {
    private static final String TAG = "Sub_bActivity";

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
        setContentView(R.layout.activity_sub_b);
        btn_return = findViewById(R.id.btn_return2);
        progressBar=findViewById(R.id.progressBar2);
        progressText = findViewById(R.id.progressText2);
        progText = findViewById(R.id.prog2);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus++;
                    progressPercent = progressStatus;
                    handler.sendEmptyMessage(0);
                    SystemClock.sleep(400);
                    //handler.postDelayed(this, 450);
                    btn_return.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            SerialSingleton.getInstance(getApplicationContext()).send("RE0");

                        }
                    });
                }

                btn_return.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SerialSingleton.getInstance(getApplicationContext()).send("RE0");

                    }
                });
            }
        });
        thread.start();
    }
}