package com.jok.archwizacja_sms;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.PrintWriter;


public class SmsBackup extends Fragment{
    public Switch swch;
    public Button bt;

    private int[] t_id;
    private DbAccess dba;
    private SmsData[] smsData;
    private ContactData[] contactData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.smsbackup_layout,container,false);
        swch = (Switch) view.findViewById(R.id.switch1);
        bt = (Button) view.findViewById(R.id.button);
        dba = new DbAccess(view.getContext());

        swch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkSwitch();
            }
        });
        return view;
    }

    public void checkSwitch(){
        if(swch.isChecked()){
            smsData = dba.prepareSmsData();
            String filepath;
            String filename;
            String filebody;
            int length = getNumsOfMsgs();
            int i;
            PrintWriter pw;
            try{
                for(i=0;i<length;i++){
                    filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/compress/";
                    filename = "sms"+i+".xml";
                    File file = new File(filepath+filename);
                    pw = new PrintWriter(file);
                    filebody = "<sms>\n\t<address>"+smsData[i].getAddress()+"</address>\n\t<person>"+smsData[i].getPerson()+
                            "</person>\n\t<date>"+smsData[i].getDate()+"</date>\n\t<body>"+smsData[i].getBody()+"</body>\n</sms>";
                    pw.write(filebody);
                    pw.flush();
                    pw.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{

        }
    }

    private int getNumsOfMsgs(){
        int length=0;
        try{
            while(smsData[length].get_id()!=null){
                length++;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return length;
    }
}
