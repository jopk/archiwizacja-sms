package com.jok.archwizacja_sms;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.ArrayMap;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class DbAccess {

    public static final Uri SMS_URI = Uri.parse("content://sms/");
    public static final Uri THREAD_URI = Uri.parse("content://sms/conversations");
    public static final Uri PPL_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    private Context ctx;

    public DbAccess(final Context ctx) {
        this.ctx = ctx;
    }

    String[] getSmsXml(long date, int thread_id) {
        Uri uri= SMS_URI;
        String tag = "sms";
        String mSelection = "date > ? AND thread_id = ?";
        String[] mSelectionArgs = { String.valueOf(date), String.valueOf(thread_id) };
        return getXml(uri, tag, mSelection, mSelectionArgs);
    }

    String[] getContactXml(String address) {
        Uri uri= PPL_URI;
        String tag = "contact";
        String mSelection = "data4 = ?";
        String[] mSelectionArgs = { address };
        return getXml(uri, tag, mSelection, mSelectionArgs);
    }

    String[] getXml(Uri uri, String tag, String mSelection, String[] mSelectionArgs) {
        String[] data;
        Cursor cursor = ctx.getContentResolver().query(uri, null, mSelection, mSelectionArgs, null);
        if (cursor.getCount() > 0) {
            int app_id = 1;
            data = new String[cursor.getCount()];
            try {
                while (cursor.moveToNext()) {
                    StringWriter writer = new StringWriter();
                    XmlSerializer serializer = Xml.newSerializer();
                    serializer.setOutput(writer);
                    serializer.startDocument("UTF-8", true);
                    serializer.startTag("", tag);
                    serializer.startTag("", "app_id");
                    serializer.attribute("", "type", String.valueOf(Cursor.FIELD_TYPE_INTEGER));
                    serializer.text(String.valueOf(app_id));
                    serializer.endTag("", "app_id");
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        serializer.startTag("", cursor.getColumnName(i));
                        serializer.attribute("", "type", String.valueOf(cursor.getType(i)));
                        if (cursor.getType(i) == Cursor.FIELD_TYPE_NULL) {
                            serializer.text("null");
                        }
                        else {
                            try {
                                serializer.text(cursor.getString(i));
                            } catch (Exception e) {
                                serializer.text("BŁĄD: Ten rekord nie może zostać poprawnie zarchiwizowany.");
                            }
                        }
                        serializer.endTag("", cursor.getColumnName(i));
                    }
                    serializer.endTag("", tag);
                    serializer.endDocument();
                    data[app_id - 1] = writer.toString();
                    app_id++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            data = null;
        }
        cursor.close();
        return data;
    }

    public boolean restoreSms(LinkedList<ArrayMap<String, String>> list) {
        ListIterator<ArrayMap<String, String>> iterator = list.listIterator();
        while (iterator.hasNext()) {
            ArrayMap<String, String> tmp = iterator.next();
            if (tmp == null) {
                iterator.remove();
                continue;
            }
            String[] mProjection = { Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY, Telephony.Sms.TYPE };
            String mSelection = "address=? AND date=? AND body=? AND type=?";
            String[] mSelectionArgs = { tmp.get(Telephony.Sms.ADDRESS), tmp.get(Telephony.Sms.DATE), tmp.get(Telephony.Sms.BODY), tmp.get(Telephony.Sms.TYPE) };
            Cursor c = ctx.getContentResolver().query(SMS_URI, mProjection, mSelection, mSelectionArgs, null);
            if (c.getCount() > 0)
                iterator.remove();
            c.close();
        }
        Cursor c = ctx.getContentResolver().query(SMS_URI, null, null, null, null);
        for (ArrayMap<String, String> map : list) {
            ContentValues values = new ContentValues();
            for (int i = 0; i < c.getColumnCount(); i++) {
                String name = c.getColumnName(i);
                String val = map.get(name);
                if (!val.equals("null")) {
                    values.put(name, val);
                }
            }
            ctx.getContentResolver().insert(DbAccess.SMS_URI, values);
        }
        c.close();
        return true;
    }

    String getAddressByThreadId(int id) {
        Cursor smsC = getThreadSmses(id);
        String address = null;
        smsC.moveToNext();
        try {
            address = smsC.getString(smsC.getColumnIndex("address"));
        }
        catch (Exception e) {
            address = null;
        }
        smsC.close();
        return address;
    }

    private Cursor getThreads() {
        Cursor c;
        String[] mProjection = { "thread_id" };
        c = ctx.getContentResolver().query(THREAD_URI, mProjection, null, null, null);
        return c;
    }

    int[] getThreadsIds() {
        Cursor threadC = getThreads();
        threadC.moveToFirst();
        int[] ids = new int[threadC.getCount()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = threadC.getInt(threadC.getColumnIndex("thread_id"));
            threadC.moveToNext();
        }
        threadC.close();
        return ids;
    }

    private Cursor getThreadSmses(int thread) {
        Cursor c;
        String[] mProjection = {"thread_id", "address", "date", "type", "body"};
        String mSelectionClause = "thread_id=" + String.valueOf(thread);
        c = ctx.getContentResolver().query(SMS_URI, mProjection, mSelectionClause, null, null);
        return c;
    }

    String[] getSmsesBody(int t_id) {
        String[] data;
        Cursor smsC = getThreadSmses(t_id);
        smsC.moveToFirst();
        data = new String[smsC.getCount()];
        for (int i = 0; i < data.length; i++) {
            data[i] = smsC.getString(smsC.getColumnIndex("body"));
            smsC.moveToNext();
        }
        smsC.close();
        return data;
    }

    private Cursor getContactsByAddress(String address) {
        String[] mProjection = { "data4", "display_name" };
        String mSelection = "data4=?";
        String[] mSelectionArgs = { address };
        return ctx.getContentResolver().query(PPL_URI, mProjection, mSelection, mSelectionArgs, null);
    }

    String[] getContactsNames() {
        Cursor threadC = getThreads();
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
            smsC.close();

        }
        threadC.close();
        String[] data = new String[tmp_data.length];
        for (int j = 0; j < data.length; j++) {
            if (tmp_data[j] != null) {
                Cursor pplC = getContactsByAddress(tmp_data[j]);
                if (pplC.getCount() > 0) {
                    while (pplC.moveToNext()) {
                        if (pplC.getType(pplC.getColumnIndex("display_name")) != Cursor.FIELD_TYPE_NULL) {
                            data[j] = pplC.getString(pplC.getColumnIndex("display_name"));
                            break;
                        }
                    }
                }
                else {
                    data[j] = tmp_data[j];
                }
                pplC.close();
            }
            else {
                data[j] = tmp_data[j];
            }
        }
        return data;
    }

    public static String formatDate(long ts) {
        try {
            DateFormat df = new SimpleDateFormat("HH:mm:ss, dd.MM.yyyy");
            Date myDate = new Date(ts);
            return df.format(myDate);
        } catch (Exception e) {
            return "error";
        }
    }
}
