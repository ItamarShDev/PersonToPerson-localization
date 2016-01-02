package com.jcefinal.itamarsh.persontoperson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by itamar on 27-Nov-15.
 */
public class DAL {
    private SQLiteDatabase db;
    private ContactsDBHelper cdh;
    public DAL(Context context)
    {
        cdh = new ContactsDBHelper(context);
    }

    public Cursor getAllTimeEntriesCursor()
    {
        //get db
        db = cdh.getReadableDatabase();
        //get data
        Cursor cursor = db.rawQuery("SELECT * FROM " + Contacts.ContactsTable.TABLE_NAME, new String[] {});
        return cursor;

    }
    public void addIDEntry(String id)
    {
        db = cdh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contacts.ContactsTable.userID, id);
        db.insert(Contacts.ContactsTable.TABLE_NAME, null, values);
        db.close();

    }
    public void addEntries(String name,String phone)
    {
        db = cdh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contacts.ContactsTable.userName, name);
        values.put(Contacts.ContactsTable.phoneNum, phone);
        db.insert(Contacts.ContactsTable.TABLE_NAME, null, values);
        db.close();

    }

    public String getName(int position) {
        Cursor cursor = getAllTimeEntriesCursor();
        cursor.moveToPosition(position);
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.userName);
        String name = cursor.getString(timeIndex);
        return name;
    }
}
