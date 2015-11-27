package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button contactsButton;
    private Intent intent;
    private Context context;
    private DAL dal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactsButton = (Button)findViewById(R.id.button);
        context = getApplicationContext();
        dal = new DAL(this);
        for (int i=0;i<20;i++){
            dal.addIDEntry(Integer.toString(i));
        }
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, ContactsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
/*
private Cursor cursor;
    private DAL dal;
    private SimpleCursorAdapter cursorAdapter;


 String[] entries = new String[] {Contacts.ContactsTable.userName};
        int [] viewsID = new int[] {R.id.userNameTextView};

        cursor = dal.getAllTimeEntriesCursor();
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.contact, cursor, entries, viewsID, 0);
        cursorListView.setAdapter(cursorAdapter);
 */