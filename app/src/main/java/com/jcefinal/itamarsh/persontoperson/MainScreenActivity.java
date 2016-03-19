package com.jcefinal.itamarsh.persontoperson;

import android.app.AlertDialog;
<<<<<<< HEAD
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
=======
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
<<<<<<< HEAD
import android.content.SharedPreferences;
=======
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
<<<<<<< HEAD
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.ParcelUuid;
=======
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
<<<<<<< HEAD
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
=======
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
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

<<<<<<< HEAD
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
    private WifiManager wifi;
    private WifiInfo info;
    private IntentFilter mIntentFilter;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
=======
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, SensorEventListener {
    /*Define variables */
    private final static int NETWORK_ON=0, WIFI_ON =1, GPS_ON = 2;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tab;
    private static final String MODE_APPROVE = "approve", MODE_STOP = "stop_search", MESSAGE_RECEIVER = "my-event", SHOW_DIALOG ="show-dialog";
    private static final int RED = 2, BLUE = 0, PINK = 1, PLAY = 1,ADD= 0, PAUSE = 2;
    public int[] colorIntArray = {R.color.blue, R.color.dark_pink, R.color.red};
    public int[] colorIntArray2 = {R.color.dark_blue, R.color.dark_pink};
    public int[] iconIntArray = {R.drawable.ic_add_white_24dp, R.drawable.ic_play_arrow_white_24dp, R.drawable.ic_pause_white_24dp};
    private FloatingActionButton fab;
    private Toolbar toolbar;
<<<<<<< HEAD
    private String message = "Add Contact" , address;
    boolean play;
    private TextView m;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DAL dal;
    private ListView cursorListView;
    private static final String TAG = "myDebug";
    private SharedPreferences memory;
    private GcmRegistrationIntent gcmRegistration;
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
=======
    private String message = "Add Contact";
    private TextView m;
    private DAL dal;
    private ListView cursorListView;
    private CompassView compassView;
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private float azimuth = 0; // degree
    private static final String TAG = "myDebug";
    private SharedPreferences memory;
    private Location currentLocation, target;
    private Context context;
    private Helper helper;
    private ProgressDialog dialog;
    private boolean flag = true;
    private SensorEventListener sensorEventListener;
    private Intent service;
    private ViewPager mViewPager; //The {@link ViewPager} that will host the section contents.
    private boolean mBound;
    private LocationService locationService;

    private boolean receiving = false;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        initViews();
<<<<<<< HEAD
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
        new GcmRegistrationAsyncTask(this).execute("register", null, null);
=======
        service = new Intent(this,LocationService.class);
        memory = getSharedPreferences("currentLoc", MODE_PRIVATE);
        mBound = false;
        setSupportActionBar(toolbar);
        dal = new DAL(this);
        context = this;
        helper = new Helper();
        sensorEventListener = this;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
<<<<<<< HEAD
        l = new Location("");
=======
        target = new Location("");
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
        //FLOATING BUTTON
        fab.setOnLongClickListener(this);
        fab.setOnClickListener(this);
        tab.setupWithViewPager(mViewPager);
<<<<<<< HEAD
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
=======

        //*************************************************************************
        // prompt for phone number and name only when application first installed *
        //*************************************************************************
        if (memory.getString("myphone", "").isEmpty()) {
            Log.i(TAG, "in if");
            FirstPageDialog alert = new FirstPageDialog();
            alert.setCancelable(false);
            alert.show(getFragmentManager(), null);
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //register with number and name that user gave
                    Helper.sendMessage(getBaseContext(), "register", null, null);
                }
            });
        }
        else {
            Helper.sendMessage(getBaseContext(), "register", null, null); //register with number and name that saved in SP
        }

        /***************************************************
        * Treatment for opening activity from notification *
        ***************************************************/
        Intent i = getIntent();
        String action = i.getAction();
        int tabToOpen = i.getIntExtra("loc", -1);
        Log.i(TAG, "Extra is " + tabToOpen);
        Log.i(TAG, "action is " + action);
        if (tabToOpen!=-1) { //If was opened from Notification, extra will be given
            NotificationManager nm = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(0); //Cancel all notifications
            if(action.equals(MODE_APPROVE)) { // make sure action is approve
                m = (TextView) findViewById(R.id.textView);
                mViewPager.setCurrentItem(1);
                setSearchStatus(true, 1);
                setMessage(1);
                if(tabToOpen == 1) {//when approving request
                    String to = i.getStringExtra("to");
                    SharedPreferences.Editor edit = memory.edit();
                    edit.putString("to", to);
                    edit.apply();
                    Log.i("after noti","to" + to);
                    Helper.sendMessage(getApplicationContext(), "message", to, Helper.APPROVED);
                }
                else if(tabToOpen == 2){
                    receiving = true;
                    //Register broadcast receiver
                            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                            new IntentFilter(MESSAGE_RECEIVER));
                }
                startService(service);
                bindService(service, mConnection, Context.BIND_AUTO_CREATE);
                LocalBroadcastManager.getInstance(this).registerReceiver(mDialogShow,
                        new IntentFilter(SHOW_DIALOG));
            }
            else if(action.equals(MODE_STOP)){
                try{
                    stopService(service);
                    setSearchStatus(false, 0);
                    receiving = false;
                    Toast.makeText(this, "Search stopped",Toast.LENGTH_SHORT).show();
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mDialogShow);
                }catch (SecurityException e){
                    Log.e("LOCATION",e.toString());
                }
            }
        }
        else {
            readContacts();
        }

                //********************* Tab Listener  ******************************
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
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


    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    //        Set colors and the icon of fab depends on search status
    private void setSearchStatus(boolean status, int tab){
        if(tab == 0){
            setFloatingActionButtonColors(fab,colorIntArray[BLUE],colorIntArray[PINK], iconIntArray[ADD]);
        }else{

            if(status){
                setFloatingActionButtonColors(fab,colorIntArray[RED],colorIntArray[PINK], iconIntArray[PAUSE]);
            }
            else{
                setFloatingActionButtonColors(fab,colorIntArray[PINK],colorIntArray[BLUE], iconIntArray[PLAY]);
            }
        }
    }
    //set colors for FAB
    private void setFloatingActionButtonColors(FloatingActionButton fab, int primaryColor, int rippleColor, int icon) {
        int[][] states = {
                {android.R.attr.state_enabled},
                {android.R.attr.state_pressed},
        };

        int[] colors = {
                primaryColor,
                rippleColor,
        };
        int color;
        if(primaryColor==colorIntArray[BLUE])
            color = BLUE;
        else
            color = PINK;
        toolbar.setBackgroundColor(getResources().getColor(colorIntArray2[color]));
        tab.setBackgroundColor(getResources().getColor(colorIntArray2[color]));

        ColorStateList colorStateList = new ColorStateList(states, colors);
        fab.setImageDrawable(getResources().getDrawable(icon));

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
                String loc = "\nYour Location: \n" + correntLocation.getLongitude()
                        + "," + correntLocation.getLatitude()
                        +"\nUsing: "+correntLocation.getProvider()
                        +"\nAccuracy: "+correntLocation.getAccuracy();
                l.setLongitude(Float.valueOf(location[0]));
                l.setLatitude(Float.valueOf(location[1]));
                d = correntLocation.distanceTo(l);
                Log.i("DISTANCE", ""+d);
                String dtLoc = "\nFriend Location: "+message+"\n\nDistance: "+d;
                TextView locationText = (TextView)findViewById(R.id.distanceText);
                locationText.setText(dtLoc);
                TextView m = (TextView) findViewById(R.id.textView);
                m.setText(loc);
                locationAlgorithm(d);
            }
        }
    };

<<<<<<< HEAD
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
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        info = wifi.getConnectionInfo();
        address = info.getMacAddress();
        Log.i("WiFi Addredd", address);
        if (!wifi.isWifiEnabled()){
            buildAlertMessageNoGps(WIFI_ON);
        }else{
            Log.i("ALGO", "WiFi On");

            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i("P2P", "discoverPeers SUCCESS");
                    if (mManager != null) {
                        Log.i("P2P","Searching Peers");
                        mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {

                            @Override
                            public void onPeersAvailable(WifiP2pDeviceList peers) {
                                Log.d("P2P", String.format("PeerListListener: %d peers available, updating device list", peers.getDeviceList().size()));
                                Toast.makeText(getApplicationContext(),
                                        String.format("PeerListListener: %d peers available, updating device list",
                                                peers.getDeviceList().size()), Toast.LENGTH_SHORT).show();
                                // DO WHATEVER YOU WANT HERE
                                // YOU CAN GET ACCESS TO ALL THE DEVICES YOU FOUND FROM peers OBJECT

                            }
                        });
                    }
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
=======
    //****************************************************************
    //*        Treatment for notification with location
    //****************************************************************
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!mBound)
                    return;
                currentLocation = locationService.getLastLocation();
                if(flag) {
                    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
                    mSensorManager.registerListener(sensorEventListener, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
                    flag = false;
                }
                // Extract data included in the Intent
                String message = intent.getStringExtra("message");
                Log.d("receiver", "Got message: " + message);
                float d = 0;
                if(locationService.getLastLocation() != null){// If current location set
                    String location[] = message.split(",");
                    String loc = "\nYour Location: \n" + String.format("%.2f",currentLocation.getLongitude()) + ","
                            + String.format("%.2f", currentLocation.getLatitude());
                    target.setLongitude(Float.valueOf(location[0]));
                    target.setLatitude(Float.valueOf(location[1]));
                    float l1 = Float.valueOf(location[0]);
                    float l2 = Float.valueOf(location[1]);
                    d = currentLocation.distanceTo(target);
                    Log.i("DISTANCE", "" + d);
                 //Update friend's location
                            String dtLoc = "Friend Location: "+ String.format("%.2f", l1) +',' + String.format("%.2f",l2) +"\nDistance: "+
                            String.format("%.2f",d);
                    TextView locationText = (TextView)findViewById(R.id.distanceText);
                    locationText.setText(dtLoc);
                    TextView m = (TextView) findViewById(R.id.textView);
                    m.setText(loc);
                }
            }
        };

    @Override
    public void onResume(){
        super.onResume();
        // Need to register again all broadcast receivers
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MESSAGE_RECEIVER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDialogShow,
                new IntentFilter(SHOW_DIALOG));
        if(mSensorManager!=null)
           mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
//         Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDialogShow);
        flag = true;
        super.onPause();
    }

    //BR for DIALOGS
    private BroadcastReceiver mDialogShow = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", -1);
            if(type>-1)
            buildAlertMessageNoGps(type);
        }
    };
// Alert message asking for user to enable GPS
    public void buildAlertMessageNoGps(final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String posType = (type==GPS_ON||type==NETWORK_ON)?"GPS":"WiFi";
        final String cancel = type==GPS_ON?"Cancel Search":"No Thanks";
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
                        }
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if (type == GPS_ON) {
                            Toast.makeText(getApplicationContext(), "Search Disabled", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "WiFi Disabled", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3

    //Private function, used to set message jumping when floating button long pressed
    private void setMessage(final int loc) {
        switch (loc) {
            case 0:
                message = "Add Contact";
                break;
            case 1:
                if( mBound)
                    message = "Stopped Current Search";
                else
                    message = "Long Press to Stop Current Search";
                break;
        }
    }

    //*********** Listener for floating button *************
    @Override
    public void onClick(final View view) {

        switch (mViewPager.getCurrentItem()) {
<<<<<<< HEAD
            case 0:
=======
            case 0: //tab 0 selected, add contact
                setFloatingActionButtonColors(fab, colorIntArray[BLUE], colorIntArray[PINK], iconIntArray[ADD]);
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
                AddContactDialogFragment alert = new AddContactDialogFragment();
                alert.show(getFragmentManager(), null);
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.e("myDebug", "dialog on dismiss");
                        getContacts();
<<<<<<< HEAD
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

=======
                        Log.i(TAG, "not_exist " + memory.getBoolean("not_exist", false));
                        if(memory.getBoolean("add", false)) {
                            ArrayList<String> l = new ArrayList<String>();
                            ArrayList<String> l2 = new ArrayList<String>();
                            l.add(memory.getString("to", ""));
                            l2.add(memory.getString("name",""));
                            sendToServer(1, l, l2);
                        }
                   }
                });
                Log.e("myDebug", "action button was pressed, and first tab selected");
                break;
            case 1: // tab 1 selected, stop location transmission
                Log.i("BOUND", "mBound is "+mBound);
                m = (TextView) findViewById(R.id.textView);
                if(!mBound&&!receiving){
                    Snackbar.make(view,"Please Select a Contact",Snackbar.LENGTH_LONG) .setAction("Action", null).show();
                }else{
                if (mBound) {
                    try {
                        setSearchStatus(false, 1);
                        stopService(service);
                        mBound = false;
                        m.setText("Paused Location");
                        if(mSensorManager!=null)
                        mSensorManager.unregisterListener(sensorEventListener);
                    } catch (SecurityException s) {
                        Log.i(TAG, "Security exception");
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
                    }
                } else {
<<<<<<< HEAD
                    fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[2]));
                    fab.setImageDrawable(getResources().getDrawable(iconIntArray[2]));
=======
                    startService(service);
                    bindService(service, mConnection, Context.BIND_AUTO_CREATE);
                    setSearchStatus(true, 1);
                    if(mSensorManager!=null)
                        mSensorManager.registerListener(sensorEventListener, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
                    m = (TextView) findViewById(R.id.textView);
                    m.setText("looking for location...");
                    mBound = true;

                }
                break;
<<<<<<< HEAD
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
=======
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
            }
        }

    }
    @Override
    public boolean onLongClick(View v) {
        if(v==fab){
            m = (TextView) findViewById(R.id.textView);
            if (mBound) {
                try {
                    setSearchStatus(false, 1);
                    receiving = false;
                    stopService(service);
                    mBound = false;
                    m.setText("Stopped Search");
                    TextView locationText = (TextView)findViewById(R.id.distanceText);
                    locationText.setText("");
                    Helper.sendMessage(this, "message", memory.getString("to", ""), Helper.STOP_SEARCH);
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mDialogShow);
                    if(mSensorManager!=null)
                        mSensorManager.unregisterListener(sensorEventListener);
                } catch (SecurityException s) {
                    Log.i(TAG, "Security exception");
                }
            }
            else{
                Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
<<<<<<< HEAD
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
=======
        }
        return true;
    }
    //Function to calculate angle of the phone relative to friend's location using sensors
    private void getDirection()
    {
        compassView = (CompassView)findViewById(R.id.myView);
        double angle = 0;
        if(locationService.getLastLocation() != null) {
            currentLocation = locationService.getLastLocation();
            angle = calculateAngle(currentLocation.getLongitude(), currentLocation.getLatitude(), target.getLongitude(), target.getLatitude());
        }
        //Correction;
        angle-=90;
        //Correction for azimuth
        angle-=azimuth;
        while(angle<0)angle=angle+360;
        if(compassView != null) {
            compassView.angle = (float) angle;
            compassView.invalidate();
        }
    }
    //Sensor listener
    public void onSensorChanged(SensorEvent event) {
        azimuth = event.values[0];
        getDirection();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3

    }
<<<<<<< HEAD
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
=======
    //Function to calculate angle from (x,y) of 2 points
    private static double calculateAngle(double x1, double y1, double x2,
                                        double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        return (Math.atan2(dx, dy) * 180) / Math.PI;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    }

    //Function responsible for floating button animation
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
                Log.i("PLAY", "play is "+mBound);
                int p = position==0?0:position-1;
                int pos = position;
                if(mBound&&(position==1))
                    pos = PAUSE;
                setFloatingActionButtonColors(fab, colorIntArray[position],colorIntArray2[p], iconIntArray[pos]);
                toolbar.setBackgroundColor(getResources().getColor(colorIntArray2[position]));
                tab.setBackgroundColor(getResources().getColor(colorIntArray2[position]));

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

<<<<<<< HEAD
    @Override
    public void updateList() {

    }
=======
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
<<<<<<< HEAD

    }


=======
        //Unregister sensor listener
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDialogShow);
        stopService(service);
        if(mSensorManager!=null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }

    }
    // This function sending to GCM server list of contacts from phonebook and getting all registered friends,
    // adding them to list of contacts
    public void sendToServer(final int src, final ArrayList<String> phoneList, final ArrayList<String> names){
        String serverAddr = Helper.SERVER_ADDR+"/contacts";
        context = this;
        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        ArrayList<String> list = new ArrayList<String>();

        for (String i: phoneList)
        {
            list.add(helper.encode(i));
        }
        JSONArray arr = new JSONArray(list);
        Log.i(TAG, "json array" + arr);
        JSONObject jsonBody = new JSONObject();
        try { //Got the list of friends, need to add them to contacts list
            jsonBody.put("contacts", arr);
        }
        catch (JSONException e)
        {
            Log.i(TAG, "json error" + e.getMessage());
        }
        Log.i(TAG, "in find contact after building json " + jsonBody);


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if(src == 1) {
            dialog.show();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                serverAddr,
                jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.cancel();
                            JSONArray result = response.getJSONArray("response");
                            Log.i(TAG, "response is: " + result);
                            for(int i=0; i<result.length();i++) {
                                String phone = result.get(i).toString().replace("\n", "").trim();
                                for (int j = 0; j < phoneList.size(); j++) {
                                    if (phone.equals(helper.encode(phoneList.get(j)).trim())) {
                                        dal.addEntries(names.get(j), phoneList.get(j));
                                    }
                                }
                            }
                            //If user asked to add specific user, and he is not registered, ask if he want to invite him
                           if(result.length()== 0 && src == 1)
                            {
                                inviteFriend();
                            }                         }
                        catch (JSONException e)
                        {
                            Log.i(TAG,"Error on response " +  e.getMessage());
                        }
                        getContacts();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.cancel();
                        try{
                            Log.i(TAG, error.toString());
                        }
                        catch (NullPointerException e)
                        {
                            Log.i(TAG, "Volley Error");
                        }

                    }
                }

        );
        //Sometimes timeout acquired, increase timeout and resend
        request.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("REQUEST");
        queue.add(request);
    }
    //Function to invite friend to APP by SMS
    public void inviteFriend()
    {
        Toast.makeText(context, "contact won't be added", Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(context)
                .setMessage("Sorry, your friend is not registered to our APP. Would you like to invite him?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmsManager smsManager = SmsManager.getDefault();
                        try {
                            smsManager.sendTextMessage(memory.getString("to", ""), null, "Hi, I would like to invite you to \"Find your Friend\" application." +
                                    " Please go to this link to download it: " +
                                    "https://github.com/olesyash/Find_Your_Friends/wiki", null, null);
                            Log.i(TAG, "sending message");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    // Function to read contacts from phone book and it to list
    public void readContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String phone = null;
        String id = "";
        ArrayList<String> listPhones = new ArrayList<String>();
        ArrayList<String> listNames = new ArrayList<String>();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if(pCur.moveToNext())
                    {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phone = phone.replace(" ", "");
                        phone = phone.replace("-", "");
                        phone = phone.replace("+972", "0");
                        listPhones.add(phone);
                        listNames.add(name);
                    }
                    pCur.close();
                }
            }
        }

        sendToServer(2, listPhones, listNames);
        cur.close();
        }
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3

    /**
     * A placeholder fragment containing a simple view.
     */
    //the activities themselves
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private Cursor cursor;
        private DAL dal;
        private SimpleCursorAdapter cursorAdapter;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView cursorListView;
        private View rootView;
        private String[] entries = new String[]{Contacts.ContactsTable.userName, Contacts.ContactsTable.phoneNum, Contacts.ContactsTable.userID};
        private int[] viewsID = new int[]{R.id.userNameTextView, R.id.userPhoneTextView, R.id.userIdTextView};
        private Context context;
<<<<<<< HEAD
        private AlertDialog approveDialog;
        private ArrayList list;
        private static final String SENDER_ID = "186698592995";
=======
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3

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
<<<<<<< HEAD
            this.container = container;
=======
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.activity_contacts, container, false);
                updateList();
                cursorListView = (ListView) rootView.findViewById(R.id.cursorListView);
                cursorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        new AlertDialog.Builder(context)
<<<<<<< HEAD
                                //.setTitle("Are you sure?")
=======
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
                                .setMessage("Do you want to look for " + dal.getName(position) + "?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
<<<<<<< HEAD
                                        new GcmRegistrationAsyncTask(getContext()).execute("message", dal.getPhone(position), "I Want to Search For You");
=======
                                        Helper.sendMessage(getContext(), "message", dal.getPhone(position), Helper.REQUEST);
                                        new AlertDialog.Builder(context)
                                                .setMessage("Request sent to " + dal.getName(position) + ". You will be notified when your friend reply")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
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

<<<<<<< HEAD
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
=======
                });

>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
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
<<<<<<< HEAD


        }

=======
        }
        //Function to update contacts list after adding to DB in fragment class
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
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
    //Initialize views
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tab = (TabLayout) findViewById(R.id.tabs);
        cursorListView = (ListView) findViewById(R.id.cursorListView);
        compassView = (CompassView)findViewById(R.id.myView);

<<<<<<< HEAD
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

=======
    }
    //Function to update contacts list after adding to DB.
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    private void getContacts() {
        cursorListView = (ListView) findViewById(R.id.cursorListView);
        Cursor cursor = dal.getAllTimeEntriesCursor();
        String[] entries = new String[]{Contacts.ContactsTable.userName, Contacts.ContactsTable.phoneNum, Contacts.ContactsTable.userID};
        int[] viewsID = new int[]{R.id.userNameTextView, R.id.userPhoneTextView, R.id.userIdTextView};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.contact, cursor, entries, viewsID, BIND_ABOVE_CLIENT);
        cursorListView.setAdapter(cursorAdapter);

    }



}


