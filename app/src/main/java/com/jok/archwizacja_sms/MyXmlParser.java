package com.jok.archwizacja_sms;


import android.util.ArrayMap;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class MyXmlParser {

    private static final String ns = null;

    public MyXmlParser() {}

    public ArrayMap<String, String> parse(String file) throws XmlPullParserException, IOException {
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

    private ArrayMap<String, String> readSms(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "sms");
        ArrayMap<String, String> smsData = new ArrayMap<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            smsData.put(name, readText(parser, name));
        }
        return smsData;
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

}
