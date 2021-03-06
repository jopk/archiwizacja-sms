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

    /**
     * Tryby archiwizacji.
     */
    private final long NEVER = 0;
    private final long NO_AUTO = 0;
    /**
     * Akcje dla receivera.
     */
    private final String ACTION_FROM_MAIN = "fromMainActivity";
    private final String ACTION_FROM_THREADS = "fromThreadsActivity";
    /**
     * Tagi dla wydobywania danych z bazy i xmla.
     */
    private final String SMS_TAG = "sms";
    private final String CONTACT_TAG = "contact";

    /**
     * Służy do otrzymywania poleceń od aktywności.
     */
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

    /**
     * Baza danych i wątek archiwizacji.
     */
    private DbAccess dba;
    private Thread thread;

    /**
     * ID wątków do archiwizacji i data wykonania ostatniej kopii.
     */
    private HashSet<String> threadsIds = null;
    private ArrayMap<Integer, Long> lastSmsBackups = null;

    /**
     * Flagi kontrolne dla wątka.
     * restore - czy wrzucić smsy z zipa do bazy.
     * saveT. - czy będzie wykonywany zapis.
     */
    private boolean restore = false;
    private boolean saveThreads = false;

    private long time;

    /**
     * Wykonuje się po onCreate(), ustala tryb pracy.
     */
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

    /**
     * Tworzy serwis i odczytuje ustawienia z SharedPreferences, czyli listę wątków do archiwizacji
     * i datę ich ostatniej kopii, oraz okres wykonywania kopii. Jeśli okres jest =/= 0 to startuje wątek.
     * Rejestruje receivera do odbioru komunikatów z aktywności.
     */
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
                Long value = sharedPref.getLong(SMS_TAG + key, NEVER);
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

    /**
     * Zabija wątek automatyczej archwiizacji i zapisuje ustawienia. Wyrejestrowuje receivera.
     */
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
                if (value != null) {
                    editor.putLong(SMS_TAG + key, value);
                }
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

    /**
     * Serce servisu. Odpala wątek do wykonania kopii lub odzyskania danych.
     * Główna pętla do wykonuje sie co najmniej raz (archiwizacja natychmiastowa-jednorazowa).
     * Kontakty zapisuje tylko przy pierwszym zapisie smsów dla danego wątka.
     * Smsy zapisuje tylko od ostatniej daty zapisu (przyrostowość).
     */
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
                                Compress compress = new Compress(getApplicationContext());
                                String[] files = compress.writeFiles(smsData, sms_amount, SMS_TAG);
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
                                        Compress compress = new Compress(getApplicationContext());
                                        String[] files = compress.writeFiles(pplData, ppl_amount, CONTACT_TAG);
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

    /**
     * Po kolei najpierw czyta smsy, a potem kontakty i w tejże kolejności wrzuca je do bazy.
     * (Ale tylko nie występujące w niej, patrz: Compress.restore[Sms/Contacts]).
     */
    private void restore() {
        Compress compress = new Compress(getApplicationContext());
        compress.unzip();
        String[] files = compress.readFiles();
        MyXmlParser parser = new MyXmlParser(SMS_TAG);
        LinkedList<ArrayMap<String, String>> list = new LinkedList<>();
        for (String file : files) {
            try {
                if (file != null && file.contains(SMS_TAG)) {
                    list.add(parser.parse(file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DbAccess dba = new DbAccess(this);
        if (dba.restoreSms(list)) {
            Toast.makeText(this, "SMS: Zrobione.", Toast.LENGTH_SHORT).show();
        }
        list.clear();
        parser = new MyXmlParser(CONTACT_TAG);
        for (String file : files) {
            try {
                if (file != null && file.contains(CONTACT_TAG)) {
                    list.add(parser.parse(file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dba.restoreContact(list)) {
            Toast.makeText(this, "Kontakty: Zrobione.", Toast.LENGTH_SHORT).show();
        }
        restore = false;
    }

    /**
     * Zapisuje które wątki będą archiwizowane. Set, żeby nie dublować.
     */
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
