package com.jok.archwizacja_sms;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<String> {

    private Activity ctx;
    private String[] data;
    private boolean[] checked;

    public MyListAdapter(final Activity ctx, final String[] data) {
        super(ctx, R.layout.thread_layout, data);
        this.ctx = ctx;
        this.data = data;
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        //ViewHolder holder = null;
        LayoutInflater inflater = ctx.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.thread_layout, null, true);
        TextView address = (TextView) rowView.findViewById(R.id.address);
        CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBox);
        checked = new boolean[data.length];
        for(int i=0;i<data.length;i++){
            checked[i]=true;
        }
        cb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                CheckBox checkbox = (CheckBox) v;
                if (checkbox.isChecked()){
                    checked[position]=true;
                }
                if (!(checkbox.isChecked())){
                    checked[position]=false;
                }
            }
        });
        address.setText(data[position]);
        return rowView;
    }
    public boolean[] getChecked(){
        return checked;
    }
}
