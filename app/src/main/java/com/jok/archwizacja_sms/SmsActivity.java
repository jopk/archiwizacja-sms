package com.jok.archwizacja_sms;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SmsActivity extends Activity {

    private DbAccess dba;

    String address;
    int t_id;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list);

        dba = new DbAccess(this);


        Bundle extras = getIntent().getExtras();
        address = extras.getString("address");
        t_id = extras.getInt("thread_id");

        String[] data = dba.getSmsesBody(t_id);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sms_row, data);
        ListView listView = (ListView) findViewById(R.id.sms_list);
        listView.setAdapter(adapter);
        listView.setSelection(1);
        listView.setClickable(false);
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
}
