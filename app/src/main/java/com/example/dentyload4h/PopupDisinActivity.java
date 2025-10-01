package com.example.dentyload4h;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

;


public class PopupDisinActivity extends Activity {

    Button mClose3Button, mClose4Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_disin);
        mClose3Button= findViewById(R.id.close3_button);
        mClose4Button= findViewById(R.id.close4_button);



        mClose3Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialSingleton.getInstance(getApplicationContext()).send("RC0");
            }
        });
        mClose4Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialSingleton.getInstance(getApplicationContext()).send("RC1");
            }
        });

    }
}



