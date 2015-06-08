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
        super(ctx, R.layout.thread_row, data);
        this.ctx = ctx;
        this.data = data;
        checked = new boolean[data.length];
        for (boolean x : checked) {
            x = true;
        }
    }

    /**
     * Google i internet twierdzi że to dobry wzorzec.
     * W zasadzie wygodny, bo zapewnia jednokrotną inicjalizację czyli że dane już są potem.
     * I łatwo się dzięki temu na nich operuje.
     */
    private static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }

    public View getView(final int position, View rowView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = ctx.getLayoutInflater();
            rowView = inflater.inflate(R.layout.thread_row, null, true);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) rowView.findViewById(R.id.address);
            viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
            rowView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.checkBox.setChecked(checked[position]);
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener(){
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
        viewHolder.textView.setText(data[position]);
        return rowView;
    }

    /**
     * potrzebne do sprawdzenia które wątki będą archiwizowane.
     */
    public boolean[] getChecked(){
        return checked;
    }
}
