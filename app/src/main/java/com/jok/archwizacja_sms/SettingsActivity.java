package com.jok.archwizacja_sms;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadSettings();
    }

    @Override
    protected void onDestroy() {
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

    private void loadSettings() {
        final Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        int second = sharedPref.getInt(getString(R.string.time_period_second),
                Integer.parseInt(getString(R.string.default_setting)));
        int minute = sharedPref.getInt(getString(R.string.time_period_minute),
                Integer.parseInt(getString(R.string.default_setting)));
        int hour = sharedPref.getInt(getString(R.string.time_period_hour),
                Integer.parseInt(getString(R.string.default_setting)));
        int day = sharedPref.getInt(getString(R.string.time_period_day),
                Integer.parseInt(getString(R.string.default_setting)));
        int week = sharedPref.getInt(getString(R.string.time_period_week),
                Integer.parseInt(getString(R.string.default_setting)));
        int month = sharedPref.getInt(getString(R.string.time_period_month),
                Integer.parseInt(getString(R.string.default_setting)));




        EditText output;
        output = (EditText) findViewById(R.id.edit_seconds);
        output.setText(String.valueOf((second)));
        output = (EditText) findViewById(R.id.edit_minutes);
        output.setText(String.valueOf((minute)));
        output = (EditText) findViewById(R.id.edit_hours);
        output.setText(String.valueOf((hour)));
        output = (EditText) findViewById(R.id.edit_days);
        output.setText(String.valueOf((day)));
        output = (EditText) findViewById(R.id.edit_weeks);
        output.setText(String.valueOf((week)));
        output = (EditText) findViewById(R.id.edit_months);
        output.setText(String.valueOf((month)));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                saveSettings();
                break;
            default:
                resetSetting(v.getId());
                break;
        }
    }

    private void saveSettings() {
        EditText input;
        input = (EditText) findViewById(R.id.edit_seconds);
        int second = Integer.parseInt(String.valueOf(input.getText()));
        input = (EditText) findViewById(R.id.edit_minutes);
        int minute = Integer.parseInt(String.valueOf(input.getText()));
        input = (EditText) findViewById(R.id.edit_hours);
        int hour = Integer.parseInt(String.valueOf(input.getText()));
        input = (EditText) findViewById(R.id.edit_days);
        int day = Integer.parseInt(String.valueOf(input.getText()));
        input = (EditText) findViewById(R.id.edit_weeks);
        int week = Integer.parseInt(String.valueOf(input.getText()));
        input = (EditText) findViewById(R.id.edit_months);
        int month = Integer.parseInt(String.valueOf(input.getText()));
        long time = getMiliseconds(second, minute, hour, day, week, month);


        final Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.service_settings), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(getString(R.string.time_period_second), second);
        editor.putInt(getString(R.string.time_period_minute), minute);
        editor.putInt(getString(R.string.time_period_hour), hour);
        editor.putInt(getString(R.string.time_period_day), day);
        editor.putInt(getString(R.string.time_period_week), week);
        editor.putInt(getString(R.string.time_period_month), month);
        editor.putLong(getString(R.string.time_period), time);
        editor.apply();
    }

    private long getMiliseconds(int second, int minute, int hour, int day, int week, int month) {
        return (((((month * 30) + (week * 7) + day) * 24 + hour) * 60 + minute) * 60 + second) * 1000;

    }

    private void resetSetting(int buttonId) {
        EditText editText;
        switch (buttonId) {
            case R.id.button_seconds:
                editText = (EditText) findViewById(R.id.edit_seconds);
                editText.setText(R.string.default_setting);
                break;
            case R.id.button_minutes:
                editText = (EditText) findViewById(R.id.edit_minutes);
                editText.setText(R.string.default_setting);
                break;
            case R.id.button_hours:
                editText = (EditText) findViewById(R.id.edit_hours);
                editText.setText(R.string.default_setting);
                break;
            case R.id.button_days:
                editText = (EditText) findViewById(R.id.edit_days);
                editText.setText(R.string.default_setting);
                break;
            case R.id.button_weeks:
                editText = (EditText) findViewById(R.id.edit_weeks);
                editText.setText(R.string.default_setting);
                break;
            case R.id.button_months:
                editText = (EditText) findViewById(R.id.edit_months);
                editText.setText(R.string.default_setting);
                break;
        }
    }
}
