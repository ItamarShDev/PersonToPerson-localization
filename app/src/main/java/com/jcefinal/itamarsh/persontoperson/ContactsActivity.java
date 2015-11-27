package com.jcefinal.itamarsh.persontoperson;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsActivity extends AppCompatActivity {
    private Cursor cursor;
    private DAL dal;
    private SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ListView cursorListView = (ListView)findViewById(R.id.cursorListView);
        dal = new DAL(this);
        String[] entries = new String[] {Contacts.ContactsTable.userID};
        int [] viewsID = new int[] {R.id.userNameTextView};

        cursor = dal.getAllTimeEntriesCursor();
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.contact, cursor, entries, viewsID, 0);
        cursorListView.setAdapter(cursorAdapter);
    }
}
