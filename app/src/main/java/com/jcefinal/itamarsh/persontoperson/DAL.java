package com.jcefinal.itamarsh.persontoperson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by itamar on 27-Nov-15.
 * This class responsible for actions performed on Data Base
 */
public class DAL {
    private SQLiteDatabase db;
    private ContactsDBHelper cdh;
    private Helper helper = new Helper();
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

    public void addEntries(String name,String phone)
    {
        db = cdh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contacts.ContactsTable.userName, name);
        values.put(Contacts.ContactsTable.phoneNum, phone);
        String hashed = helper.encode(phone);
        values.put(Contacts.ContactsTable.hashedPhone, hashed);
        db.insert(Contacts.ContactsTable.TABLE_NAME, null, values);
        db.close();

    }
    public String getName(String phone) {
        Cursor cursor = getAllTimeEntriesCursor();
        int position = 0;
        String name;
        do{
            cursor.moveToPosition(position);
            int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.hashedPhone);
            if(timeIndex != -1) {
                name = cursor.getString(timeIndex);
            }
            else{
                name = "New Friend";
            }
        position++;
        }
        while(!name.equals(phone)&&(position<cursor.getCount()));
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.userName);
        return cursor.getString(timeIndex);
    }
    public String getName(int position) {
        Cursor cursor = getAllTimeEntriesCursor();
        cursor.moveToPosition(position);
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.userName);
        String name = cursor.getString(timeIndex);
        return name;
    }
    public String getPhone(int position) {
        Cursor cursor = getAllTimeEntriesCursor();
        cursor.moveToPosition(position);
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.hashedPhone);
        String phone = cursor.getString(timeIndex);
        return phone;
    }
   // public Cursor checkPhoneExist
}
