package com.acando.newshunter.database.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acando.newshunter.Util;
import com.acando.newshunter.content.Article;
import com.acando.newshunter.database.UtilDatabase;

import java.util.ArrayList;

public class FeedTable {

    public static void insertAll(Context context, ArrayList<Article> entries) {
        synchronized (UtilDatabase.LOCK) {
            SQLiteDatabase db = UtilDatabase.openWritableDatabase(context);

            for(Article entry : entries) {
                int currentID = TableIDsTable.increaseTableID(db, Util.FEED_TABLE);

                String sql = "INSERT INTO feed (id, source, title, link, date, desc) VALUES (" +
                        currentID + ", '"
                        + entry.source.replace("'", Util.DB_APOSTROPHE_FIX) + "', '"
                        + entry.title.replace("'", Util.DB_APOSTROPHE_FIX) + "', '"
                        + entry.link.replace("'", Util.DB_APOSTROPHE_FIX) + "', "
                        + entry.date + ", '"
                        + entry.desc.replace("'", Util.DB_APOSTROPHE_FIX) + "');";
                db.execSQL(sql);
            }
            db.close();
        }
    }

    public static ArrayList<Article> getAll(Context context) {
        SQLiteDatabase db = UtilDatabase.openReadableDatabase(context);
        ArrayList<Article> feedEntries = new ArrayList<>();

        String sql = "SELECT * FROM feed";
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst()) {
            for(int i = 0; i < c.getCount(); i++) {
                Article entry = new Article();
                entry.source = c.getString(1).replace(Util.DB_APOSTROPHE_FIX, "'");;
                entry.title = c.getString(2).replace(Util.DB_APOSTROPHE_FIX, "'");
                entry.link = c.getString(3).replace(Util.DB_APOSTROPHE_FIX, "'");;
                entry.date = c.getLong(4);
                entry.desc = c.getString(5).replace(Util.DB_APOSTROPHE_FIX, "'");;
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
