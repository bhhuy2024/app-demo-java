package com.example.dentyload4h;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

;


public class LogActivity extends Activity {


    public static final int REQUEST_CODE1 = 100;

    TextView mUserAddress, mUserName, mTitle;
    Button mButtonDay;
    Button mButtonWeek;
    Button mButtonMonth;
    LinearLayout mgoHomeButton, mLoading;
    ImageView mClosecloseButton;
    String serialNum;
    TextView mLogText;
    String dayWashed = "";
    String weekWashed = "";
    String monthWashed = "";
    String cntDayWashed = "", cntWeekWashed = "", cntMonthWashed = "";

    String cmd;
    String _cmd;
    String baseDay = "";
    String criticalDay = "";
    String userName = "";
    String nextScreen = "";
    String backTo;

    String address = "";

    String jsonName = "default", jsonAddress = "default";

    MediaPlayer mediaPlayer;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log);
        Log.d("=============", "LogPopupActivity");
        mClosecloseButton = findViewById(R.id.closeButton);
        mButtonDay = findViewById(R.id.dayButton);
        mButtonWeek = findViewById(R.id.weekButton);
        mButtonMonth = findViewById(R.id.monthButton);
        mLogText = findViewById(R.id.logText);
        mUserName = findViewById(R.id.nameofuser);
        mUserAddress = findViewById(R.id.addressofuser);
        mgoHomeButton = findViewById(R.id.gohomeButton);
        mLoading = findViewById(R.id.lodaing);

       serialNum = getIntent().getStringExtra("serialNum");
      //serialNum = "04527852FA368100";
        context= this.getApplicationContext();


        if(serialNum == null){
            serialNum = "04527852FA3681  111111";
        }
        //Toast.makeText(getApplicationContext(), serialNum+"logpopup", Toast.LENGTH_LONG).show();
        submit("e");
        submit2("rd");

        mButtonDay.setBackgroundResource(R.drawable.blue_bar);



        //Toast.makeText(getApplicationContext(), valueOf(in_data4), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), valueOf(in_data5), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), valueOf(in_data6), Toast.LENGTH_SHORT).show();
        mButtonDay.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                submit2("rd");
                mLogText.setText("오늘 세척 횟수 " + dayWashed + "회");
                mButtonDay.setBackgroundResource(R.drawable.blue_bar);
                mButtonWeek.setBackgroundResource(R.drawable.grey_bar);
                mButtonMonth.setBackgroundResource(R.drawable.grey_bar);
            }
        });

        mButtonWeek.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                submit2("rw");
                mLogText.setText("지난 1주간 세척 횟수 " + weekWashed + "회");
                //submit2("rw");
                //mLogText.setText("지난 1주간 세척 횟수 " + weekWashed + "회");
                mButtonDay.setBackgroundResource(R.drawable.grey_bar);
                mButtonWeek.setBackgroundResource(R.drawable.blue_bar);
                mButtonMonth.setBackgroundResource(R.drawable.grey_bar);
            }
        });

        mButtonMonth.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                submit2("rm");
                mLogText.setText("지난 1달간 세척 횟수 " + monthWashed + "회");
                submit2("rm");
                mLogText.setText("지난 1달간 세척 횟수 " + monthWashed + "회");
                mButtonDay.setBackgroundResource(R.drawable.grey_bar);
                mButtonWeek.setBackgroundResource(R.drawable.grey_bar);
                mButtonMonth.setBackgroundResource(R.drawable.blue_bar);
            }
        });

        mClosecloseButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //backTo = "previous";
                Intent intentR = new Intent();
                //intentR.putExtra("backTo" , backTo);
                setResult(RESULT_OK,intentR); //결과를 저장
                finish();
            }
        });

        mgoHomeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //nextScreen="Home";
//                Intent intentR = new Intent();
//                //intentR.putExtra("nextScreen" , nextScreen);
//                setResult(RESULT_OK, intentR); //결과를 저장
//                finish();

                Intent intent = new Intent(LogActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(@NonNull Message msg)
        {
            cmd = _cmd;
            try {
                if (cmd.equalsIgnoreCase("rd")) {
                    mLogText.setText("오늘 세척 횟수 " + dayWashed + "회");
                }
                if (cmd.equalsIgnoreCase("rw")) {
                    mLogText.setText("지난 1주간 세척 횟수 " + weekWashed + "회");
                }
                if (cmd.equalsIgnoreCase("rm")) {
                    mLogText.setText("지난 1달간 세척 횟수 " + monthWashed + "회");
                }

                mUserName = findViewById(R.id.nameofuser);
                mUserName.setText(userName);
                mUserAddress = findViewById(R.id.addressofuser);
                mUserAddress.setText(address);
                mediaPlayer.start();

                //mUserAddress.setText(address);
            }catch(NullPointerException e){
                cmd = "rd";
                mUserName = findViewById(R.id.nameofuser);
                mUserName.setText(userName);
                mUserAddress = findViewById(R.id.addressofuser);
                mUserAddress.setText(address);
                mediaPlayer.start();

                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        mediaPlayer = MediaPlayer.create(context, R.raw.new_log);

    }

    public void submit(final String cmd) {//throws IOException {

        OkHttpClient client = new OkHttpClient();
        //mUserName = findViewById(R.id.username);

        JSONObject json = new JSONObject();
        try {
            json.put("cmd", cmd);
            json.put("serialNum", serialNum);

            if(serialNum == null || serialNum.trim().length() == 0){
                //json.put("serialNum", "04F27E52FA3680");
                json.put("serialNum", "04F27E52FA3680");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Request request = new Request.Builder()
                //.addHeader("x-api-key", RestTestCommon.API_KEY)
                .url("https://3o58tmfsx6.execute-api.ap-northeast-2.amazonaws.com/prod4dentiroad")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "9911f040-bf95-b193-ceb3-36b9bbf89e04")
                .post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
                .build();


        //  final CountDownLatch countDownLatch = new CountDownLatch(1);
        //비동기 처리 (enqueue 사용)
        client.newCall(request).enqueue(new Callback() {
                                            //비동기 처리를 위해 Callback 구현
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.e("network failed", e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {

                                                String _body = response.body().string();
                                                //Toast.makeText(getApplicationContext(), _body+"_body", Toast.LENGTH_LONG).show();
                                                Log.d("Not Found ========", _body);
                                                Log.d("Not Found ========", _body);

                                                if(_body.contains("Not Found")){
                                                    //Toast.makeText(getApplicationContext(), _body+"Not Found(in if)", Toast.LENGTH_LONG).show();
                                                    Log.d("user not found=======", serialNum);
                                                    //Toast.makeText(getApplicationContext(), serialNum+"error", Toast.LENGTH_LONG).show();

                                                    countDownTimer.cancel();
                                                    Intent intent = new Intent(LogActivity.this, PopupNoteethActivity.class);
                                                    startActivityForResult(intent, REQUEST_CODE1);

                                                    return;
                                                }

                                                _body = _body.replace("\\", "");
                                                //_body = _body.replace("\"", "'");
                                                _body = _body.substring(1);
                                                _body = _body.substring(0,_body.length()-1);

                                                Log.d("body..", _body);
                                                final String body = _body;
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject(body);

//                                                    if(cmd.equalsIgnoreCase("r")){
//                                                        dayWashed = jsonObject.getString("cleanCnt");
//                                                    }
//
//                                                    if(cmd.equalsIgnoreCase("rw")){
//                                                        weekWashed = jsonObject.getString("cleanCnt");
//                                                    }
//
//                                                    if(cmd.equalsIgnoreCase("rm")){
//                                                        monthWashed = jsonObject.getString("cleanCnt");
//                                                    }

                                                    if(cmd.equalsIgnoreCase("e")){
                                                        //monthWashed = jsonObject.getString("cleanCnt");
                                                        userName = jsonObject.getString("name");
                                                        address = jsonObject.getString("address");

                                                    }

//                                                    baseDay = jsonObject.getString("baseDay");
//                                                    userName = jsonObject.getString("name");
//                                                    address = jsonObject.getString("address");


                                                    //mUserName.setText(userName);
                                                    //mUserAddress.setText(address);


                                                    Log.d("muserName ", userName);
                                                    Log.d("userAddress ", address);


                                                    Message msg = handler.obtainMessage();
                                                    handler.sendMessage(msg);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();

                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
//                                                            if (cmd.equalsIgnoreCase("r")) {
//                                                                mLogText.setText("오늘 세척 횟수 " + dayWashed + "회");
//
//
//                                                            }
//                                                            if (cmd.equalsIgnoreCase("rw")) {
//                                                                mLogText.setText("지난 1주간 세척 횟수 " + weekWashed + "회");
//
//                                                            }
//                                                            if (cmd.equalsIgnoreCase("rm")) {
//                                                                mLogText.setText("지난 1달간 세척 횟수 " + monthWashed + "회");
//                                                            }

                                                            if(cmd.equalsIgnoreCase("e")){
                                                                //monthWashed = jsonObject.getString("cleanCnt");
                                                                mUserName.setText(userName);
                                                                mUserAddress.setText(address);
                                                                mLoading.setVisibility(View.GONE);
                                                                mLogText.setVisibility(View.VISIBLE);

                                                            }
//                                                            _cmd = cmd;
                                                            //mUserName.setText(userName);
                                                            //mUserAddress.setText(address);
                                                        }catch(NullPointerException e){
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });//*/
                                            }


                                        }

        );

    }

    public void submit2 (final String cmd) {//throws IOException {

        OkHttpClient client = new OkHttpClient();
        //mUserName = findViewById(R.id.username);

        JSONObject json = new JSONObject();
        try {
            json.put("cmd", cmd);
            json.put("serialNum", serialNum);

            if(serialNum == null || serialNum.trim().length() == 0){
                //json.put("serialNum", "04F27E52FA3680");
                json.put("serialNum", "04F27E52FA3680");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Request request = new Request.Builder()
                //.addHeader("x-api-key", RestTestCommon.API_KEY)
                .url("https://3o58tmfsx6.execute-api.ap-northeast-2.amazonaws.com/prod4dentiroad")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "9911f040-bf95-b193-ceb3-36b9bbf89e04")
                .post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
                .build();


        //  final CountDownLatch countDownLatch = new CountDownLatch(1);
        //비동기 처리 (enqueue 사용)
        client.newCall(request).enqueue(new Callback() {
                                            //비동기 처리를 위해 Callback 구현
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.e("network failed", e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {

                                                String _body = response.body().string();
                                                //Toast.makeText(getApplicationContext(), _body+"_body", Toast.LENGTH_LONG).show();
                                                Log.d("Not Found ========", _body);
                                                Log.d("Not Found ========", _body);

                                                if(_body.contains("Not Found")){
                                                    //Toast.makeText(getApplicationContext(), _body+"Not Found(in if)", Toast.LENGTH_LONG).show();
                                                    //Log.d("user not found=======", serialNum);
                                                    //Toast.makeText(getApplicationContext(), serialNum+"error", Toast.LENGTH_LONG).show();

                                                    //Intent intent = new Intent(LogActivity.this, PopupNoteethActivity.class);
                                                    //startActivityForResult(intent, REQUEST_CODE1);


                                                    if(cmd.equalsIgnoreCase("rd")){
                                                        dayWashed = "0";
                                                    }

                                                    if(cmd.equalsIgnoreCase("rw")){
                                                        weekWashed = "0";
                                                    }

                                                    if(cmd.equalsIgnoreCase("rm")){
                                                        monthWashed = "0";
                                                    }

                                                    //return;
                                                }

                                                _body = _body.replace("\\", "");
                                                //_body = _body.replace("\"", "'");
                                                _body = _body.substring(1);
                                                _body = _body.substring(0,_body.length()-1);

                                                Log.d("body..", _body);
                                                final String body = _body;
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject(body);

                                                    if(cmd.equalsIgnoreCase("rd")){
                                                        dayWashed = jsonObject.getString("cleanCnt");
                                                    }

                                                    if(cmd.equalsIgnoreCase("rw")){
                                                        weekWashed = jsonObject.getString("cleanCnt");
                                                    }

                                                    if(cmd.equalsIgnoreCase("rm")){
                                                        monthWashed = jsonObject.getString("cleanCnt");
                                                    }


//                                                    baseDay = jsonObject.getString("baseDay");
//                                                    userName = jsonObject.getString("name");
//                                                    address = jsonObject.getString("address");


                                                    //mUserName.setText(userName);
                                                    //mUserAddress.setText(address);
                                                    Log.d("muserName ", userName);
                                                    Log.d("userAddress ", address);



                                                    Message msg = handler.obtainMessage();
                                                    handler.sendMessage(msg);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();

                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            if (cmd.equalsIgnoreCase("rd")) {
                                                                mLogText.setText("오늘 세척 횟수 " + dayWashed + "회");


                                                            }
                                                            if (cmd.equalsIgnoreCase("rw")) {
                                                                mLogText.setText("지난 1주간 세척 횟수 " + weekWashed + "회");

                                                            }
                                                            if (cmd.equalsIgnoreCase("rm")) {
                                                                mLogText.setText("지난 1달간 세척 횟수 " + monthWashed + "회");
                                                            }

                                                            _cmd = cmd;
                                                            //mUserName.setText(userName);
                                                            //mUserAddress.setText(address);
                                                        }catch(NullPointerException e){
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                });//*/
                                            }


                                        }

        );

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

            mClosecloseButton.performClick();

        }
    }.start();

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
//=======================================================================

        if (requestCode == REQUEST_CODE1) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            countDownTimer.cancel();
            Intent intentR = new Intent();
            setResult(RESULT_OK,intentR); //결과를 저장
            finish();


        }

    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mediaPlayer.release();
    }
}



