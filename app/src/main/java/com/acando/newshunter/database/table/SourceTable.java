package com.acando.newshunter.database.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acando.newshunter.Util;
import com.acando.newshunter.content.Source;
import com.acando.newshunter.database.UtilDatabase;

import java.util.ArrayList;

public class SourceTable {

    public static void insert(Context context, Source source) {
        synchronized (UtilDatabase.LOCK) {
            SQLiteDatabase db = UtilDatabase.openWritableDatabase(context);

            String sql = "INSERT INTO source (internal_name, name, url) VALUES ('"
                    + source.internalName.replace("'", Util.DB_APOSTROPHE_FIX) + "', '"
                    + source.name.replace("'", Util.DB_APOSTROPHE_FIX) + "', '"
                    + source.url.replace("'", Util.DB_APOSTROPHE_FIX) + "');";
            db.execSQL(sql);
            db.close();
        }
    }

    public static ArrayList<Source> getAll(Context context) {
        SQLiteDatabase db = UtilDatabase.openReadableDatabase(context);
        ArrayList<Source> sources = new ArrayList<>();

        String sql = "SELECT * FROM source";
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst()) {
            for(int i = 0; i < c.getCount(); i++) {
                Source source = new Source();
                source.internalName = c.getString(0);
                source.name = c.getString(1);
                source.url = c.getString(2);
                sources.add(source);
                c.moveToNext();
            }
        }

        c.close();
        db.close();
        return sources;
    }

    public static Source get(Context context, String internalName) {
        SQLiteDatabase db = UtilDatabase.openReadableDatabase(context);
        Source source = null;

        String sql = "SELECT * FROM source WHERE internal_name = '" + internalName + "'";
        Cursor c = db.rawQuery(sql, null);

        if(c.moveToFirst()) {
            source = new Source();
            source.internalName = c.getString(0);
            source.name = c.getString(1);
            source.url = c.getString(2);
        }

        c.close();
        db.close();
        return source;
    }

    public static void remove(Context context, String internalName) {
        synchronized (UtilDatabase.LOCK) {
            SQLiteDatabase db = UtilDatabase.openWritableDatabase(context);
            String sql = "DELETE FROM source WHERE internal_name = '" + internalName + "'";
            db.execSQL(sql);
            db.close();
        }
    }
}
