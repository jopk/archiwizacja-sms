package com.jok.archwizacja_sms;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class ThreadActivity extends ActionBarActivity {

    /**
     * Akcja (w uproszczeniu można traktować jako tag) potrzebna żeby serwis rozróżniał wysyłane komunikaty.
     */
    private final String ACTION_FROM_THREADS = "fromThreadsActivity";

    private DbAccess dba;
    /**
     * Te trzy tablice są równoległe, tj.
     * threadId[k] odpowiada threadName[k], a checkedThreads[k] informuje czy ten wątek podlega kopiowaniu.
     */
    private int[] threadId;
    private String[] threadName;
    public int[] checkedThreads = null;
    MyListAdapter adapter;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        dba = new DbAccess(this);
        createThreadList();
    }


    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_save:
                findChecked();
                saveThreads();
                break;
            case R.id.button_now:
                findChecked();
                saveThreads();
                archiveNow();
                break;
        }
    }

    /**
     * Wysyła serwisowi sygnał startu, następnie przesyła listę id wątków do archiwizacji.
     */
    private void saveThreads() {
        int[] ids = checkedThreads;
        Intent startIntent = new Intent(this, MyService.class);
        startIntent.putExtra("save_threads", true);
        startService(startIntent);
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent actionIntent = new Intent();
        actionIntent.putExtra("list", ids);
        actionIntent.setAction(ACTION_FROM_THREADS);
        sendBroadcast(actionIntent);
    }

    /**
     * Zabija serwis i uruchamia ponownie co powoduje co najmniej jednokrotny przebieg pętli w wątku serwisu.
     * Wykonywane w sekwencji PO przesłaniu id wątków do archiwizacji co zapewnia że zrobi co ma zrobić.
     */
    private void archiveNow() {
        Intent killIntent = new Intent();
        killIntent.putExtra("kill", true);
        killIntent.setAction(ACTION_FROM_THREADS);
        sendBroadcast(killIntent);
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent restartIntent = new Intent(this, MyService.class);
        startService(restartIntent);
    }

    /**
     * Wyszukuje zaznaczone do archiwizacji wątki wykorzystując "równoległość" tablic.
     */
    private void findChecked() {
        checkedThreads = new int[threadId.length];
        boolean[] checkedbool;
        ListView list = (ListView) findViewById(R.id.thread_list);
        adapter = (MyListAdapter) list.getAdapter();
        checkedbool = adapter.getChecked();
        for (int i = 0; i < checkedThreads.length; i++) {
            if (checkedbool[i]) {
                checkedThreads[i] = threadId[i];
            }
            else {
                checkedThreads[i] = -1;
            }
        }
    }


    /**
     * Czyta z bazy listę wątków i wyświetla w listview.
     */
    private void createThreadList() {
        setContentView(R.layout.activity_thread);

        threadId = dba.getThreadsIds();
        threadName = dba.getContactsNames();
        adapter = new MyListAdapter(this, threadName);
        ListView list = (ListView) findViewById(R.id.thread_list);
        list.setAdapter(adapter);
        list.setSelection(1);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ThreadActivity.this, SmsActivity.class);
                intent.putExtra("address", threadName[position]);
                intent.putExtra("thread_id", threadId[position]);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_threads, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
