package com.jcefinal.itamarsh.persontoperson;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;

public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, ContactsInterface {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private IntentFilter mIntentFilter;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
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
    private static final String TAG = "myDebug";
    private SharedPreferences memory;
    private Location l, correntLocation;
    private final int MIN_GPS_RADIUS = 30,BT_ON_RADIUS = 20;
    boolean gpsOn, networkOn;
    private final static int NETWORK_ON=0, WIFI_ON =1, GPS_ON = 2,BT_ON = 3;
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
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        play = false;
        setSupportActionBar(toolbar);
        dal = new DAL(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        l = new Location("");
        //FLOATING BUTTON
        fab.setOnLongClickListener(this);
        fab.setOnClickListener(this);
        tab.setupWithViewPager(mViewPager);
        Intent i = getIntent();
        int tabToOpen = i.getIntExtra("loc", -1);
        if (tabToOpen!=-1) {
            m = (TextView) findViewById(R.id.textView);
            mViewPager.setCurrentItem(1);
            fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[2]));
            fab.setImageDrawable(getResources().getDrawable(iconIntArray[2]));
            play = true;
            gpsLookout();
        }
        memory = getSharedPreferences("currentLoc", MODE_PRIVATE);
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

        if (memory.getString("myphone", "").isEmpty()) {
            Log.i(TAG, "in if");
            FirstPageDialog alert = new FirstPageDialog();
            alert.setCancelable(false);
            alert.show(getFragmentManager(), null);
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    String phone =  memory.getString("myphone", "");
                    new GcmRegistrationAsyncTask(getBaseContext()).execute("register", null, null);
                }
            });
        }
        else
        {
            new GcmRegistrationAsyncTask(this).execute("register", null, null);
        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            DAL dal = new DAL(getBaseContext());
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            float d = 0;
            if(correntLocation!=null){
                String location[] = message.split(",");
                String loc = "\nYour Location: \n" + correntLocation.getLongitude() + "," + correntLocation.getLatitude();
                l.setLongitude(Float.valueOf(location[0]));
                l.setLatitude(Float.valueOf(location[1]));
                d = correntLocation.distanceTo(l);
                Log.i("DISTANCE", ""+d);
                String dtLoc = "Friend Location: "+message+"\nDistance: "+d;
                TextView locationText = (TextView)findViewById(R.id.distanceText);
                locationText.setText(dtLoc);
                TextView m = (TextView) findViewById(R.id.textView);
                m.setText(loc);
                locationAlgorithm(d);
            }
        }
    };

    private void locationAlgorithm(float d) {
        Log.i("ALGO","distance is "+Math.round(d));
        if (Math.round(d)<=BT_ON_RADIUS){
            blueTooth();
        }
        if (Math.round(d)<=MIN_GPS_RADIUS) {
            wifi();
        }
    }

    private void wifi() {
        Log.i("ALGO", "WiFi Range");
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()){
            buildAlertMessageNoGps(WIFI_ON);
        }else{
            Log.i("ALGO", "WiFi On");
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i("P2P", "discoverPeers SUCCESS");
                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.i("P2P", "discoverPeers Failure");
                }
            });
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }
    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
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
                m = (TextView) findViewById(R.id.textView);
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
    private void blueTooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i("BlueTooth", "no bt");
            // Device does not support Bluetooth
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable :)
                Log.i("BlueTooth", "enabled");
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

                Method getUuidsMethod = null;
                try {
                    getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                ParcelUuid[] uuids = new ParcelUuid[0];
                try {
                    uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                for (ParcelUuid uuid: uuids) {
                    Log.d("BT_UUID", "UUID: " + uuid.getUuid().toString());
                }
            }
            else{
                Log.i("BlueTooth", "disabled");
                buildAlertMessageNoGps(BT_ON);
            }
        }
    }
    private void gpsLookout() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gpsOn) {
            buildAlertMessageNoGps(GPS_ON);
        }
        if((!networkOn)&&gpsOn){
            buildAlertMessageNoGps(NETWORK_ON);
        }
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        final String locationProvider = locationManager.getBestProvider(c, true);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                correntLocation = location;
                // Called when a new location is found by the network location provider.
                new GcmRegistrationAsyncTask(getBaseContext()).execute("message", memory.getString("to", "053"), location.getLongitude() + "," + location.getLatitude());
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
                        correntLocation = locationManager.getLastKnownLocation(locationProvider);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    } catch (SecurityException s) {
                    }
                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == GPS_ON) {
            // Make sure the request was successful
                gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(!gpsOn)
                    Toast.makeText(getApplicationContext(), "Search Disabled", Toast.LENGTH_LONG).show();
                else{
                    if(!networkOn){
                        buildAlertMessageNoGps(WIFI_ON);
                    }
                }
        }
    }

    private void buildAlertMessageNoGps(final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String posType = (type==GPS_ON||type==NETWORK_ON)?"GPS":"WiFi";
        final String cancel = type==GPS_ON?"Cancel Search":"No Thanks";
        if(type==BT_ON)
            posType = "BlueTooth";
        builder.setMessage("Your "+posType+" seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        switch (type){
                            case GPS_ON:
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ON);
                            break;
                            case NETWORK_ON:
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ON);
                                break;
                            case WIFI_ON:
                                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), WIFI_ON);
                            break;
                            case BT_ON:
                                startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), BT_ON);
                                break;
                        }
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if(type==GPS_ON) {
                            Toast.makeText(getApplicationContext(), "Search Disabled", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "WiFi Disabled", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
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
    }

    @Override
    public void onStop() {
        super.onStop();

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
        private AlertDialog approveDialog;
        private ArrayList list;
        private static final String SENDER_ID = "186698592995";

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
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        new AlertDialog.Builder(context)
                                //.setTitle("Are you sure?")
                                .setMessage("Do you want to look for " + dal.getName(position) + "?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new GcmRegistrationAsyncTask(getContext()).execute("message", dal.getPhone(position), "I Want to Search For You");
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                        Log.i(TAG, "name" + dal.getName(position));
                        Log.e("myDebug", "item in list clicked");

                    }

                });
                cursorListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                        new AlertDialog.Builder(context)
                                .setMessage("Do you want to delete " + dal.getName(position) + "?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new GcmRegistrationAsyncTask(getContext()).execute("message", dal.getPhone(position), "I Want to Search For You");
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                        return false;
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


