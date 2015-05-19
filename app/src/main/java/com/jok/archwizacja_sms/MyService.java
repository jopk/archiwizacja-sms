package com.jok.archwizacja_sms;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.widget.Toast;

public class MyService extends Service {

    ResultReceiver resultReceiver;
    private DbAccess dbAccess;


    private int flags;
    private int startId;
    private long time;


    private Thread thread;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "service is running", Toast.LENGTH_SHORT).show();
        this.resultReceiver = intent.getParcelableExtra("receiver");
//        this.time = intent.getLongExtra("time", 0);
        this.flags = flags;
        this.startId = startId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbAccess = new DbAccess(getApplicationContext());
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public  void onCreate() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: wątek servisu


                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO: przerwanie
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "service is dying", Toast.LENGTH_SHORT).show();
        thread.interrupt();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
