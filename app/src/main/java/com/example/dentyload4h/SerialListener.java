package com.example.dentyload4h;

import java.io.IOException;

public interface  SerialListener {
     void onSerialConnect      () throws IOException;
     void onSerialConnectError (Exception e);
     void onSerialRead         (byte[] data);
    void onSerialIoError      (Exception e);
}
