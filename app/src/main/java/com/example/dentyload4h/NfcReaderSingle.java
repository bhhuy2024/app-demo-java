package com.example.dentyload4h;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.acs.smartcard.Reader;

public class NfcReaderSingle extends IntentService
{

    private UsbManager mManager;
    private Reader mReader;

    public NfcReaderSingle() {
        super("NfcReaderSingle");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("NFC Service","NFC start");


        Toast.makeText(getApplicationContext(), "service starting", Toast.LENGTH_SHORT).show();



        mManager = (UsbManager) getSystemService(getApplicationContext().USB_SERVICE);
        // Initialize reader
        for (UsbDevice device : mManager.getDeviceList().values()) {
            Log.d("NFC Service", "usb Device : "+device.getDeviceName());
            Reader reader = new Reader(mManager);

            if (reader.isSupported(device)) {
                reader.setOnStateChangeListener(new Reader.OnStateChangeListener() {
                    @Override
                    public void onStateChange(int slotNum, int prevState, int currState) {


                        Log.d("NFC Service", "reader on state change" + reader.getDevice().getDeviceName());
                        if (prevState < Reader.CARD_UNKNOWN
                                || prevState > Reader.CARD_SPECIFIC) {
                            prevState = Reader.CARD_UNKNOWN;
                        }

                        if (currState < Reader.CARD_UNKNOWN
                                || currState > Reader.CARD_SPECIFIC) {
                            currState = Reader.CARD_UNKNOWN;
                        }

                        mReader = reader;

                        // Create output string
                        final String outputString = "Slot " + slotNum + ": ";


                    }
                });

                reader.open(device);
                //new OpenTask().execute(device);
                //logMsg("isSupported usb Device"+device.getDeviceName());
                Log.d("NFC", "isSupported usb Device : "+device.getDeviceName());
                //mReaderAdapter.add(device.getDeviceName());
            }
        }
        return super.onStartCommand(intent,flags,startId);
    }

}

