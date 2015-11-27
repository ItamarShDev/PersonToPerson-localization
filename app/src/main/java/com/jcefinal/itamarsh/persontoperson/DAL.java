package com.jcefinal.itamarsh.persontoperson;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

/**
 * Created by itamar on 27-Nov-15.
 */
public class DAL {
    private SQLiteDatabase db;
    private ContactsDBHelper cdh;
    public Cursor getAllTimeEntriesCursor()
    {
        //get db
        db = cdh.getReadableDatabase();
        //get data
        Cursor cursor = db.rawQuery("SELECT * FROM " + Contacts.ContactsTable.TABLE_NAME, null);
        return cursor;

    }
}
