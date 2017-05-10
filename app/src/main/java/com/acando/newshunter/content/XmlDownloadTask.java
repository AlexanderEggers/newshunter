package com.acando.newshunter.content;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.acando.newshunter.UtilNetwork;
import com.acando.newshunter.internal.ListActivity;

import java.util.ArrayList;

public class XmlDownloadTask extends AsyncTask<String, Void, ArrayList<NewsEntry>> {

    private Context mContext;

    public XmlDownloadTask(Context context) {
        mContext = context;
    }

    @Override
    protected ArrayList<NewsEntry> doInBackground(String... params) {
        ArrayList<NewsEntry> items = new ArrayList<>();

        try {
            GuardianXMLParser xmlParser = new GuardianXMLParser();
            items = xmlParser.parse(UtilNetwork.downloadUrl(params[0]));
        } catch (Exception e) {
            Log.e(XmlDownloadTask.class.getName(), e.getMessage());
        }

        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsEntry> list) {
        super.onPostExecute(list);
        ((ListActivity) mContext).loadList(list);
    }
}
