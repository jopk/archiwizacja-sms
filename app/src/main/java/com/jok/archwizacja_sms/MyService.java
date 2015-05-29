package com.jok.archwizacja_sms;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.ArrayMap;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

public class MyService extends Service {

    private final String ACTION_FROM_MAIN = "fromMainActivity";
    private final String ACTION_TO_MAIN = "toMainActivity";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("kill"))
                stopSelf();
            if (intent.hasExtra("restore"))
                restore();
        }
    };

    private DbAccess dba;

    private long time;
    private long last_sms_backup;
    private long last_contacts_backup;
    private final long NO_BACKUP = -1;
    private final long NO_AUTO = 0;

    private Thread thread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void restore() {
        Compress compress = new Compress();
        compress.unzip();
        String[] files = compress.readFiles();
        MyXmlParser parser = new MyXmlParser();
        LinkedList<ArrayMap<String, String>> list = new LinkedList<>();
        for (String file : files) {
            try {
                if (file != null) {
                    list.add(parser.parse(file));
                }
            } catch (XmlPullParserException e) {
                Toast.makeText(this, "XmlPullParserException", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
            }
        }
        DbAccess dba = new DbAccess(this);
        if (dba.restoreSms(list)) {
            Toast.makeText(this, "Zrobione.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate() {

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
                        String[] pplData = (last_contacts_backup != NO_BACKUP) ? dba.getXml(last_contacts_backup, DbAccess.CONTACT_TYPE) : null;
                        if (pplData != null) {
                            int ppl_amount = sharedPref.getInt("ppl_amount", 0);;
                            Compress compress = new Compress();
                            String[] files = compress.writeFiles(pplData, ppl_amount);
                            compress.zip(files);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("ppl_amount", ppl_amount + files.length);
                            editor.apply();
                            Calendar calendar = Calendar.getInstance();
                            last_contacts_backup = calendar.getTimeInMillis();
                        }
                        Thread.sleep(time);
                    } while (time != NO_AUTO);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        thread.interrupt();

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
