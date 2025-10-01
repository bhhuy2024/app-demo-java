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


public class PjtgoActivity extends AppCompatActivity {

    LinearLayout mDoWashing;
    MediaPlayer mediaPlayer;
    //private final Handler handler = new Handler(Looper.getMainLooper());


    //private Button mDoWashing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pjtgo);


        mDoWashing = findViewById(R.id.dowash);
        //mWashLog = findViewById(R.id.washlog);


         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             SingletonTest.getInstance(getApplicationContext());
             SerialSingleton.getInstance(getApplicationContext());
          }
         /*getApplicationContext()`는 안드로이드 앱에서 애플리케이션 자체의 생명주기를 따르는 하나의 Context(맥락)
         객체를 얻는 메소드입니다. 이는 특정 액티비티나 서비스에 국한되지 않고, 앱 전체의 생명주기와 연결된 전역적인
         정보를 다룰 때 사용되며, 싱글톤으로 유지되어 앱이 종료되기 전까지는 동일한 객체를 반환합니다.
          */

        mDoWashing.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                mediaPlayer = MediaPlayer.create(PjtgoActivity.this, R.raw.wash4_new);
                mediaPlayer.start();
                SystemClock.sleep(300);
                //mediaPlayer.stop();
                SerialSingleton.getInstance(getApplicationContext()).send("RE1");


                Intent intent = new Intent( PjtgoActivity.this, SubActivity.class);
                startActivity(intent);
            }

        });

    }
}