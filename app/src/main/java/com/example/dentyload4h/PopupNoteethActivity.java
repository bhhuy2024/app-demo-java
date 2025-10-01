package com.example.dentyload4h;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

;


public class PopupNoteethActivity extends Activity {

    Button mCloseButton;
    String backTo, dentureType, startTiming,userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_noteeth);
        mCloseButton=findViewById(R.id.close_button);


        mCloseButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                countDownTimer.cancel();

                backTo = "previous";
                dentureType = "default";
                startTiming = "default";
                userName = "default";

                Intent intentR = new Intent();
                intentR.putExtra("backTo" , backTo);
                intentR.putExtra("dentureType" , dentureType);
                intentR.putExtra("startTiming" , startTiming);
                intentR.putExtra("userName" , userName);
                setResult(RESULT_OK,intentR); //결과를 저장
                finish();
            }
        });

//
//
//        mCloseButton2.setOnClickListener(new Button.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intentR = new Intent();
//                setResult(RESULT_OK,intentR); //결과를 저장
//                finish();
//            }
//        });


    }



    CountDownTimer countDownTimer = new CountDownTimer(1000 * 90, 1000) {

        public void onTick(long millisUntilFinished) {
            //TODO: Do something every second
        }

        public void onFinish() {

            countDownTimer.cancel();
            mCloseButton.performClick();

        }
    }.start();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
