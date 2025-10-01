package com.example.dentyload4h;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.acs.smartcard.Features;
import com.acs.smartcard.PinModify;
import com.acs.smartcard.PinProperties;
import com.acs.smartcard.PinVerify;
import com.acs.smartcard.ReadKeyOption;
import com.acs.smartcard.Reader;
import com.acs.smartcard.TlvProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class SingletonTest implements AutoCloseable{
    private static SingletonTest Instance = null;
    private Context context;
    private Reader mReader;

    private Features mFeatures = new Features();
    private PinVerify mPinVerify = new PinVerify();
    private PinModify mPinModify = new PinModify();
    private ReadKeyOption mReadKeyOption = new ReadKeyOption();

    private ArrayList<Reader>mReaders;

    private HashMap<String, String>userSerial = new HashMap<>();

    private String CommandString = "FF CA 00 00 00";


    private static final String[] powerActionStrings = { "Power Down",
            "Cold Reset", "Warm Reset" };

    private static final String[] stateStrings = { "Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific" };

    private static final String[] featureStrings = { "FEATURE_UNKNOWN",
            "FEATURE_VERIFY_PIN_START", "FEATURE_VERIFY_PIN_FINISH",
            "FEATURE_MODIFY_PIN_START", "FEATURE_MODIFY_PIN_FINISH",
            "FEATURE_GET_KEY_PRESSED", "FEATURE_VERIFY_PIN_DIRECT",
            "FEATURE_MODIFY_PIN_DIRECT", "FEATURE_MCT_READER_DIRECT",
            "FEATURE_MCT_UNIVERSAL", "FEATURE_IFD_PIN_PROPERTIES",
            "FEATURE_ABORT", "FEATURE_SET_SPE_MESSAGE",
            "FEATURE_VERIFY_PIN_DIRECT_APP_ID",
            "FEATURE_MODIFY_PIN_DIRECT_APP_ID", "FEATURE_WRITE_DISPLAY",
            "FEATURE_GET_KEY", "FEATURE_IFD_DISPLAY_PROPERTIES",
            "FEATURE_GET_TLV_PROPERTIES", "FEATURE_CCID_ESC_COMMAND" };

    private static final String[] propertyStrings = { "Unknown", "wLcdLayout",
            "bEntryValidationCondition", "bTimeOut2", "wLcdMaxCharacters",
            "wLcdMaxLines", "bMinPINSize", "bMaxPINSize", "sFirmwareID",
            "bPPDUSupport", "dwMaxAPDUDataSize", "wIdVendor", "wIdProduct" };

    int controlCode = Reader.IOCTL_CCID_ESCAPE;;
    private  ArrayList<NfcTagData>NfcTagArray;
    private  HashMap<String,String>nfcCode;
    private NfcReaderInterface NfcCallback = null;



    public static  SingletonTest getInstance(Context _context)
    {

        if(Instance == null)
            Instance = new SingletonTest(_context);

        return Instance;
    }

    public SingletonTest(Context _context)
    {
        context = _context;
        NfcTagArray = new ArrayList<>();
        nfcCode = new HashMap<>();

        init();
    }

    public void setNfcCallback(NfcReaderInterface nfcReaderInterface)
    {
        NfcCallback = nfcReaderInterface;
    }
    public HashMap getNfcSerial()
    {
        return nfcCode;
    }


    public ArrayList<NfcTagData> getNFCReadInfo()
    {
        NfcTagArray = new ArrayList<>();

        for(Reader r : mReaders )
        {
            if(r.getState(0) == Reader.CARD_PRESENT)
            {

                Log.d("State Num:", r.getState(0)+r.getDevice().getDeviceName());
                Log.d("arr size",NfcTagArray.size()+"");
                getNfcCode(stateStrings[Reader.CARD_PRESENT],r);
                Log.d("arr size",NfcTagArray.size()+"");
            }
        }

        return this.NfcTagArray;
    }

    public void init()
    {
        mReaders = new ArrayList<Reader>();
        UsbManager   mManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // Initialize reader


        for (UsbDevice device : mManager.getDeviceList().values()) {

            Log.d("NFC", "usb Device : "+device.getDeviceName());
            Reader reader = new Reader(mManager);

            if (reader.isSupported(device)) {
                reader.setOnStateChangeListener(new Reader.OnStateChangeListener() {
                    @Override
                    public void onStateChange(int slotNum, int prevState, int currState) {

                        Log.d("NFC", "reader on state change" + reader.getDevice().getDeviceName());
                        if (prevState < Reader.CARD_UNKNOWN
                                || prevState > Reader.CARD_SPECIFIC) {
                            prevState = Reader.CARD_UNKNOWN;
                        }

                        if (currState < Reader.CARD_UNKNOWN
                                || currState > Reader.CARD_SPECIFIC) {
                            currState = Reader.CARD_UNKNOWN;
                        }
                        //mReader = reader;

                        // Create output string
                        final String outputString = "Slot " + slotNum + ": "
                                + stateStrings[prevState] + " -> "
                                + stateStrings[currState];
                        // Show output

                        getNfcCode(outputString,reader);
                        Log.d("NFC Singleton",outputString);
                    }
                });
                reader.open(device);
                mReaders.add(reader);
                Log.d("NFC", "isSupported usb Device : "+device.getDeviceName());
            }
        }
    }

    private NfcTagData getNfcCode(String msg, Reader reader)
    {
        Log.d("NFC", msg);

        if(msg.endsWith("Present")){

            TransmitParams params = new TransmitParams();
            params.slotNum = 0;
            params.controlCode =  Reader.IOCTL_CCID_ESCAPE;
            params.commandString = "FF CA 00 00 00";
            params.reder = reader;



            // Transmit APDU
            getNfcCode("Slot " + params.slotNum + ": Transmitting APDU...",reader);
            new TransmitTask().execute(params);
            Log.d("mControlButton","evetn trigger");

        }else if(msg.endsWith("Absent")){
            nfcCode.remove(reader.getDevice().getDeviceName());

            Log.d("mControlButton","Absent evetn trigger");

        }else if(msg.indexOf("90 00") > 0) {

            String delemeter = " ";
            String buffer = "";
            StringTokenizer stk = new StringTokenizer(msg, delemeter);
            for (int i = 0; i < 7; i++) {
                stk.hasMoreElements();
                buffer += stk.nextToken();
            }
            String serialNum = buffer;
            Log.d("NFC Single", serialNum);
            Log.d("NFC Single", "DeviceName"+reader.getDevice().getDeviceName());
            NfcTagData data = new NfcTagData();

            data.userSerial = serialNum;
            data.deviceName = reader.getDevice().getDeviceName();

            Log.d("NFC Single", data.userSerial);
            Log.d("NFC SING","return data");

            nfcCode.put(data.deviceName, data.userSerial);
            NfcTagArray.add(data);

            if(NfcCallback !=null)
                NfcCallback.NfcTag(serialNum);

            return data;

            //userSerial.put(reader.getDevice().getDeviceName(), serialNum);
        }
        Log.d("NFCSING", "return Null");

        return null;
    }
    private void logMsg(String msg) {


        Log.d("NFC", msg);


        if(msg.endsWith("Present")){

            TransmitParams params = new TransmitParams();
            params.slotNum = 0;
            params.controlCode =  Reader.IOCTL_CCID_ESCAPE;
            params.commandString = "FF CA 00 00 00";
            params.reder = mReader;


            // Transmit APDU
            logMsg("Slot " + params.slotNum + ": Transmitting APDU...");
            new TransmitTask().execute(params);


            Log.d("mControlButton","evetn trigger");

        }else if(msg.endsWith("Absent")){
            Log.d("mControlButton","Absent evetn trigger");


        }else if(msg.indexOf("90 00") > 0) {

            String delemeter = " ";
            String buffer = "";
            StringTokenizer stk = new StringTokenizer(msg, delemeter);
            for (int i = 0; i < 7; i++) {
                stk.hasMoreElements();
                buffer += stk.nextToken();
            }
            String serialNum = buffer;

            Log.d("NFC Single", serialNum);
            //Log.d("NFC Single", mReader.getDevice().getDeviceName());
            //userSerial.put(mReader.getDevice().getDeviceName(), serialNum);
        }
    }



    @Override
    public void close() {

        Log.d("NFC Single", "Close Readers");
        for(Reader reder : mReaders)
            reder.close();
    }



    private class TransmitParams {
        public String deviceName;
        public int slotNum;
        public int controlCode;
        public String commandString;

        public Reader reder;

    }

    private class TransmitProgress {

        public int controlCode;
        public byte[] command;
        public int commandLength;
        public byte[] response;
        public int responseLength;
        public Exception e;

        public Reader reader;

    }

    private class TransmitTask extends
            AsyncTask<TransmitParams, TransmitProgress, Void> {
        @Override
        protected Void doInBackground(TransmitParams... params) {


            logMsg("131");
            Reader transmitReader = params[0].reder;

            getNfcCode(params[0].commandString,transmitReader);
            getNfcCode(params[0].slotNum+"",transmitReader);
            getNfcCode(params[0].controlCode+"",transmitReader);



            TransmitProgress progress = null;

            byte[] command = null;
            byte[] response = null;
            int responseLength = 0;
            int foundIndex = 0;
            int startIndex = 0;
            do {


                // Find carriage return
                foundIndex = params[0].commandString.indexOf('\n', startIndex);
                if (foundIndex >= 0) {
                    command = toByteArray(params[0].commandString.substring(
                            startIndex, foundIndex));
                } else {
                    command = toByteArray(params[0].commandString
                            .substring(startIndex));
                }

                // Set next start index
                startIndex = foundIndex + 1;

                response = new byte[65538];
                progress = new TransmitProgress();
                progress.controlCode = params[0].controlCode;


                try {

                    if (params[0].controlCode < 0) {

                        // Transmit APDU
                        responseLength = transmitReader.transmit(params[0].slotNum,
                                command, command.length, response,
                                response.length);

                    } else {

                        // Transmit control command
                        responseLength = transmitReader.control(params[0].slotNum,
                                params[0].controlCode, command, command.length,
                                response, response.length);
                    }

                    progress.command = command;
                    progress.commandLength = command.length;
                    progress.response = response;
                    progress.responseLength = responseLength;
                    progress.e = null;
                    progress.reader = transmitReader;


                } catch (Exception e) {

                    progress.command = null;
                    progress.commandLength = 0;
                    progress.response = null;
                    progress.responseLength = 0;
                    progress.e = e;
                    progress.reader = transmitReader;

                }

                publishProgress(progress);

            } while (foundIndex >= 0);

            return null;
        }

        @Override
        protected void onProgressUpdate(TransmitProgress... progress) {

            if (progress[0].e != null) {

                getNfcCode(progress[0].e.toString(),progress[0].reader);

            } else {

                getNfcCode("Command:",progress[0].reader);
                logBuffer(progress[0].command, progress[0].commandLength,progress[0].reader);

                getNfcCode("Response:",progress[0].reader);
                logBuffer(progress[0].response, progress[0].responseLength,progress[0].reader);

                if (progress[0].response != null
                        && progress[0].responseLength > 0) {


                    int i;

                    // Show control codes for IOCTL_GET_FEATURE_REQUEST
                    if (progress[0].controlCode == Reader.IOCTL_GET_FEATURE_REQUEST) {

                        mFeatures.fromByteArray(progress[0].response,
                                progress[0].responseLength);

                        getNfcCode("Features:",progress[0].reader);
                        for (i = Features.FEATURE_VERIFY_PIN_START; i <= Features.FEATURE_CCID_ESC_COMMAND; i++) {

                            controlCode = mFeatures.getControlCode(i);
                            if (controlCode >= 0) {
                                getNfcCode("Control Code: " + controlCode + " ("
                                        + featureStrings[i] + ")",progress[0].reader);
                            }
                        }

                        // Enable buttons if features are supported
                        /*
                        mVerifyPinButton
                                .setEnabled(mFeatures
                                        .getControlCode(Features.FEATURE_VERIFY_PIN_DIRECT) >= 0);
                        mModifyPinButton
                                .setEnabled(mFeatures
                                        .getControlCode(Features.FEATURE_MODIFY_PIN_DIRECT) >= 0);

                         */
                    }


                    controlCode = mFeatures
                            .getControlCode(Features.FEATURE_IFD_PIN_PROPERTIES);
                    if (controlCode >= 0
                            && progress[0].controlCode == controlCode) {

                        PinProperties pinProperties = new PinProperties(
                                progress[0].response,
                                progress[0].responseLength);

                        getNfcCode("PIN Properties:",progress[0].reader);
                        getNfcCode("LCD Layout: "
                                + toHexString(pinProperties.getLcdLayout()),progress[0].reader);
                        getNfcCode("Entry Validation Condition: "
                                + toHexString(pinProperties
                                .getEntryValidationCondition()),progress[0].reader);
                        getNfcCode("Timeout 2: "
                                + toHexString(pinProperties.getTimeOut2()),progress[0].reader);
                    }

                    controlCode = mFeatures
                            .getControlCode(Features.FEATURE_GET_TLV_PROPERTIES);
                    if (controlCode >= 0
                            && progress[0].controlCode == controlCode) {

                        TlvProperties readerProperties = new TlvProperties(
                                progress[0].response,
                                progress[0].responseLength);

                        Object property;
                        getNfcCode("TLV Properties:",progress[0].reader);
                        for (i = TlvProperties.PROPERTY_wLcdLayout; i <= TlvProperties.PROPERTY_wIdProduct; i++) {

                            property = readerProperties.getProperty(i);
                            if (property instanceof Integer) {
                                getNfcCode(propertyStrings[i] + ": "
                                        + toHexString((Integer) property),progress[0].reader);
                            } else if (property instanceof String) {
                                getNfcCode(propertyStrings[i] + ": " + property,progress[0].reader);
                            }
                        }
                    }
                }
            }
        }
    }



/*
    public void testeddd() {

        // If slot is selected

        byte[] command = null;
        byte[] response = null;
        int responseLength = 0;
        int foundIndex = 0;
        int startIndex = 0;
        int controlCode = Reader.IOCTL_CCID_ESCAPE;;

        response = new byte[65538];
        try {

            if (controlCode < 0) {

                // Transmit APDU
                responseLength = mReader.transmit(0,
                        command, command.length, response,
                        response.length);

            } else {

                // Transmit control command
                responseLength = mReader.control(0,
                        controlCode, command, command.length,
                        response, response.length);
            }

        } catch (Exception e) {

        }
        Log.d("NFC Single", ""+responseLength);
        /*
        do {
            // Set next start index
            startIndex = foundIndex + 1;

            int controlCode = Reader.IOCTL_CCID_ESCAPE;;

            response = new byte[65538];
            try {

                if (controlCode < 0) {

                    // Transmit APDU
                    responseLength = mReader.transmit(0,
                            command, command.length, response,
                            response.length);

                } else {

                    // Transmit control command
                    responseLength = mReader.control(0,
                            controlCode, command, command.length,
                            response, response.length);
                }

            } catch (Exception e) {

            }
            Log.d("NFC Single", ""+responseLength);
        } while (foundIndex >= 0);

    }

*/

    /**
     * Logs the contents of buffer.
     *
     * @param buffer
     *            the buffer.
     * @param bufferLength
     *            the buffer length.
     */
    private void logBuffer(byte[] buffer, int bufferLength,Reader _reader) {

        String bufferString = "";

        for (int i = 0; i < bufferLength; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            if (i % 16 == 0) {

                if (bufferString != "") {

                    getNfcCode(bufferString,_reader);
                    bufferString = "";
                }
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        if (bufferString != "") {
            getNfcCode(bufferString,_reader);
        }
    }

    /**
     * Converts the HEX string to byte array.
     *
     * @param hexString
     *            the HEX string.
     * @return the byte array.
     */
    private byte[] toByteArray(String hexString) {

        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[len] = (byte) (value << 4);

                } else {

                    byteArray[len] |= value;
                    len++;
                }

                first = !first;
            }
        }

        return byteArray;
    }

    /**
     * Converts the integer to HEX string.
     *
     * @param i
     *            the integer.
     * @return the HEX string.
     */
    private String toHexString(int i) {

        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        return hexString.toUpperCase();
    }

    /**
     * Converts the byte array to HEX string.
     *
     * @param buffer
     *            the buffer.
     * @return the HEX string.
     */
    private String toHexString(byte[] buffer) {

        String bufferString = "";

        for (int i = 0; i < buffer.length; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        return bufferString;
    }


}
