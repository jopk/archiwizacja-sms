package com.jok.archwizacja_sms;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    MyResultReceiver resultReceiver;
    private Switch swch;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag);
        /*Configuration config = getResources().getConfiguration();
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        SmsBackup smsFrag = new SmsBackup();
        fragTransaction.replace(android.R.id.content, smsFrag);
        fragTransaction.commit();*/
        swch = (Switch) findViewById(R.id.switch1);
        swch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchCheck();
            }
        });
    }

    public void switchCheck(){
        if(swch.isChecked()){
            resultReceiver = new MyResultReceiver(null);
            Intent intent = new Intent(this, MyService.class);
            intent.putExtra("receiver", resultReceiver);
            startService(intent);
        }
        else {
            Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        Intent intent;
        switch (id) {
            case R.id.Settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.SmsList:
                intent = new Intent(getApplicationContext(), ThreadActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void restore(View v) {
        Compress compress = new Compress();
        compress.unzip();
        String[] files = compress.readFiles();
        SmsXmlParser parser = new SmsXmlParser(this);
        SMS.Data[] data = new SMS.Data[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                if (files[i] != null)
                    data[i] = parser.parse(files[i]);
                else
                    data[i] = null;
            } catch (XmlPullParserException e) {
                Toast.makeText(this, "XmlPullParserException", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
            }
        }
        DbAccess dba = new DbAccess(this);
        dba.restoreSms(data);
    }
}
