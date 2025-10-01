package com.example.dentyload4h;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class PopupProblemActivity extends Activity {

    Button mClose7Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_problem);
        mClose7Button= findViewById(R.id.close7_button);

        mClose7Button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                SerialSingleton.getInstance(getApplicationContext()).send("RE0");
            }
        });

    }
}



