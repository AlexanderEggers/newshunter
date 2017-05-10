package com.acando.newshunter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.acando.newshunter.Util;
import com.acando.newshunter.database.table.TableIDsTable;
import com.acando.newshunter.database.tool.DatabaseCreator;

import java.io.File;

public class UtilDatabase {

    public static final String LOCK = "LOCK";

    public static void checkDatabase(Context context) {
        if (!isDatabaseExisting(context)) {
            resetDatabase(context);
        }
    }

    public static void resetDatabase(Context context) {
        synchronized (LOCK) {
            DatabaseCreator.createDatabase(context);
            initDatabase(context);
        }
    }

    public static boolean isDatabaseExisting(Context context) {
        return new File(context.getFilesDir() + "/" + "content.db").exists();
    }

    public static SQLiteDatabase openReadableDatabase(Context context) {
        return SQLiteDatabase.openDatabase(context.getFilesDir() + "/" + "content.db",
                null, SQLiteDatabase.OPEN_READONLY);
    }

    public static SQLiteDatabase openWritableDatabase(Context context) {
        return SQLiteDatabase.openDatabase(context.getFilesDir() + "/" + "content.db",
                null, SQLiteDatabase.OPEN_READWRITE);
    }

    private static void initDatabase(Context context) {
        TableIDsTable.insertTableID(context, Util.FEED_TABLE);
        TableIDsTable.insertTableID(context, Util.SOURCE_TABLE);
    }
}
