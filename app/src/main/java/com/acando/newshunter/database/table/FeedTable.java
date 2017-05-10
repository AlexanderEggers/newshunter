package com.acando.newshunter.database.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acando.newshunter.Util;
import com.acando.newshunter.content.NewsEntry;
import com.acando.newshunter.database.UtilDatabase;

import java.util.ArrayList;

public class FeedTable {

    public static void insertAll(Context context, ArrayList<NewsEntry> entries) {
        synchronized (UtilDatabase.LOCK) {
            SQLiteDatabase db = UtilDatabase.openWritableDatabase(context);

            for(NewsEntry entry : entries) {
                int currentID = TableIDsTable.increaseTableID(db, Util.FEED_TABLE);

                String sql = "INSERT INTO feed (id, title, link, pubDate, desc) VALUES (" +
                        currentID + ", '"
                        + entry.title.replace("'", Util.DB_APOSTROPHE_FIX) + "', '"
                        + entry.link + "', '"
                        + entry.pubDate + "', '"
                        + entry.desc.replace("'", Util.DB_APOSTROPHE_FIX) + "');";
                db.execSQL(sql);
            }
            db.close();
        }
    }

    public static ArrayList<NewsEntry> getAll(Context context) {
        SQLiteDatabase db = UtilDatabase.openReadableDatabase(context);
        ArrayList<NewsEntry> feedEntries = new ArrayList<>();

        String sql = "SELECT * FROM feed";
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst()) {
            for(int i = 0; i < c.getCount(); i++) {
                NewsEntry entry = new NewsEntry(
                        c.getString(1).replace(Util.DB_APOSTROPHE_FIX, "'"),
                        c.getString(4),
                        c.getString(2),
                        c.getString(3).replace(Util.DB_APOSTROPHE_FIX, "'"),
                        null);
                feedEntries.add(entry);
                c.moveToNext();
            }
        }

        c.close();
        db.close();
        return feedEntries;
    }

    public static void removeAll(Context context) {
        synchronized (UtilDatabase.LOCK) {
            SQLiteDatabase db = UtilDatabase.openWritableDatabase(context);
            String sql = "DELETE FROM feed";
            db.execSQL(sql);
            db.close();
        }
    }
}
