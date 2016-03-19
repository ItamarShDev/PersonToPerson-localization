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
    //Function to add entry with phone number and name
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
<<<<<<< HEAD
=======
    //Function return name's for person from his phone number
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    public String getName(String phone) {
        Cursor cursor = getAllTimeEntriesCursor();
        int position = 0;
        String name;
        do{
            cursor.moveToPosition(position);
<<<<<<< HEAD
            int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.phoneNum);
            name = cursor.getString(timeIndex);
=======
            int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.hashedPhone);
            if(timeIndex != -1) {
                name = cursor.getString(timeIndex);
            }
            else{
                name = "New Friend";
            }
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
        position++;
        }
        while(!name.equals(phone)&&(position<cursor.getCount()));
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.userName);
        return cursor.getString(timeIndex);
    }
<<<<<<< HEAD
=======
    //Return name from cursor position
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    public String getName(int position) {
        Cursor cursor = getAllTimeEntriesCursor();
        cursor.moveToPosition(position);
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.userName);
        String name = cursor.getString(timeIndex);
        return name;
    }
<<<<<<< HEAD
    public String getPhone(int position) {
        Cursor cursor = getAllTimeEntriesCursor();
        cursor.moveToPosition(position);
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.phoneNum);
=======
    //Return phone from cursor position
    public String getPhone(int position) {
        Cursor cursor = getAllTimeEntriesCursor();
        cursor.moveToPosition(position);
        int timeIndex = cursor.getColumnIndex(Contacts.ContactsTable.hashedPhone);
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
        String phone = cursor.getString(timeIndex);
        return phone;
    }
}
