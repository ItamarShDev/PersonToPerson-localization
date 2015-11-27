package com.jcefinal.itamarsh.persontoperson;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id .toolbar);
        setSupportActionBar(toolbar);
        ListView cursorListView = (ListView)findViewById(R.id.cursorListView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        String[] entries = new String[] {Contacts.ContactsTable.userName};
        int [] viewsID = new int[] {R.id.userNameTextView};

        cursor = dal.getAllTimeEntriesCursor();
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.contact, cursor, entries, viewsID, 0);
        cursorListView.setAdapter(cursorAdapter);


    }
}
