package com.acando.newshunter.content;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.acando.newshunter.internal.ListActivity;

import java.util.ArrayList;

public class LoadArticlesTask extends AsyncTask<String, Void, ArrayList<Article>> {

    private Context mContext;

    public LoadArticlesTask(Context context) {
        mContext = context;
    }

    @Override
    protected ArrayList<Article> doInBackground(String... params) {
        ArrayList<Article> items = new ArrayList<>();

        try {
            items = ArticleRetriever.retrieveArticles(mContext);
        } catch (Exception e) {
            Log.e(LoadArticlesTask.class.getName(), e.getMessage());
        }

        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<Article> list) {
        super.onPostExecute(list);
        ((ListActivity) mContext).loadList(list);
    }
}
