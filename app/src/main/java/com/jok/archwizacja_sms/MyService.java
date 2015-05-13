package com.jok.archwizacja_sms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.widget.Toast;

public class MyService extends Service {

    ResultReceiver resultReceiver;
    DbAccess dbAccess;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "service is running", Toast.LENGTH_SHORT).show();
        resultReceiver = intent.getParcelableExtra("receiver");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: wątek servisu
                stopSelf();
            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
