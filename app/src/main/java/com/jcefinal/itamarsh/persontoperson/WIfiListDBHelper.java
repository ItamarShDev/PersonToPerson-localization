package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
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
        db.execSQL("CREATE TABLE " + WifiList.WifiTable.TABLE_NAME + " ( "
                + WifiList.WifiTable._ID + " INTEGER PRIMARY KEY autoincrement, "
                + WifiList.WifiTable.FREQUENCY + " INTEGER, "
                + WifiList.WifiTable.SIGNAL + " INTEGER, "
                + WifiList.WifiTable.CHANNEL + " INTEGER, "
                + "" + WifiList.WifiTable.BSSID + " TEXT NOT NULL UNIQUE"
                + ");");

    }

    public void addColumnToTable(SQLiteDatabase db, String name) {
        db.execSQL("ALTER TABLE " + WifiList.WifiTable.TABLE_NAME
                + " ADD " + name);
    }

    public void reserDb(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + WifiList.WifiTable.TABLE_NAME);
        onCreate(db);
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
