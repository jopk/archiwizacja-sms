package com.jok.archwizacja_sms;


import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class SmsXmlParser {
    private static final String ns = null;

    private Context ctx = null;

    public SmsXmlParser(Context ctx) {
        this.ctx = ctx;
    }

    public SMS.Data parse(String file) throws XmlPullParserException, IOException {
        StringReader sr = new StringReader(file);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(sr);
            parser.nextTag();
            return readSms(parser);
        } finally {
            sr.close();
        }
    }

    private SMS.Data readSms(XmlPullParser parser) throws XmlPullParserException, IOException {
        String date = null, date_sent = null, id = null, thread_id = null, m_size = null, person = null, protocol = null, read = null, status = null, type = null, reply_path_present = null, locked = null, sim_id = null, error_code = null, seen = null, star = null, pri = null, address = null, subject = null, body = null, service_center = null;
        parser.require(XmlPullParser.START_TAG, ns, "sms");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            switch (name) {
                case SMS.ID:
                    id = readText(parser, name);
                    break;
                case SMS.THREAD_ID:
                    thread_id = readText(parser, name);
                    break;
                case SMS.M_SIZE:
                    m_size = readText(parser, name);
                    break;
                case SMS.PERSON:
                    person = readText(parser, name);
                    break;
                case SMS.DATE:
                    date = readText(parser, name);
                    break;
                case SMS.DATE_SENT:
                    date_sent = readText(parser, name);
                    break;
                case SMS.PROTOCOL:
                    protocol = readText(parser, name);
                    break;
                case SMS.READ:
                    read = readText(parser, name);
                    break;
                case SMS.STATUS:
                    status = readText(parser, name);
                    break;
                case SMS.TYPE:
                    type = readText(parser, name);
                    break;
                case SMS.REPLY_PATH_PRESENT:
                    reply_path_present = readText(parser, name);
                    break;
                case SMS.LOCKED:
                    locked = readText(parser, name);
                    break;
                case SMS.SIM_ID:
                    sim_id = readText(parser, name);
                    break;
                case SMS.ERROR_CODE:
                    error_code = readText(parser, name);
                    break;
                case SMS.SEEN:
                    seen = readText(parser, name);
                    break;
                case SMS.STAR:
                    star = readText(parser, name);
                    break;
                case SMS.PRI:
                    pri = readText(parser, name);
                    break;
                case SMS.ADDRESS:
                    address = readText(parser, name);
                    break;
                case SMS.SUBJECT:
                    subject = readText(parser, name);
                    break;
                case SMS.BODY:
                    body = readText(parser, name);
                    break;
                case SMS.SERVICE_CENTER:
                    service_center = readText(parser, name);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new SMS.Data(id, thread_id, address, m_size, person, date, date_sent, protocol, read, status, type, reply_path_present, subject, body, service_center, locked, sim_id, error_code, seen, star, pri);
    }

    private String readText(XmlPullParser parser, String name) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, name);
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, name);
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
