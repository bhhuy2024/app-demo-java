package com.example.dentyload4h;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.app.IntentService;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.health.connect.datatypes.Device;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.SerialTimeoutException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.Objects;

public class SerialSingleton implements ServiceConnection, SerialListener {
    int cnt;
    int receive_count = 0;
    private enum Connected { False, Pending, True }

    //private final BroadcastReceiver broadcastReceiver;

    int deviceId, portNum, baudRate;

    private String sendData;
    private UsbSerialPort usbSerialPort;
    private static SerialService service;
    private TextView receiveText;
    private TextView sendText;
    private TerminalFragment.ControlLines controlLines;
    private TextUtil.HexWatcher hexWatcher;
    private Button sendBtn;
    private Connected connected = Connected.False;
    private final boolean initialStart = true;
    private final boolean hexEnabled = false;
    private final boolean controlLinesEnabled = false;
    private boolean pendingNewline = false;
    private final boolean isPopupRemoveCupShown = false;
    private final boolean isPopupCloseDoorShown = false;
    private boolean isWaterComingOut = false;
    private boolean cleanDentureByScreen = false;
    private boolean isHaHa = false;
    private final String newline = TextUtil.newline_crlf;
    boolean usb_connected;
    char rx_buff[];
    StringBuilder sb = new StringBuilder();
    private  static Context context;
    public  static SerialSingleton Instance = null;
    private String RecvText;
    private static String Log_Tag="FTDI";
    SerialSocket socket;
    private UsbDevice v;

    MediaPlayer mediaPlayer;

    public SerialSingleton() {
        Log.d(Log_Tag,"SerialSingleton_ys1");
        Intent intent = new Intent(context, SerialService.class);

        Log.d(Log_Tag,"SerialSingleton_ys2");
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);

        Log.d(Log_Tag,"SerialSingleton_ys3");
        context.startService(intent); // prevents service destroy on unbind from recreated activity caused by orientation change

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
        {
            /* onReceive(Context context, Intent intent) 는 안드로이드의 BroadcastReceiver 클래스에 정의된 메서드로, 시스템이나
            다른 앱에서 발생한 이벤트를 수신(receive)할 때 호출됩니다.
            이 메서드는 앱이 수신한 Intent 객체를 통해 이벤트의 상세 정보를 얻고, 해당 이벤트에 대한 앱의 응답 로직을 수행하는 역할을 합니다.
            브로드캐스트 인텐트가 발생하면 BroadcastReceiver 객체 내부의 onReceive() 메서드가 자동으로 호출됩니다.
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(Log_Tag, "onReceive_ys2");
                if (Constants.INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                Boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                connect(granted);
                }
            }
        };
    }
    public static SerialSingleton getInstance(Context _context)
    {
        Log.d("FTDI", "SerialSingleton in");
        context = _context;
        if(Instance == null) {
            Log.d("FTDI", "SerialSingleton instance");
            Instance = new SerialSingleton();
        }
        return  Instance;

    }

    public void init()
    {
     //   send("SA10012");
     //   send("SB10012");
    }

    private void connect(Boolean permissionGranted) {
        Log.d("FTDI", "CON="+permissionGranted);
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);//dks

        Log.d("FTDI", "CON2="+permissionGranted);

        for(UsbDevice v : usbManager.getDeviceList().values()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.d(Log_Tag, v.getDeviceName());
                Log.d(Log_Tag, Objects.requireNonNull(v.getProductName()));
                Log.d(Log_Tag, String.valueOf(v.getVendorId()));
            }
            if (v.getVendorId() == 1027)
            {
                device = v;
                deviceId = device.getDeviceId();
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Log.d(Log_Tag,device.getProductName()); //--important(sjs)
            Log.d("FTDI","connection failed: device not found");

        }
        if(device == null) {
            Log.d("FTDI","connection failed: device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if(driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if(driver == null) {
            Log.d("FTDI","connection failed: no driver for device");
            return;
        }
        if(driver.getPorts().size() < portNum) {
            Log.d("FTDI","connection failed: not enough ports at device");
            return;
        }
        usbSerialPort = driver.getPorts().get(0);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());

        if(usbConnection == null && permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), PendingIntent.FLAG_IMMUTABLE);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if(usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                Log.d("FTDI","connection failed: permission denied");
            else
                Log.d("FTDI","connection failed: open failed");
            return;
        }

        connected = Connected.Pending;
        try {
            Log.d("FTDI","Try Connect");

            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(9600, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            socket = new SerialSocket(context, usbConnection, usbSerialPort);
            service.connect(socket);
            onSerialConnect();
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((SerialService.SerialBinder) iBinder).getService();
        Log.d("FTDI", "service connected_a");
        try {
            Log.d("FTDI", "service connected_b");
            service.attach(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.d("FTDI", "service connected_c");
        connect(null);
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d("FTDI", "service Disconnected");
        service = null;
    }
    @Override
    public void onSerialConnect() {
        Log.d(Log_Tag,"onSerialConnect");
        connected = Connected.True;
        init();

        if(controlLinesEnabled)
            controlLines.start();
    }
    @Override
    public void onSerialConnectError(Exception e) {
        Log.d(Log_Tag,"connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        //Toast.makeText(getActivity(), "in onSerialRead Function", Toast.LENGTH_SHORT).show();
        Log.d(Log_Tag,"===========in onSerialRead Function===========");
        receive(data);
    }
    @Override
    public void onSerialIoError(Exception e) {
    }
    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        usbSerialPort = null;
    }
    public void send(String str) {
        //cnt =  getArguments().getInt("cnt");
         //Toast.makeText(getActivity(), "cnt is " + cnt +"in fragment", Toast.LENGTH_SHORT).show();
        Log.d(Log_Tag, str);
        if(connected != Connected.True) {
            Log.d(Log_Tag, "not connected jsy");
            Toast.makeText(context, "not connected jsy", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;

            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            }
            else
            {
                msg = str;

                data = (str + newline).getBytes();
            }
            Log.d("FTDI SEND",data.toString());
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            service.write(data);
            Log.d("FTDI SEND",data.toString());
        } catch (SerialTimeoutException e) {
            Log.d("FTDI","serial ");
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }
    private void receive(byte[] data) {
        //Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_SHORT).show();
        if(hexEnabled) {
            receiveText.append(TextUtil.toHexString(data) + '\n');
        }
        else
        {
            String msg = new String(data);
            if(newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                // don't show CR as ^M if directly before LF
                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }
            RecvText = RecvText + (TextUtil.toCaretString(msg, newline.length() != 0));
            rx_buff = msg.toCharArray();
            //Log.d("debug3","==================================="+rx_buff.length);
            //Log.d("debug4","==================================="+receive_count);
            for(int i=0; i<msg.length();i++)
            {
                sb.append(msg.charAt(i));
            }

            //Log.d("(sb.toString()!!!!!!!!!", sb.toString());

            if (sb.toString().contains("\r") || sb.toString().contains("\n")){

                if(sb.toString().contains("ST1"))
                {
                    //mediaPlayer = MediaPlayer.create(context, R.raw.wash4_new);
                    //mediaPlayer.start();

                    context.getApplicationContext().startActivity(new Intent(context, Sub_bActivity.class));
                    //((Activity)context).startActivityForResult(new Intent((Activity)context, Sub_bActivity.class), 8000);
                }


                else if(sb.toString().contains("PG1"))
                {
                    context.getApplicationContext().startActivity(new Intent(context, CupremActivity.class));
                    //startActivityForResult(intent,0);
                }

                if(sb.toString().contains("PG2"))
                {
                    context.getApplicationContext().startActivity(new Intent(context, Pjtgo_bActivity.class));
                    //startActivityForResult(intent,0);
                }

                else if(sb.toString().contains("SE1"))
                {
                    mediaPlayer = MediaPlayer.create(context, R.raw.wash6_new);
                    mediaPlayer.start();
                }

                else if(sb.toString().contains("SE2"))
                {
                    context.getApplicationContext().startActivity(new Intent(context, PopupCupActivity.class));
                }


                else if(sb.toString().contains("SE3"))
                {
                    context.getApplicationContext().startActivity(new Intent(context, PopupMiddleActivity.class));

                }

                else if(sb.toString().contains("SE4"))
                {
                    context.getApplicationContext().startActivity(new Intent(context, PopupDisinActivity.class));
                }

                else if(sb.toString().contains("SE5"))
                {
                    context.getApplicationContext().startActivity(new Intent(context, PopupProblemActivity.class));
                }

                else if(sb.toString().contains("RU1"))
                {
                    Sub_bActivity.progText.setText("살균 소독수를 공급하고 있습니다");
                    Sub_bActivity.progText.setTextColor(Color.parseColor("#C10824"));
                }

                else if(sb.toString().contains("RU2"))
                {
                    Sub_bActivity.progText.setText("소독액을 분사하고 있습니다");
                    Sub_bActivity.progText.setTextColor(Color.parseColor("#2196F3"));
                }

                else if(sb.toString().contains("RU3"))
                {
                    Sub_bActivity.progText.setText("틀니 세척을 진행하고 있습니다");
                    Sub_bActivity.progText.setTextColor(Color.parseColor("#08C15E"));
                }

                else if(sb.toString().contains("RU4"))
                {
                    Sub_bActivity.progText.setText("세척을 완료 하였습니다 ");
                    context.getApplicationContext().startActivity(new Intent(context, CupremActivity.class));
                }

                else if(sb.toString().contains("S10"))
                {
                    Pjtgo_bActivity.waterQty.setText("0");
                }
                else if(sb.toString().contains("S11"))
                {
                    Pjtgo_bActivity.waterQty.setText("1");
                }
                else if(sb.toString().contains("S12"))
                {
                    Pjtgo_bActivity.waterQty.setText("2");
                }
                else if(sb.toString().contains("S13"))
                {
                    Pjtgo_bActivity.waterQty.setText("3");
                }
                else if(sb.toString().contains("S14"))
                {
                    Pjtgo_bActivity.waterQty.setText("4");
                }

                else if(sb.toString().contains("S20"))
                {
                    Pjtgo_bActivity.disinStat.setImageResource(R.drawable.badb);
                }

                else if(sb.toString().contains("S21"))
                {
                    Pjtgo_bActivity.disinStat.setImageResource(R.drawable.midb);
                }

                else if(sb.toString().contains("S23"))
                {
                    Pjtgo_bActivity.disinStat.setImageResource(R.drawable.goodb);
                }

                else if(sb.toString().contains("S30"))
                {
                    Pjtgo_bActivity.uvClean.setImageResource(R.drawable.uvprog);
                }

                else if(sb.toString().contains("S31"))
                {
                    Pjtgo_bActivity.uvClean.setImageResource(R.drawable.stopb);
                }


                if(sb.toString().contains("FE"))
                {
                    Log.d("문 열림!!!!!!!", "문 열림!!!!!!!!!!");
                }
                sb = new StringBuilder();
            }
        }
    }
}
