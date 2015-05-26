package com.jok.archwizacja_sms;


import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class BaseSmsXmlParser {
    private static final String ns = null;

    public SMS.Data parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readSms(parser);
        } finally {
            in.close();
        }
    }

    private SMS.Data readSms(XmlPullParser parser) throws XmlPullParserException, IOException {
        int id = 0, thread_id = 0, date = 0, type = 0;
        String address = null, body = null;
        parser.require(XmlPullParser.START_TAG, ns, "sms");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            switch (name) {
                case SMS.ID:
                    id = readInt(parser, name);
                    break;
                case SMS.THREAD_ID:
                    thread_id = readInt(parser, name);
                    break;
                case SMS.ADDRESS:
                    address = readString(parser, name);
                    break;
                case SMS.DATE:
                    date = readInt(parser, name);
                    break;
                case SMS.TYPE:
                    type = readInt(parser, name);
                    break;
                case SMS.BODY:
                    body = readString(parser, name);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new SMS.Data(id, thread_id, address, date, type, body);
    }

    private int readInt(XmlPullParser parser, String name) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, name);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, name);
        return Integer.parseInt(text);
    }

    private String readString(XmlPullParser parser, String name) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, name);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, name);
        return text;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
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
