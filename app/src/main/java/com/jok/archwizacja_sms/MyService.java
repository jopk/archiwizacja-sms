package com.jok.archwizacja_sms;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;
import java.util.Calendar;

public class MyService extends Service {

    private final String ACTION_FROM_MAIN = "fromMainActivity";
    private final String ACTION_TO_MAIN = "toMainActivity";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("kill"))
                stopSelf();
        }
    };

    private DbAccess dba;

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

        if (receiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_FROM_MAIN);
            registerReceiver(receiver, intentFilter);
        }


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (dba == null) {
                        dba = new DbAccess(getApplicationContext());
                    }
                    do {
                        String[] smsData = (last_sms_backup != NO_BACKUP) ? dba.getXml(last_sms_backup, DbAccess.SMS_TYPE) : null;
                        if (smsData != null) {
                            test = true;
                            int sms_amount = sharedPref.getInt("sms_amount", 0);
                            Compress compress = new Compress();
                            String[] files = compress.writeFiles(smsData, sms_amount);
                            compress.zip(files);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("sms_amount", sms_amount + files.length);
                            editor.apply();
                            Calendar calendar = Calendar.getInstance();
                            last_sms_backup = calendar.getTimeInMillis();
                        }
                        else
                            test = false;
                        String[] pplData = (last_contacts_backup != NO_BACKUP) ? dba.getXml(last_contacts_backup, DbAccess.CONTACT_TYPE) : null;
                        if (pplData != null) {
                            int ppl_amount = 0;
                            Compress compress = new Compress();
                            String[] files = compress.writeFiles(pplData, ppl_amount);
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
        Toast.makeText(this, "Service.onDestroy()", Toast.LENGTH_SHORT).show();

        time = NO_AUTO; // kills thread
        Thread.interrupted();
        /*
        while (thread.isAlive()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */

        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(getString(R.string.last_sms_backup), last_sms_backup);
        editor.putLong(getString(R.string.last_contacts_backup), NO_BACKUP);
        editor.apply();

        unregisterReceiver(receiver);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
