package com.example.artsi.rssreader2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Artsi on 11/10/15.
 */

public class HandleXML {
    private String title = "title";
    private String link = "link";
    private String description = "description";
    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = false;
    ArrayList postDataList = new ArrayList();

    public HandleXML(String url) {
        this.urlString = url;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }


    public String getDescription() {
        return description;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        boolean insideItem = false;

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {

                if (event == XmlPullParser.START_TAG) {
                    if (myParser.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (myParser.getName().equalsIgnoreCase("url")) {
                        insideItem = true;
                    } else if (myParser.getName().equalsIgnoreCase("title")) {
                        if (insideItem)
                            this.title = myParser.nextText();
                    } else if (myParser.getName().equalsIgnoreCase("link")) {
                        if (insideItem)
                            this.link = myParser.nextText() + "\n\n";
                    } else if (myParser.getName().equalsIgnoreCase("description")) {
                        if (insideItem)
                            this.description = myParser.nextText() + "\n";
                    }
                } else if (event == XmlPullParser.END_TAG && myParser.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                }
                postDataList.add(event);
                event = myParser.next();

            }
            parsingComplete = true;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void fetchXML() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();
                } catch (Exception e) {
                }
            }
        });
        thread.start();
    }
}