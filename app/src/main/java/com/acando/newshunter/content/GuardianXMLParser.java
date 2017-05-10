package com.acando.newshunter.content;

import android.text.Html;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GuardianXMLParser {

    private static final String ns = null;

    public ArrayList<NewsEntry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<NewsEntry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<NewsEntry> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private NewsEntry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null, link = null, pubDate = null, desc = null, image = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "title":
                    title = readTag(parser, "title");
                    break;
                case "description":
                    desc = Html.fromHtml(readTag(parser, "description")).toString().replaceAll("\n", "").trim();
                    break;
                case "link":
                    link = readTag(parser, "link");
                    break;
                case "pubDate":
                    pubDate = readTag(parser, "pubDate");
                    break;
                case "media:content":
                    image = readImage(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new NewsEntry(title, desc, link, pubDate, image);
    }

    // Processes general tags in the feed.
    private String readTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return title;
    }

    // Processes image tags in the feed.
    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        String url = "";
        parser.require(XmlPullParser.START_TAG, ns, "media:content");
        if(parser.getAttributeValue(0).equals("460")) {
            url = parser.getAttributeValue(1);
        }
        skip(parser);
        return url;
    }

    // For the tags title and summary, extracts their text values.
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
