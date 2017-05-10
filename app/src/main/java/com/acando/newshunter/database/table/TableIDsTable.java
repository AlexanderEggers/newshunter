package com.acando.newshunter.database.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acando.newshunter.Util;
import com.acando.newshunter.database.UtilDatabase;

public final class TableIDsTable {

    /**
     * Inserts a new table id for certain table of the database.
     *
     * @param context context which is used to insert a new entry
     * @param table   id of the table which needs to be included. This integer is saved in the Util
     *                class
     */
    public static void insertTableID(final Context context, final int table) {
        SQLiteDatabase db = UtilDatabase.openWritableDatabase(context);
        String sql = "INSERT INTO table_ids (table_id,current_id) VALUES ("
                + table + ", 0);";
        db.execSQL(sql);
        db.close();
    }

    /**
     * Increases the id of a certain table id.
     *
     * @param db    database to access the table id
     * @param table table id which needs to be increased
     * @return returns the table id which can be used
     */
    protected static int increaseTableID(final SQLiteDatabase db, final int table) {
        int current_id = getTableID(db, table);
        if (current_id != Util.ERROR) {
            String sql = "UPDATE table_ids SET "
                    + "current_id = " + (current_id + 1)
                    + " WHERE table_id = " + table + ";";
            db.execSQL(sql);
            return current_id;
        } else {
            String sql = "INSERT INTO table_ids (table_id,current_id) VALUES ("
                    + table + ", 0);";
            db.execSQL(sql);
            return 0;
        }
    }

    /**
     * Internal getter to get the current table id associate to a specific table.
     *
     * @param db    database to access the table id
     * @param table table id which needs to be increased
     * @return return the current table id or the value of Util.ERROR
     */
    private static int getTableID(final SQLiteDatabase db, final int table) {
        int current_id = Util.ERROR;
        Cursor c = db.rawQuery("SELECT current_id FROM table_ids WHERE table_id = " + table, null);
        if (c.moveToFirst()) {
            current_id = c.getInt(0);
        }
        c.close();
        return current_id;
    }
}