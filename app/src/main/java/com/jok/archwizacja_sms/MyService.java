package com.jok.archwizacja_sms;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.widget.Toast;

public class MyService extends Service {

    ResultReceiver resultReceiver;
    DbAccess dbAccess;

    // run data
    int flags;
    int startId;

    Thread thread;
    long time;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "service is running", Toast.LENGTH_SHORT).show();
        resultReceiver = intent.getParcelableExtra("receiver");
        time = intent.getLongExtra("time", -1);




      this.flags = flags;
      this.startId = startId;





        return START_STICKY;
    }

    @Override
    public  void onCreate() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                dbAccess = new DbAccess(getApplicationContext());
                // TODO: wątek servisu
                if (time < 0)
                    return;

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO: coś innego
                //    Thread.currentThread().interrupt();
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
