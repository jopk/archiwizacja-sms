package com.jok.archwizacja_sms;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


public class ThreadActivity extends ActionBarActivity {

    private final String ACTION_FROM_THREADS = "fromThreadsActivity";

    private DbAccess dba;
    private int[] threadId;
    private String[] threadName;
    public int[] checkedThreads;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dba = new DbAccess(this);
        createThreadList();
        checkSelected();
    }

    public int[] checkSelected() {
        int[] checked = new int[threadId.length];

        return checked;
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


    private void createThreadList() {
        setContentView(R.layout.activity_main);
        threadId = dba.getThreadsIds();
        threadName = dba.getContactsNames();
        final MyListAdapter adapter = new MyListAdapter(this, threadName);
        ListView list = (ListView) findViewById(R.id.thread_list);
        Button button = (Button) findViewById(R.id.button);
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedThreads = new int[threadId.length];
                checkedThreads = adapter.getChecked();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.action_sms:
                showTableScheme(Uri.parse("content://sms/sent"));
                return true;
            case R.id.action_thread:
                showTableScheme(Uri.parse("content://sms/conversations"));
                return true;
            case R.id.action_mms:
                showTableScheme(Uri.parse("content://mms/"));
                return true;
            case R.id.action_contacts:
                  showTableScheme(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                return true;
            case R.id.action_main:
                createThreadList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showTableScheme(Uri uri) {
        ScrollView sv = new ScrollView(this);
        TextView tv = new TextView(this);
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        String text = "";
        if(c.moveToNext()) {
            for (int i = 0; i < c.getColumnCount(); i++) {
                text += i + ". " + c.getColumnName(i) + " : ";
                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_BLOB:
                        text += "blob";
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        text += "float";
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        text += "integer";
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        text += "string";
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        text += "null";
                        break;
                }
                text += " : " + c.getString(i) + "\n";
            }
        }
        else {
            text += "data is null";
        }
        c.close();
        tv.setText(text);
        sv.addView(tv);
        setContentView(sv);
    }



}
