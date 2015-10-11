package com.example.artsi.rssreader2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private PostData[] listData;
    private String finalUrl = "http://www.iltalehti.fi/rss/digi.xml";
    private HandleXML obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);

        this.generateDummyData();

        ListView listView = (ListView) this.findViewById(R.id.postListView);
        PostItemAdapter itemAdapter = new PostItemAdapter(this,
                R.layout.postitem, listData);
        listView.setAdapter(itemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void generateDummyData() {
        PostData data = null;
        listData = new PostData[1];

        obj = new HandleXML(finalUrl);
        obj.fetchXML();
        int i = 0;
        while (!obj.parsingComplete) ;

        data = new PostData();
        data.postLink = obj.getLink();
        data.postTitle = obj.getTitle();
        data.postDescription = obj.getDescription();
        data.postThumbUrl = null;
        listData[i] = data;
        i++;

    }
}