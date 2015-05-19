package com.jok.archwizacja_sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class DbAccess {

    private final Uri SMS_URI = Uri.parse("content://sms/");
//    private final Uri MMS_URI = Uri.parse("content://mms/");
    private final Uri PPL_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private final Uri THREAD_URI = Uri.parse("content://sms/conversations");
    private Context ctx;

    private Cursor pplC;
    private Cursor threadC;
    private Map<Integer, Cursor> smsM;

    private SmsData[] smsData;
//    private ThreadData threadData;
    private ContactData[] contactData;

    public DbAccess(final Context ctx) {
        this.ctx = ctx;
        pplC = getContacts();
        threadC = getThreads();
        smsM = new HashMap<Integer, Cursor>(threadC.getCount());
        smsData = prepareSmsData();
      }

    private Cursor getContacts() {
        Cursor c;
        if (pplC == null) {
            String[] mProjection = { "contact_id", "data1", "data2", "data4", "display_name" };
            c = ctx.getContentResolver().query(PPL_URI, mProjection, null, null, null);
        }
        else {
            c = pplC;
        }
        return c;
    }

    private Cursor getContactsByAddress(String address) {
        String[] mProjection = { "data4", "display_name" };
        String mSelection = "data4=?";
        String[] mSelectionArgs = { address };

        return ctx.getContentResolver().query(PPL_URI, mProjection, mSelection, mSelectionArgs, null);
    }

    private Cursor getThreads() {
        Cursor c;
        if (threadC == null) {
            String[] mProjection = { "thread_id" };
            c = ctx.getContentResolver().query(THREAD_URI, mProjection, null, null, null);
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
            if (tmp_data[j] != null) {
                Cursor c = getContactsByAddress(tmp_data[j]);
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        if (c.getType(c.getColumnIndex("display_name")) != Cursor.FIELD_TYPE_NULL) {
                            data[j] = c.getString(c.getColumnIndex("display_name"));
                            break;
                        }
                    }
                }
                else {
                    data[j] = tmp_data[j];
                }
                c.close();
            }
            else {
                data[j] = tmp_data[j];
            }
        }

        return data;
    }

    private Cursor getThreadSmses(int thread) {
        Cursor c;
        if (smsM.containsKey(thread)) {
            c = smsM.get(thread);
        }
        else {
            String[] mProjection = {"thread_id", "address", "date", "type", "body"};
            String mSelectionClause = "thread_id=" + String.valueOf(thread);
            //    String mSelectionArgs[] = { "thread_id", String.valueOf(thread) };
            c = ctx.getContentResolver().query(SMS_URI, mProjection, mSelectionClause, null, null);
        }
        return c;
    }


    SmsData[] prepareSmsData() {
        SmsData[] data;
        Cursor c = ctx.getContentResolver().query(SMS_URI, null, null, null, null);
        if (c.getCount() > 0) {
            int app_id = 1;
            data = new SmsData[c.getCount()];
            while (c.moveToNext()) {
                data[app_id-1] = getDataFromCursorRow(c, app_id);
                app_id++;
            }
        }
        else {
            data = null;
        }
        c.close();
        return data;
    }

    SmsData getDataFromCursorRow(Cursor c, int app_id) {
        return new SmsData(app_id,
                c.getInt(c.getColumnIndex("_id")),
                c.getInt(c.getColumnIndex("thread_id")),
                (c.getColumnIndex("m_size") != -1) ? c.getInt(c.getColumnIndex("m_size")) : null,
                c.getInt(c.getColumnIndex("person")),
                c.getInt(c.getColumnIndex("date")),
                c.getInt(c.getColumnIndex("date_sent")),
                c.getInt(c.getColumnIndex("protocol")),
                c.getInt(c.getColumnIndex("read")),
                c.getInt(c.getColumnIndex("status")),
                c.getInt(c.getColumnIndex("type")),
                c.getInt(c.getColumnIndex("reply_path_present")),
                c.getInt(c.getColumnIndex("locked")),
                (c.getColumnIndex("sim_id") != -1) ? c.getInt(c.getColumnIndex("sim_id")) : null,
                c.getInt(c.getColumnIndex("error_code")),
                c.getInt(c.getColumnIndex("seen")),
                (c.getColumnIndex("star") != -1) ? c.getInt(c.getColumnIndex("star")) : null,
                (c.getColumnIndex("pri") != -1) ? c.getInt(c.getColumnIndex("pri")) : null,
                c.getString(c.getColumnIndex("address")),
                c.getString(c.getColumnIndex("body")),
                c.getString(c.getColumnIndex("service_center")));
    }

    // TODO: unifikacja do pojedyńczych rekordów
    ContactData[] prepareContactData() {
        ContactData[] data;
        String[] mProjection = { "contact_id", "data4", "display_name" };
        Cursor c = ctx.getContentResolver().query(PPL_URI, mProjection, null, null, null);
        if (c.getCount() > 0) {
            int app_id = 1;
            data = new ContactData[c.getCount()];
            while (c.moveToNext()) {
                data[app_id-1] = new ContactData(app_id,
                        c.getInt(c.getColumnIndex("contact_id")),
                        c.getString(c.getColumnIndex("data4")),
                        c.getString(c.getColumnIndex("display_name")));
                app_id++;
            }
        }
        else {
            data = null;
        }
        c.close();
        return data;
    }

    String[] getSmsesBody(int t_id) {
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
        pplC.close();
        for (Map.Entry<Integer, Cursor> entry : smsM.entrySet()) {
            entry.getValue().close();
        }
    }

    public SmsData[] getSmsData() {
        return smsData;
    }

    public ContactData[] getContactData() {
        return contactData;
    }
}
