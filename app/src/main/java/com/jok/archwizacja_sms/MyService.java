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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class MyService extends Service {

    private final long NEVER = 0;
    private final long NO_AUTO = 0;
    private final String ACTION_FROM_MAIN = "fromMainActivity";
    private final String ACTION_FROM_THREADS = "fromThreadsActivity";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("kill"))
                stopSelf();
            if (intent.hasExtra("restore"))
                restore();
            if (intent.hasExtra("list")) {
                int[] ids = intent.getIntArrayExtra("list");
                storeList(ids);
            }
        }
    };

    private DbAccess dba;
    private Thread thread;

    private HashSet<String> threadsIds = null;
    private ArrayMap<Integer, Long> lastSmsBackups = null;

    private boolean restore = false;
    private boolean saveThreads = false;

    private long time;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service.onStartCommand()", Toast.LENGTH_SHORT).show();

        restore = intent.getBooleanExtra("restore", false);
        saveThreads = intent.getBooleanExtra("save_threads", false);
        if (intent.hasExtra("once")) {
            time = NO_AUTO;
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service.onCreate()", Toast.LENGTH_SHORT).show();

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        this.threadsIds = (HashSet<String>) sharedPref.getStringSet("threads", null);
        if (threadsIds != null) {
            Iterator<String> iterator = threadsIds.iterator();
            ArrayMap<Integer, Long> lastSmsBackups = new ArrayMap<>();
            while (iterator.hasNext()) {
                Integer key = Integer.parseInt(iterator.next());
                Long value = sharedPref.getLong("sms" + key, NEVER);
                lastSmsBackups.put(key, value);
            }
            this.lastSmsBackups = lastSmsBackups;
        }

        this.time = sharedPref.getLong(getString(R.string.time_period), NO_AUTO);

        if (receiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_FROM_MAIN);
            intentFilter.addAction(ACTION_FROM_THREADS);
            registerReceiver(receiver, intentFilter);
        }

        thread = createNewThread(sharedPref);
        if (threadsIds != null && !threadsIds.isEmpty() && lastSmsBackups != null && !lastSmsBackups.isEmpty()) {
            thread.start();
        }

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
        editor.putStringSet("threads", threadsIds);
        if (lastSmsBackups != null) {
            for (String key : threadsIds) {
                Long value = lastSmsBackups.get(Integer.parseInt(key));
                editor.putLong("sms" + key, value);
            }
        }
        editor.apply();

        unregisterReceiver(receiver);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Thread createNewThread(final SharedPreferences sharedPref) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (dba == null) {
                        dba = new DbAccess(getApplicationContext());
                    }
                    do {
                        for (String sId : threadsIds) {
                            int id = Integer.parseInt(sId);
                            long smsBackup = lastSmsBackups.get(id);
                            String[] smsData = dba.getSmsXml(smsBackup, id);
                            if (smsData != null) {
                                int sms_amount = sharedPref.getInt("sms_amount", 0);
                                Compress compress = new Compress();
                                String[] files = compress.writeFilesExternal(smsData, sms_amount, "sms");
                                compress.zip(files);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("sms_amount", sms_amount + files.length);
                                editor.apply();
                                Calendar calendar = Calendar.getInstance();
                                Long backup = calendar.getTimeInMillis();
                                lastSmsBackups.put(id, backup);
                            }
                            if (smsBackup == NEVER) {
                                String address = dba.getAddressByThreadId(id);
                                String[] pplData = null;
                                if (address != null) {
                                    pplData = dba.getContactXml(address);
                                    if (pplData != null) {
                                        int ppl_amount = sharedPref.getInt("ppl_amount", 0);
                                        Compress compress = new Compress();
                                        String[] files = compress.writeFilesExternal(pplData, ppl_amount, "contact");
                                        compress.zip(files);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putInt("ppl_amount", ppl_amount + files.length);
                                        editor.apply();
                                    }
                                }
                            }
                        }
                        Thread.sleep(time);
                    } while (time != NO_AUTO);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!restore && !saveThreads) {
                    stopSelf();
                }
            }
        });
    }

    private void restore() {
        Compress compress = new Compress();
        compress.unzip();
        String[] files = compress.readFilesExternal();
        MyXmlParser parser = new MyXmlParser();
        LinkedList<ArrayMap<String, String>> list = new LinkedList<>();
        for (String file : files) {
            try {
                if (file != null && file.contains("sms")) {
                    list.add(parser.parse(file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DbAccess dba = new DbAccess(this);
        if (dba.restoreSms(list)) {
            Toast.makeText(this, "Zrobione.", Toast.LENGTH_SHORT).show();
        }
        restore = false;
    }

    private void storeList(int[] ids) {
        HashSet<String> set = new HashSet<>();
        for (int id : ids) {
            if (id != -1) {
                set.add(String.valueOf(id));
            }
        }
        threadsIds = set;
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("threads", set);
        editor.apply();
        saveThreads = false;
    }

}
