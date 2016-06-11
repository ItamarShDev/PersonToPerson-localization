package com.jcefinal.itamarsh.persontoperson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by itamar on 11-Jun-16.
 */
public class WIfiListDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VER = 1;
    public static final String DB_NAME = "wifi.db";

    public WIfiListDBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VER);
    }

    public static int getDatabaseVer() {
        return DATABASE_VER;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WifiList.WifiTable.TABLE_NAME + " (" + WifiList.WifiTable._ID
                + WifiList.WifiTable.FREQUENCY + " INTEGER, "
                + WifiList.WifiTable.SIGNAL + " INTEGER, "
                + WifiList.WifiTable.CHANNEL + " INTEGER, "
                + "UNIQUE(" + WifiList.WifiTable.BSSID + ") ON CONFLICT IGNORE"
                + ");");
    }

    public void addColumnToTable(SQLiteDatabase db, String name) {
        db.execSQL("ALTER TABLE " + WifiList.WifiTable.TABLE_NAME
                + " ADD " + name);
    }

    public Cursor getDB() {
        SQLiteDatabase db = this.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
//                WifiList.WifiTable._ID,
                WifiList.WifiTable.BSSID,
                WifiList.WifiTable.SIGNAL
        };

// How you want the results sorted in the resulting Cursor

        Cursor c = db.query(
                WifiList.WifiTable.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                WifiList.WifiTable.SIGNAL                                 // The sort order
        );
        c.moveToFirst();
        return c;
    }

    public long addWifi(String bssid, int signal, int channel, int frequency) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WifiList.WifiTable.BSSID, bssid);
        values.put(WifiList.WifiTable.FREQUENCY, frequency);
        values.put(WifiList.WifiTable.SIGNAL, signal);
        if (channel > -1)
            values.put(WifiList.WifiTable.CHANNEL, channel);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        String[] selectionArgs = {String.valueOf(bssid)};

        newRowId = db.update(
                WifiList.WifiTable.TABLE_NAME,
                values,
                WifiList.WifiTable.BSSID,
                selectionArgs);
        if (newRowId == 0) {
            newRowId = db.insert(
                    WifiList.WifiTable.TABLE_NAME,
                    null,
                    values);
        }
        return newRowId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WifiList.WifiTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WifiList.WifiTable.TABLE_NAME);
        onCreate(db);
    }
}
