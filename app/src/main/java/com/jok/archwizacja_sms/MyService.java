package com.jok.archwizacja_sms;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.widget.Toast;
import java.util.Calendar;

public class MyService extends Service {

    ResultReceiver resultReceiver;
    private DbAccess dba;


    private int flags;
    private int startId;

    private long time;
    private long last_sms_backup;
    private long last_contacts_backup;
    private final long NO_BACKUP = -1;
    private final long NO_AUTO = 0;

    private boolean test = false;

    private Thread thread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "service is running", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dba = new DbAccess(getApplicationContext());
            }
        }).start();

        this.flags = flags;
        this.startId = startId;
        this.resultReceiver = intent.getParcelableExtra("receiver");

        return START_STICKY;
    }



    @Override
    public  void onCreate() {

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        this.last_sms_backup = sharedPref.getLong(getString(R.string.last_sms_backup), 1);
        this.last_contacts_backup = sharedPref.getLong(getString(R.string.last_contacts_backup), NO_BACKUP);
        this.time = sharedPref.getLong(getString(R.string.time_period), NO_AUTO);


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (dba == null) {
                        Thread.sleep(1000L);
                    }
                    do {
                        String[] smsData = (last_sms_backup != NO_BACKUP) ? dba.getXml(last_sms_backup, dba.SMS_TYPE) : null;
                        if (smsData != null) {
                            test = true;
                            int sms_ammount = sharedPref.getInt("sms_app_id", 0);
                            Compress compress = new Compress();
                            String[] files = compress.writeFiles(smsData, sms_ammount);
                            compress.zip(files);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("sms_ammount", sms_ammount + files.length);
                            editor.apply();
                            Calendar calendar = Calendar.getInstance();
                            last_sms_backup = calendar.getTimeInMillis();
                        }
                        else
                            test = false;
                        String[] pplData = (last_contacts_backup != NO_BACKUP) ? dba.getXml(last_contacts_backup, dba.CONTACT_TYPE) : null;
                        if (pplData != null) {
                            int ppl_ammount = 0;
                            Compress compress = new Compress();
                            String[] files = compress.writeFiles(pplData, ppl_ammount);
                            compress.zip(files);
                            Calendar calendar = Calendar.getInstance();
                            last_contacts_backup = calendar.getTimeInMillis();
                        }
                        Thread.sleep(time);
                    } while (time != NO_AUTO);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                stopSelf();
            }
        });
        thread.start();
    }

    @Override
    public void onDestroy() {
        if (test) {
            Toast.makeText(getApplicationContext(), "true=" + String.valueOf(time), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "false=" + String.valueOf(time), Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(getApplicationContext(), "service is dying", Toast.LENGTH_SHORT).show();
        time = NO_AUTO; // kills thread
        Thread.interrupted();
        while (thread.isAlive()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(getString(R.string.last_sms_backup), last_sms_backup);
        editor.putLong(getString(R.string.last_contacts_backup), NO_BACKUP);
        editor.apply();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
