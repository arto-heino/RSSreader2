package com.example.artsi.rssreader2;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by Artsi on 12/10/15.
 */
public class PostViewActivity extends Activity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.postview);
        this.webView = (WebView) this.findViewById(R.id.webview);

        Bundle bundle = this.getIntent().getExtras();

        String postContent = bundle.getString("content");
        webView.loadData(postContent, "text/html; charset=utf-8", "utf-8");
    }


}
