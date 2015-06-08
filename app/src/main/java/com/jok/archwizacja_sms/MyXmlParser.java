package com.jok.archwizacja_sms;


import android.util.ArrayMap;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class MyXmlParser {

    private final String tag;
    private static final String ns = null;

    public MyXmlParser(String tag) {
        this.tag = tag;
    }

    /**
     * Czyta poprawnie sformatowany dokument xml, na niepoprawnie kończy pracę wyjątkiem.
     */
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

    /**
     * Nazwa archaiczna, czyta po prostu wszystko.
     */
    private ArrayMap<String, String> readSms(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        ArrayMap<String, String> data = new ArrayMap<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            data.put(name, readText(parser, name));
        }
        return data;
    }

    /**
     * Pobiera zawartość tagu: <tag>zawartość</tag>
     */
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
