package com.acando.newshunter.database.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class DatabaseCreator {

    public static void createDatabase(Context context) {
        SQLiteDatabase.deleteDatabase(new File(context.getFilesDir() + "/" + "content.db"));
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir() + "/" + "content.db", null);
        db.setVersion(1);
        createTables(db);
        db.close();
    }

    private static void createTables(SQLiteDatabase db) {
        String sql = "CREATE TABLE feed "
                + "(id                      INT       PRIMARY KEY    NOT NULL,"
                + " source                  TEXT                     NOT NULL,"
                + " title                   TEXT                     NOT NULL,"
                + " link                    TEXT                     NOT NULL,"
                + " date                    INTEGER                  NOT NULL,"
                + " desc                    TEXT                     NOT NULL)";
        db.execSQL(sql);

        sql = "CREATE TABLE source "
                + "(internal_name           TEXT       PRIMARY KEY   NOT NULL,"
                + " name                    TEXT                     NOT NULL,"
                + " url                     TEXT                     NOT NULL)";
        db.execSQL(sql);

        sql = "CREATE TABLE table_ids "
                + "(table_id                INT       PRIMARY KEY    NOT NULL,"
                + " current_id              INT                      NOT NULL)";
        db.execSQL(sql);
    }
}
