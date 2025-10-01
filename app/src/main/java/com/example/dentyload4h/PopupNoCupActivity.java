package com.example.dentyload4h;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;



public class PopupNoCupActivity extends Activity {

    Button mCloseButton;
    Button mAgainButton;

    TextView txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_nocup);
        mCloseButton= findViewById(R.id.close_button);
        mAgainButton= findViewById(R.id.again_button);

        mCloseButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentR = new Intent();
                intentR.putExtra("close_button" , 1);
                intentR.putExtra("again_button" , 0);
                setResult(RESULT_OK,intentR); //결과를 저장
                finish();
            }
        });

        mAgainButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentR = new Intent();
                intentR.putExtra("close_button" , 0);
                intentR.putExtra("again_button" , 1);
                setResult(RESULT_OK,intentR); //결과를 저장
                finish();
            }
        });

    }

}