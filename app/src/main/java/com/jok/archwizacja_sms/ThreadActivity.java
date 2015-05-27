package com.jok.archwizacja_sms;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class ThreadActivity extends ActionBarActivity {

    private DbAccess dba;
    private int[] threadId;
    private String[] threadName;

    MyResultReceiver resultReceiver;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        resultReceiver = new MyResultReceiver(null);
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("receiver", resultReceiver);
        startService(intent);
*/
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


    private void createThreadList() {
        setContentView(R.layout.activity_main);

        threadId = dba.getThreadsIds();
        threadName = dba.getContactsNames();
        MyListAdapter adapter = new MyListAdapter(this, threadName);
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
//                restoreSms();
                showTableScheme(Uri.parse("content://sms/"));
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

    private void restoreSms() {
        ScrollView sv = new ScrollView(this);
        TextView tv = new TextView(this);
        Compress compress = new Compress();
        String files[] = compress.readFiles();
        SmsXmlParser parser = new SmsXmlParser(this);
        SMS.Data data = null;
        try {
            data = parser.parse(files[1]);
        } catch (XmlPullParserException e) {
            Toast.makeText(this, "XmlPullParserException", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }

        String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.getPackageName());
        startActivity(intent);

        // TODO: przerobić SMS.Data dodać warunki i konwersje
        ContentValues values = new ContentValues();

        if (!data._id.equals("null"))
            values.put(SMS.ID, data._id);
        if (!data.thread_id.equals("null"))
            values.put(SMS.THREAD_ID, data.thread_id);
        if (!data.address.equals("null"))
            values.put(SMS.ADDRESS, data.address);
        if (!data.m_size.equals("null"))
            values.put(SMS.M_SIZE, data.m_size);
        if (!data.person.equals("null"))
            values.put(SMS.PERSON, data.person);
        if (!data.date.equals("null"))
            values.put(SMS.DATE, data.date);
        if (!data.date_sent.equals("null"))
            values.put(SMS.DATE_SENT, data.date_sent);
        if (!data.protocol.equals("null"))
            values.put(SMS.PROTOCOL, data.protocol);
        if (!data.read.equals("null"))
            values.put(SMS.READ, data.read);
        if (!data.status.equals("null"))
            values.put(SMS.STATUS, data.status);
        if (!data.type.equals("null"))
            values.put(SMS.TYPE, data.type);
        if (!data.reply_path_present.equals("null"))
            values.put(SMS.REPLY_PATH_PRESENT, data.reply_path_present);
        if (!data.subject.equals("null"))
            values.put(SMS.SUBJECT, data.subject);
        if (!data.body.equals("null"))
            values.put(SMS.BODY, data.body);
        if (!data.service_center.equals("null"))
            values.put(SMS.SERVICE_CENTER, data.service_center);
        if (!data.locked.equals("null"))
            values.put(SMS.LOCKED, data.locked);
        if (!data.sim_id.equals("null"))
            values.put(SMS.SIM_ID, data.sim_id);
        if (!data.error_code.equals("null"))
            values.put(SMS.ERROR_CODE, data.error_code);
        if (!data.seen.equals("null"))
            values.put(SMS.SEEN, data.seen);
        if (!data.star.equals("null"))
            values.put(SMS.STATUS, data.star);
        if (!data.pri.equals("null"))
            values.put(SMS.PRI, data.pri);


        getContentResolver().insert(DbAccess.SMS_URI, values);

        Intent intent2 = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent2.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
        startActivity(intent2);
/*
        String text = "";
        if (data != null) {
            text += SMS.ID + " : " + data._id + "\n";
            text += SMS.THREAD_ID + " : " + data.thread_id + "\n";
            text += SMS.ADDRESS + " : " + data.address + "\n";
            text += SMS.M_SIZE + " : " + data.m_size + "\n";
            text += SMS.PERSON + " : " + data.person + "\n";
            text += SMS.DATE + " : " + data.date + "\n";
            text += SMS.DATE_SENT + " : " + data.date_sent + "\n";
            text += SMS.PROTOCOL + " : " + data.protocol + "\n";
            text += SMS.READ + " : " + data.read + "\n";
            text += SMS.STATUS + " : " + data.status + "\n";
            text += SMS.TYPE + " : " + data.type + "\n";
            text += SMS.REPLY_PATH_PRESENT + " : " + data.reply_path_present + "\n";
            text += SMS.SUBJECT + " : " + data.subject + "\n";
            text += SMS.BODY + " : " + data.body + "\n";
            text += SMS.SERVICE_CENTER + " : " + data.service_center + "\n";
            text += SMS.LOCKED + " : " + data.locked + "\n";
            text += SMS.SIM_ID + " : " + data.sim_id + "\n";
            text += SMS.ERROR_CODE + " : " + data.error_code + "\n";
            text += SMS.SEEN + " : " + data.seen + "\n";
            text += SMS.STAR + " : " + data.star + "\n";
            text += SMS.PRI + " : " + data.pri + "\n";
        }

        tv.setText(text);
        sv.addView(tv);
        setContentView(sv);
        */
        showTableScheme(Uri.parse("content://sms/"));
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
