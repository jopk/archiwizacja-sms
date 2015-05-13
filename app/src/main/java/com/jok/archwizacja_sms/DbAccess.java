package com.jok.archwizacja_sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.ArrayMap;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class DbAccess {

    private final Uri SMS_URI = Uri.parse("content://sms/");
    private final Uri MMS_URI = Uri.parse("content://mms/");
    private final Uri PPL_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private final Uri THREAD_URI = Uri.parse("content://sms/conversations");
    private Context ctx;

    private  Cursor pplC;
    private Cursor threadC;
    private Map<Integer, Cursor> smsM;

    public DbAccess(final Context ctx) {
        this.ctx = ctx;
        pplC = getContacts();
        threadC = getThreads();
        smsM = new HashMap<Integer, Cursor>(threadC.getCount());
    }

    private Cursor getContacts() {
        Cursor c;
        if (pplC == null) {
            String[] columns = { "contact_id", "data1", "data2", "data4", "display_name" };
            c = ctx.getContentResolver().query(PPL_URI, columns, null, null, null);
        }
        else {
            c = pplC;
        }
        return c;
    }

    private Cursor getThreads() {
        Cursor c;
        if (threadC == null) {
            String[] columns = { "thread_id" };
            c = ctx.getContentResolver().query(THREAD_URI, columns, null, null, null);
        }
        else {
            c = threadC;
        }
        return c;
    }

    int[] getThreadsIds() {
        if (threadC == null) {
            threadC = getThreads();
        }
        threadC.moveToFirst();
        int[] ids = new int[threadC.getCount()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = threadC.getInt(threadC.getColumnIndex("thread_id"));
            threadC.moveToNext();
        }
        return ids;
    }

    String[] getContactsNames() {
        if (threadC == null) {
            threadC = getThreads();
        }
        if (pplC == null) {
            pplC = getContacts();
        }
        threadC.moveToFirst();
        String[] tmp_data = new String[threadC.getCount()];
        for (int i = 0; i < tmp_data.length; i++)
        {
            int t_id = threadC.getInt(threadC.getColumnIndex("thread_id"));
            Cursor smsC = getThreadSmses(t_id);
            while (smsC.moveToNext()) {
                if (smsC.getInt(smsC.getColumnIndex("type")) == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX) // inbox
                    break;
            }
            try {
                tmp_data[i] = smsC.getString(smsC.getColumnIndex("address"));
            }
            catch (Exception e) {
                tmp_data[i] = null;
            }
            threadC.moveToNext();

        }
        String[] data = new String[tmp_data.length];
        for (int j = 0; j < data.length; j++) {
            pplC.moveToFirst();
            do {
                if (pplC.getType(pplC.getColumnIndex("data4")) != Cursor.FIELD_TYPE_NULL) {
                    if (pplC.getString(pplC.getColumnIndex("data4")).equals(tmp_data[j])) {
                        data[j] = pplC.getString(pplC.getColumnIndex("display_name"));
                        break;
                    }
                }
                else {
                    data[j] = tmp_data[j];
                }
            } while (pplC.moveToNext());
        }

        return data;
    }

    private Cursor getThreadSmses(int thread) {
        Cursor c;
        if (smsM.containsKey(thread)) {
            c = smsM.get(thread);
        }
        else {
            String[] mProjection = {"thread_id", "address", "date", "date_sent", "type", "body"};
            String mSelectionClause = "thread_id=" + String.valueOf(thread);
            //    String mSelectionArgs[] = { "thread_id", String.valueOf(thread) };
            c = ctx.getContentResolver().query(SMS_URI, mProjection, mSelectionClause, null, null);
        }
        return c;
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
        return data;
    }

    public String formatDate(long ts) {
        try {
            DateFormat df = new SimpleDateFormat("HH:mm:ss, dd.MM.yyyy");
            Date myDate = new Date(ts);
            return df.format(myDate);
        } catch (Exception e) {
            return "error";
        }
    }

    public void close() {
        threadC.close();
        for (Map.Entry<Integer, Cursor> entry : smsM.entrySet()) {
            entry.getValue().close();
        }
    }
}
