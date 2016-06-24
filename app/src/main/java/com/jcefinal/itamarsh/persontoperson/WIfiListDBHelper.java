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

    /**
     * constructor
     * @param context - application context
     */
    public WIfiListDBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VER);
    }

    /**
     * get the database version
     * @return database version
     */
    public static int getDatabaseVer() {
        return DATABASE_VER;
    }

    /**
     * create db
     * @param db reference to db instance
     */
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

    /**
     * add column to table
     * @param db the db instance
     * @param name the new column name
     */
    public void addColumnToTable(SQLiteDatabase db, String name) {
        db.execSQL("ALTER TABLE " + WifiList.WifiTable.TABLE_NAME
                + " ADD " + name);
    }

    /**
     * reset the db
     * @param db the db instance
     */
    public void resetDb(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + WifiList.WifiTable.TABLE_NAME);
        onCreate(db);
    }

    /**
     * handle the db upgrade
     * @param db - db instance
     * @param oldVersion - the old version number
     * @param newVersion - the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WifiList.WifiTable.TABLE_NAME);
        onCreate(db);
    }

    /**
     * handle db downgrade
     * @param db - db instance
     * @param oldVersion - the old version number
     * @param newVersion - the new version number
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WifiList.WifiTable.TABLE_NAME);

        onCreate(db);
    }
}
