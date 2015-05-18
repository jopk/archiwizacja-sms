package com.jok.archwizacja_sms;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

/**
 * Created by damian on 15.05.15.
 */
public class SmsBackup extends Fragment{
    public int sleep_timer= 100;
    public Switch swch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.smsbackup_layout,container,false);
        swch = (Switch) view.findViewById(R.id.switch1);

        return view;
    }

    public void checkSwitch(View view){
       if(swch.isChecked()){

       }
    }
}
