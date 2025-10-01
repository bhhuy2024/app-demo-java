package com.example.dentyload4h;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Test program for ACS smart card readers.
 *
 * @author Godfrey Chung
 * @version 1.1.1, 16 Apr 2013
 */
public class LogNfcActivity extends Activity implements NfcReaderInterface {
    public static final int REQUEST_CODE1 = 100;
    private static final int MAX_LINES = 25;

    private TextView mResponseTextView;
    private Button mEnrolledButton;
    private LinearLayout mBackButton;
    MediaPlayer mediaPlayer;
    Context context;
    int button_num;
    private String  mName = " ", is_Enrolled = "1";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_nfc);

        context= this.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SingletonTest.getInstance(context).setNfcCallback(this);
            SingletonTest.getInstance(context).getNFCReadInfo();
        }

        final ImageView nfctag = (ImageView) findViewById(R.id.nfc_gif);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(nfctag);
        // Glide.with(this).load(R.drawable.new_gif_door_1).into(gifImage);


        mBackButton = (LinearLayout) findViewById(R.id.backbutton);
        Log.d("=============", "LogNfcActivity");

        button_num = getIntent().getIntExtra("button_num", 0);


        // Initialize response text view
        mResponseTextView = (TextView) findViewById(R.id.main_text_view_response);
        mResponseTextView.setMovementMethod(new ScrollingMovementMethod());
        mResponseTextView.setMaxLines(MAX_LINES);
        mResponseTextView.setText("");


        mBackButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                mediaPlayer.release();
                //is_User = "0";
                Intent intentR = new Intent();
                //intentR.putExtra("is_User", is_User); //사용자에게 입력받은값 넣기
                setResult(RESULT_OK, intentR); //결과를 저장
                finish();
            }

        });
        // Hide input window
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        Log.d("end of create", "trigger event mOpenButton");
    }

    CountDownTimer countDownTimer = new CountDownTimer(1000 * 55, 1000) {
        public void onTick(long millisUntilFinished) {
            //TODO: Do something every second
        }
        public void onFinish() {
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            startActivityForResult(intent,REQUEST_CODE5);
//            countDownTimer.cancel();
            countDownTimer.cancel();

            Intent intentR = new Intent();
            //intentR.putExtra("backTo" , backTo);
            setResult(RESULT_OK,intentR); //결과를 저장
            finish();

        }
    }.start();

    @Override
    public void onResume()
    {
        super.onResume();
        mediaPlayer = MediaPlayer.create(context, R.raw.wash_nfc_activity);
        mediaPlayer.start();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        mediaPlayer.release();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE1) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Close reader
        //mReader.close();
        // Unregister receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SingletonTest.getInstance(context).setNfcCallback(null);
        }
        super.onDestroy();
    }


    @Override
    public void NfcTag(String readerCode) {


        Intent intent = new Intent(getApplicationContext(), LogActivity.class);
        intent.putExtra("serialNum", readerCode); //사용자에게 입력받은값 넣기
        intent.putExtra("is_Enrolled1", 1);
        startActivity(intent);

        Log.d("NFC TAG INterface","Tag Code:"+readerCode);
    }

}