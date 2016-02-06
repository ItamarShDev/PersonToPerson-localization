package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by itamar on 27-Nov-15.
 * This class responsible for creating DB in SQLIte and dropping it on version change
 */
public class ContactsDBHelper extends SQLiteOpenHelper{
public static final int DATABASE_VER = 2;
public static final String DB_NAME = "contacts.db";
        public ContactsDBHelper(Context context)
        {
            super(context, DB_NAME, null, DATABASE_VER);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + Contacts.ContactsTable.TABLE_NAME + " (" + Contacts.ContactsTable._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT," + Contacts.ContactsTable.userID + " TEXT, "
                    + Contacts.ContactsTable.userName + " TEXT, "
                    + Contacts.ContactsTable.phoneNum + " TEXT, "
                    + Contacts.ContactsTable.hashedPhone + " TEXT, "
                    + "UNIQUE("+ Contacts.ContactsTable.phoneNum + ")"
                    + ");");

        }
        public void addColumnToTable(SQLiteDatabase db, String name){
            db.execSQL("ALTER TABLE " + Contacts.ContactsTable.TABLE_NAME
            +" ADD " + name);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Contacts.ContactsTable.TABLE_NAME);
            onCreate(db);

        }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contacts.ContactsTable.TABLE_NAME);
        onCreate(db);
    }
}
