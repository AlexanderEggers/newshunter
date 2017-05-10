package com.acando.newshunter.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.acando.newshunter.content.GuardianXMLParser;
import com.acando.newshunter.content.NewsEntry;
import com.acando.newshunter.UtilNetwork;
import com.acando.newshunter.database.table.FeedTable;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class RemoteFetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ArrayList<NewsEntry> listItemList;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        fetchDataFromWeb();
        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchDataFromWeb() {
        listItemList = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GuardianXMLParser xmlParser = new GuardianXMLParser();
                    ArrayList<NewsEntry> temp = xmlParser.parse(UtilNetwork.downloadUrl("https://www.theguardian.com/world/rss"));

                    for(int i = 0; i <= 10; i++) {
                        listItemList.add(temp.get(i));
                    }
                } catch (IOException|XmlPullParserException e) {
                    Log.e(RemoteFetchService.class.getName(), "" + e.getMessage());
                }

                FeedTable.removeAll(getApplicationContext());
                FeedTable.insertAll(getApplicationContext(), listItemList);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                populateWidget();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void populateWidget() {
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(WidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        sendBroadcast(widgetUpdateIntent);
        this.stopSelf();
    }
}
