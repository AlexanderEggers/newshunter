package com.acando.newshunter.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.acando.newshunter.content.Article;
import com.acando.newshunter.R;
import com.acando.newshunter.content.ViewImageTask;
import com.acando.newshunter.content.LoadArticlesTask;
import com.acando.newshunter.database.UtilDatabase;

import java.util.ArrayList;

public class ListActivity extends Activity {

    public ViewImageTask mImageTask;
    private ListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mImageTask = new ViewImageTask(this);
        mImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        UtilDatabase.checkDatabase(this);

        LoadArticlesTask task = new LoadArticlesTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://www.theguardian.com/world/rss");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListAdapter(this, new ArrayList<Article>());
        recyclerView.setAdapter(adapter);
    }

    public void loadList(ArrayList<Article> items) {
        adapter.insertItems(items);
    }
}
