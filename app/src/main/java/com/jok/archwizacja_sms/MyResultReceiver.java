package com.jok.archwizacja_sms;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.Handler;

public class MyResultReceiver extends ResultReceiver {
    // result codes
    public final int THREAD_LIST = 1;
    public final int SMS_IN_THREAD = 2;

    public MyResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // TODO: bla bla bla
        switch (resultCode) {
            case THREAD_LIST:
                // TODO: bla bla bla
                break;
            case SMS_IN_THREAD:
                // TODO: bla bla bla
                break;
            default:
                // TODO: lol coś nie tak
                break;
        }
    }
}
