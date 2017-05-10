package com.acando.newshunter.widget;

import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.acando.newshunter.content.NewsEntry;
import com.acando.newshunter.R;
import com.acando.newshunter.database.table.FeedTable;

public final class ListProvider implements RemoteViewsFactory {
    private ArrayList<NewsEntry> listItemList = new ArrayList<>();
    private Context context = null;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void initList() {
        listItemList = FeedTable.getAll(context);
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);

        NewsEntry listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.title, listItem.title);
        remoteView.setTextViewText(R.id.desc, listItem.desc);
        remoteView.setTextViewText(R.id.pubDate, listItem.pubDate);

        //ListView click event part 2
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        fillInIntent.putExtra(WidgetProvider.EXTRA_LIST_VIEW_ITEM_URL, listItem.link);
        remoteView.setOnClickFillInIntent(R.id.widget_item_layout, fillInIntent);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        initList();
    }

    @Override
    public void onDestroy() {
        listItemList.clear();
    }
}
