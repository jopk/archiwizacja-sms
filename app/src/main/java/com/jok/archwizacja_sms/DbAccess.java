package com.jok.archwizacja_sms;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DbAccess {

    private final String smsAcc = "content://sms/";
    private final String threadAcc = "content://sms/conversations";

    private Uri smsUri = Uri.parse(smsAcc);
    private Uri threadUri = Uri.parse(threadAcc);
    private Context ctx;

    private Cursor threadCursor;

    public DbAccess(final Context ctx) {
        this.ctx = ctx;
        this.threadCursor = getThreads();
    }

    private Cursor getThreadSmses(int thread) {
        String[] mProjection = { "thread_id", "address", "date", "date_sent", "type", "body" };
        String mSelectionClause = "thread_id=" + String.valueOf(thread);
    //    String mSelectionArgs[] = { "thread_id", String.valueOf(thread) };
        return ((Activity) ctx).getContentResolver().query(smsUri, mProjection, mSelectionClause, null, null);
    }

    private Cursor getThreads() {
        String[] columns = { "thread_id" };
        return ((Activity) ctx).getContentResolver().query(threadUri, columns, null, null, null);
    }

    public String getDate(long ts) {
        try {
            DateFormat df = new SimpleDateFormat("HH:mm:ss, dd.MM.yyyy");
            Date myDate = new Date(ts);
            return df.format(myDate);
        } catch (Exception e) {
            return "error";
        }
    }

    int[] getThreadsIds() {
        int[] ids;
        threadCursor.moveToFirst();
        ids = new int[threadCursor.getCount()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = threadCursor.getInt(threadCursor.getColumnIndex("thread_id"));
            threadCursor.moveToNext();
        }
        return ids;
    }
    
    String[] getThreadData() {
        threadCursor.moveToFirst();
        String[] data = new String[threadCursor.getCount()];
        for (int i = 0; i < data.length; i++)
        {
            int t_id = threadCursor.getInt(threadCursor.getColumnIndex("thread_id"));
            Cursor smsC = getThreadSmses(t_id);
            while (smsC.moveToNext()) {
                if (smsC.getInt(smsC.getColumnIndex("type")) == 1) // inbox
                    break;
            }
            try {
                data[i] = smsC.getString(smsC.getColumnIndex("address"));
            }
            catch (Exception e) {
                data[i] = String.valueOf(smsC.getCount());
            }
            smsC.close();
            threadCursor.moveToNext();

        }
        return data;
    }
    
    String[] getSmsesData(int t_id) {
        String[] data;
        Cursor c = getThreadSmses(t_id);
        c.moveToFirst();
        data = new String[c.getCount()];
        for (int i = 0; i < data.length; i++) {
            data[i] = c.getString(c.getColumnIndex("body"));
            c.moveToNext();
        }
        c.close();
        return data;
    }

    public void close() {
        threadCursor.close();
    }
}
