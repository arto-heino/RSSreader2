package com.example.artsi.rssreader2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {
    private enum RSSXMLTag {
        TITLE, DATE, LINK, IGNORETAG, DESC;
    }

    private ArrayList<PostData> listData;
    private String urlString = "http://www.iltalehti.fi/rss/uutiset.xml";
    private ListView postListView;
    private PostItemAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);

        new RssDataController().execute(urlString);

        listData = new ArrayList<PostData>();
        postListView = (ListView) this.findViewById(R.id.postListView);
        postAdapter = new PostItemAdapter(this, R.layout.postitem, listData);
        postListView.setAdapter(postAdapter);
        postListView.setOnItemClickListener(onItemClickListener);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PostData data = listData.get(arg2 - 1);

            Bundle postInfo = new Bundle();
            postInfo.putString("content", data.postContent);

            Intent postviewIntent = new Intent(MainActivity.this, PostViewActivity.class);
            postviewIntent.putExtras(postInfo);
            startActivity(postviewIntent);
        }
    };

    public void refresh(View view) {
        recreate();
    }

    public void liiga(View view){
        String urlString = "http://www.iltalehti.fi/rss/jaakiekko.xml";

        new RssDataController().execute(urlString);

                listData = new ArrayList<PostData>();
                postListView = (ListView) this.findViewById(R.id.postListView);
                postAdapter = new PostItemAdapter(this, R.layout.postitem, listData);
                postListView.setAdapter(postAdapter);
            }

    public void fishing(View view){
        String urlString = "http://www.kalamies.com/kalastus-uutiset?format=feed&type=rss";

        new RssDataController().execute(urlString);

        listData = new ArrayList<PostData>();
        postListView = (ListView) this.findViewById(R.id.postListView);
        postAdapter = new PostItemAdapter(this, R.layout.postitem, listData);
        postListView.setAdapter(postAdapter);
    }

    public void homeland(View view){
        String urlString = "http://www.iltalehti.fi/rss/kotimaa.xml";

        new RssDataController().execute(urlString);

        listData = new ArrayList<PostData>();
        postListView = (ListView) this.findViewById(R.id.postListView);
        postAdapter = new PostItemAdapter(this, R.layout.postitem, listData);
        postListView.setAdapter(postAdapter);
    }

    public void hunting(View view){
        String urlString = "http://riista.fi/feed/rss/";

        new RssDataController().execute(urlString);

        listData = new ArrayList<PostData>();
        postListView = (ListView) this.findViewById(R.id.postListView);
        postAdapter = new PostItemAdapter(this, R.layout.postitem, listData);
        postListView.setAdapter(postAdapter);
    }

    public void it(View view){
        String urlString = "http://www.iltalehti.fi/rss/digi.xml";

        new RssDataController().execute(urlString);

        listData = new ArrayList<PostData>();
        postListView = (ListView) this.findViewById(R.id.postListView);
        postAdapter = new PostItemAdapter(this, R.layout.postitem, listData);
        postListView.setAdapter(postAdapter);
    }

    private class RssDataController extends AsyncTask<String, Integer, ArrayList<PostData>> {
        private RSSXMLTag currentTag;

        @Override
        protected ArrayList<PostData> doInBackground(String... params) {
            String urlStr = params[0];
            InputStream is = null;
            ArrayList<PostData> postDataList = new ArrayList<PostData>();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setReadTimeout(10 * 1000);
                connection.setConnectTimeout(10 * 1000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                Log.d("debug", "The response is: " + response);
                is = connection.getInputStream();

                // parse xml
                XmlPullParserFactory factory = XmlPullParserFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(is, null);

                int eventType = xpp.getEventType();
                PostData pdData = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z");
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {

                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("item")) {
                            pdData = new PostData();
                            currentTag = RSSXMLTag.IGNORETAG;
                        } else if (xpp.getName().equals("title")) {
                            currentTag = RSSXMLTag.TITLE;
                        } else if (xpp.getName().equals("link")) {
                            currentTag = RSSXMLTag.LINK;
                        } else if (xpp.getName().equals("pubDate")) {
                            currentTag = RSSXMLTag.DATE;
                        }else if (xpp.getName().equals("description")) {
                            currentTag = RSSXMLTag.DESC;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equals("item")) {
                            Date postDate = dateFormat.parse(pdData.postDate);
                            pdData.postDate = dateFormat.format(postDate);
                            postDataList.add(pdData);
                        } else {
                            currentTag = RSSXMLTag.IGNORETAG;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        String content = xpp.getText();
                        content = content.trim();
                        if (pdData != null) {
                            switch (currentTag) {
                                case TITLE:
                                    if (content.length() != 0) {
                                        if (pdData.postTitle != null) {
                                            pdData.postTitle += content;
                                        } else {
                                            pdData.postTitle = content;
                                        }
                                    }
                                    break;
                                case LINK:
                                    if (content.length() != 0) {
                                        if (pdData.postLink != null) {
                                            pdData.postLink += content;
                                        } else {
                                            pdData.postLink = content;
                                        }
                                    }
                                    break;
                                case DATE:
                                    if (content.length() != 0) {
                                        if (pdData.postDate != null) {
                                            pdData.postDate += content;
                                        } else {
                                            pdData.postDate = content;
                                        }
                                    }
                                    break;
                                case DESC:
                                    if (content.length() != 0) {
                                        if (pdData.postDesc != null) {
                                            pdData.postDesc += content;
                                        } else {
                                            pdData.postDesc = content;
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    eventType = xpp.next();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return postDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<PostData> result) {
            for (int i = 0; i < result.size(); i++) {
                listData.add(result.get(i));
            }
            postAdapter.notifyDataSetChanged();
        }
    }
}