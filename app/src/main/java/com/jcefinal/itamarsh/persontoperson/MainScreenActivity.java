package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, ContactsInterface {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tab;
    public int[] colorIntArray = {R.color.blue, R.color.dark_pink, R.color.red};
    public int[] colorIntArray2 = {R.color.dark_blue, R.color.dark_pink};
    public int[] iconIntArray = {R.drawable.ic_add_white_24dp, R.drawable.ic_play_arrow_white_24dp, R.drawable.ic_stop_white_24dp};
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private String message = "Add Contact";
    boolean play;
    private TextView m;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DAL dal;
    private ListView cursorListView;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        initViews();
        play = false;
        setSupportActionBar(toolbar);
        dal = new DAL(this);
        new GcmRegistrationAsyncTask(this).execute();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //FLOATING BUTTON
        fab.setOnLongClickListener(this);
        fab.setOnClickListener(this);
        tab.setupWithViewPager(mViewPager);

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                setMessage(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void setMessage(final int loc) {

        switch (loc) {
            case 0:
                message = "Add Contact";
                break;
            case 1:
                message = "Stop the Search";
                break;
        }
    }

    @Override
    public void onClick(final View view) {
        switch (mViewPager.getCurrentItem()) {
            case 0:

                AddContactDialogFragment alert = new AddContactDialogFragment();
                alert.show(getFragmentManager(), null);
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.e("myDebug", "dialog on dismiss");
                        getContacts();
                    }
                });

                Log.e("myDebug", "Add button was pressed, and first tab selected");


                break;
            case 1:
                if (play) {
                    fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[1]));
                    fab.setImageDrawable(getResources().getDrawable(iconIntArray[1]));
                    try {
                        locationManager.removeUpdates(locationListener);
                        m.setText("stopped location");
                    } catch (SecurityException s) {

                    }
                    play = false;
                } else {
                    fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[2]));
                    fab.setImageDrawable(getResources().getDrawable(iconIntArray[2]));
                    m = (TextView) findViewById(R.id.textView);
                    m.setText("looking for location...");
                    gpsLookout();
                    play = true;
                }
                break;
        }
    }

    private void gpsLookout() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean wifiOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        final String locationProvider = locationManager.getBestProvider(c, true);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                String s = "using " + location.getProvider() + "\nAccuracy: " + location.getAccuracy() + "\n" + location.getLongitude() + "," + location.getLatitude();
                m.setText(s);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        LocationSettings l = new LocationSettings(this, new LocationSettings.OnPermissionListener() {
            @Override
            public void OnPermissionChanged(boolean permissionGranted) {
                if (permissionGranted) {
                    try {
                        locationManager.getLastKnownLocation(locationProvider);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                    } catch (SecurityException s) {

                    }
                }

            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        return true;
    }

    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                toolbar.setBackgroundColor(getResources().getColor(colorIntArray2[position]));
                tab.setBackgroundColor(getResources().getColor(colorIntArray2[position]));
                fab.setImageDrawable(getResources().getDrawable(iconIntArray[position]));

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
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

    @Override
    public void updateList() {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "MainScreen Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.jcefinal.itamarsh.persontoperson/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "MainScreen Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.jcefinal.itamarsh.persontoperson/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    //the activities themselves
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private Context contex;
        private Cursor cursor;
        private DAL dal;
        private SimpleCursorAdapter cursorAdapter;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView cursorListView;
        private ViewGroup container;
        private View rootView;
        private TextView gps;
        private String[] entries = new String[]{Contacts.ContactsTable.userName, Contacts.ContactsTable.phoneNum, Contacts.ContactsTable.userID};
        private int[] viewsID = new int[]{R.id.userNameTextView, R.id.userPhoneTextView, R.id.userIdTextView};
        private Context context;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 final Bundle savedInstanceState) {
            this.container = container;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.activity_contacts, container, false);
                updateList();
                cursorListView = (ListView) rootView.findViewById(R.id.cursorListView);
                cursorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.e("myDebug", "item in list clicked");

                    }
                });
                cursorAdapter = new SimpleCursorAdapter(context, R.layout.contact, cursor, entries, viewsID, 0);
                cursorListView.setAdapter(cursorAdapter);
                Log.e("myDebug", "on create view = 1");
                return rootView;

            }
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_search, container, false);
                Log.e("myDebug", "on create view = 2");
                return rootView;
            } else {
                rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
            }
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public void onResume() {
            super.onResume();
        }

        private void updateList() {
            cursorListView = (ListView) rootView.findViewById(R.id.cursorListView);
            dal = new DAL(this.getContext());
            context = this.getActivity();
            cursor = dal.getAllTimeEntriesCursor();
            cursorAdapter = new SimpleCursorAdapter(context, R.layout.contact, cursor, entries, viewsID, BIND_ABOVE_CLIENT);
            cursorListView.setAdapter(cursorAdapter);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> FragmentList = new ArrayList();
//        private final List<String> FragmentTitles = new ArrayList();
//        private int tabCount = 2;
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
//        }
//        public void addFragment(Fragment fragment) {
//            FragmentList.add(fragment);
//        }
//        public void setTabCount(int num){
//            this.tabCount = num;
//        }
//        @Override
//        public int getCount() {
//            return tabCount;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Contacts";
//                case 1:
//                    return "Search";
//            }
//            return null;
//        }
//    }
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tab = (TabLayout) findViewById(R.id.tabs);
        cursorListView = (ListView) findViewById(R.id.cursorListView);

    }

    private void getContacts() {
        cursorListView = (ListView) findViewById(R.id.cursorListView);
        Cursor cursor = dal.getAllTimeEntriesCursor();
        String[] entries = new String[]{Contacts.ContactsTable.userName, Contacts.ContactsTable.phoneNum, Contacts.ContactsTable.userID};
        int[] viewsID = new int[]{R.id.userNameTextView, R.id.userPhoneTextView, R.id.userIdTextView};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.contact, cursor, entries, viewsID, BIND_ABOVE_CLIENT);
        cursorListView.setAdapter(cursorAdapter);

    }

}


