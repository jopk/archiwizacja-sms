package com.jok.archwizacja_sms;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


public class MainActivity extends ActionBarActivity {

    private final String ACTION_FROM_MAIN = "fromMainActivity";
    private final String ACTION_TO_MAIN = "toMainActivity";

    private Switch swch;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swch = (Switch) findViewById(R.id.switch1);
        swch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchCheck();
            }
        });
    }

    public void switchCheck() {
        if (swch.isChecked()) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("kill", true);
            intent.setAction(ACTION_FROM_MAIN);
            sendBroadcast(intent);
        }
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

        Intent intent;
        switch (id) {
            case R.id.Settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.restore:
                restore();
                break;
            case R.id.archive:
                archive();
                break;
        }
    }
    private void restore() {
        Intent startIntent = new Intent(this, MyService.class);
        startIntent.putExtra("restore", true);
        startService(startIntent);
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent actionIntent = new Intent();
        actionIntent.putExtra("restore", true);
        actionIntent.setAction(ACTION_FROM_MAIN);
        sendBroadcast(actionIntent);
    }

    private void archive() {
        Intent intent = new Intent(getApplicationContext(), ThreadActivity.class);
        startActivity(intent);
    }
}
