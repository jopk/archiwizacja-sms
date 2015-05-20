package com.jok.archwizacja_sms;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.widget.Toast;

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
        final Context context = getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                dba = new DbAccess(getApplicationContext());
            }
        }).start();

        this.flags = flags;
        this.startId = startId;
        this.resultReceiver = intent.getParcelableExtra("receiver");

        int second = intent.getIntExtra(getString(R.string.time_period_second), 0);
        int minute = intent.getIntExtra(getString(R.string.time_period_minute), 0);
        int hour = intent.getIntExtra(getString(R.string.time_period_hour), 0);
        int day = intent.getIntExtra(getString(R.string.time_period_day), 0);
        int week = intent.getIntExtra(getString(R.string.time_period_week), 0);
        int month = intent.getIntExtra(getString(R.string.time_period_month), 0);

        time = getMiliseconds(second, minute, hour, day, week, month);

        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(getString(R.string.time_period_second), second);
        editor.putInt(getString(R.string.time_period_minute), minute);
        editor.putInt(getString(R.string.time_period_hour), hour);
        editor.putInt(getString(R.string.time_period_day), day);
        editor.putInt(getString(R.string.time_period_week), week);
        editor.putInt(getString(R.string.time_period_month), month);
        editor.putLong(getString(R.string.time_period), time);
        editor.apply();

        return START_STICKY;
    }



    @Override
    public  void onCreate() {

        final Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        last_sms_backup = sharedPref.getLong(getString(R.string.last_sms_backup), 1);
        last_contacts_backup = sharedPref.getLong(getString(R.string.last_contacts_backup), NO_BACKUP);
        time = sharedPref.getLong(getString(R.string.time_period), NO_AUTO);

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
                            new Compress(null).writeFiles(smsData).zip();
                        }
                        String[] pplData = (last_contacts_backup != NO_BACKUP) ? dba.getXml(last_contacts_backup, dba.CONTACT_TYPE) : null;
                        if (pplData != null) {
                            new Compress(null).writeFiles(pplData).zip();
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
            Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
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
        editor.putLong(getString(R.string.time_period), time);
        editor.putLong(getString(R.string.last_sms_backup), 1);
        editor.putLong(getString(R.string.last_contacts_backup), NO_BACKUP);
        editor.apply();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private long getMiliseconds(int second, int minute, int hour, int day, int week, int month) {
        return (((((month * 30) + (week * 7) + day) * 24 + hour) * 60 + minute) * 60 + second) * 1000;

    }

}
