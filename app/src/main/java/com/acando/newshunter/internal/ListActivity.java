package com.acando.newshunter.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.acando.newshunter.content.NewsEntry;
import com.acando.newshunter.R;
import com.acando.newshunter.content.ViewImageTask;
import com.acando.newshunter.content.XmlDownloadTask;
import com.acando.newshunter.database.UtilDatabase;

import java.util.ArrayList;

public class ListActivity extends Activity {

    public ViewImageTask mImageTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mImageTask = new ViewImageTask(this);
        mImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        UtilDatabase.checkDatabase(this);

        XmlDownloadTask task = new XmlDownloadTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://www.theguardian.com/world/rss");
    }

    public void loadList(ArrayList<NewsEntry> items) {
        ListView listView = (ListView) findViewById(R.id.list);
        final ListAdapter adapter = new ListAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsEntry entry = adapter.getItem(position);
                if(entry != null) {
                    Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                    intent.putExtra("url", entry.link);
                    startActivity(intent);
                }
            }
        });
    }
}
