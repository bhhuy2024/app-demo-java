package com.example.dentyload4h;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

;


public class PopupCupActivity extends Activity {

    Button mClose6Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_cup);
        mClose6Button= findViewById(R.id.close6_button);



        mClose6Button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                SerialSingleton.getInstance(getApplicationContext()).send("RC1");

            }
        });

    }
}



