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
    private int[] checked;

    public MyListAdapter(final Activity ctx, final String[] data) {
        super(ctx, R.layout.thread_layout, data);
        this.ctx = ctx;
        this.data = data;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = ctx.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.thread_layout, null, true);
        TextView address = (TextView) rowView.findViewById(R.id.address);
        CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBox);
        checked = new int[data.length];
        for(int i=0;i<checked.length;i++){
            checked[i]=1;
        }
        cb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                CheckBox checkbox = (CheckBox) v;
                if (checkbox.isChecked()){
                    checked[position]=1;
                }
                if (!(checkbox.isChecked())){
                    checked[position]=-1;
                }
            }
        });
        address.setText(data[position]);
        return rowView;
    }
    public int[] getChecked(){
        return checked;
    }
}
