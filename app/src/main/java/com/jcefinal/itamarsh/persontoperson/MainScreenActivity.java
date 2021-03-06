package com.jcefinal.itamarsh.persontoperson;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.NeighboringCellInfo;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainScreenActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener, SensorEventListener {
    /*Define variables */
    private static final int RED = 2, BLUE = 0, PINK = 1, PLAY = 1, ADD = 0, PAUSE = 2;
    private final static int WIFI_ON = 1, GPS_ON = 2, BT_ON = 3, BT_SHOW = 4;
    private static final String TAG = "myDebug";
    private final int MIN_GPS_RADIUS = 30, BT_ON_RADIUS = 15;
    private final IntentFilter intentFilter = new IntentFilter();
    public int[] colorIntArray = {R.color.blue, R.color.dark_pink, R.color.red};
    public int[] colorIntArray2 = {R.color.dark_blue, R.color.dark_pink};
    public int[] iconIntArray = {R.drawable.ic_add_white_24dp, R.drawable.ic_play_arrow_white_24dp, R.drawable.ic_pause_white_24dp};
    boolean play;
    boolean gpsOn, networkOn;
    float latestAccuracy = 999999999;
    private ArrayList<String> mArrayAdapter;
    public boolean wifiOn = false;
    private BroadcastReceiver mReceiver;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tab;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location l, correntLocation;
    private boolean GPS = false, BT = false, WIFI = false;
    private String message = "Add Contact";
    private TextView m;
    private DAL dal;
    private ListView cursorListView;
    private CompassView compassView;
    public SensorManager mSensorManager;
    public Sensor mOrientation;
    private float azimuth = 0; // degree
    private SharedPreferences memory;
    public boolean isVisible;
    private WifiScanner ws = null;
    private BluetoothAdapter mBluetoothAdapter;
    private WifiManager wifi;
    public Location currentLocation, target, currentLocation1;
    private Context context;
    private Helper helper;
    private ProgressDialog dialog;
    public boolean flag = true;
    public SensorEventListener sensorEventListener;
    private Intent service;
    private ViewPager mViewPager; //The {@link ViewPager} that will host the section contents.
    public boolean mBound;
    public LocationService locationService;
    private WifiInfo info;
    private IntentFilter mIntentFilter;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private boolean receiving = false;
    private SendMessageIntentService sendMessageIS;
    private RequestQueue queue;
    private DialogShow mDialogShow;
    private float d = 99999;
    private BluetoothBR mBTReceiver;
    private NotificationBR mMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        initViews();// initial the views
        initData(); // set all the needed vars

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //FLOATING BUTTON
        fab.setOnLongClickListener(this);
        fab.setOnClickListener(this);
        tab.setupWithViewPager(mViewPager);
        // get phone number from memory or user
        getPhoneNumber();

        /***************************************************
         * Treatment for opening activity from notification *
         ***************************************************/
        Intent i = getIntent();
        String action = i.getAction();
        int tabToOpen = i.getIntExtra("loc", -1);
        if (tabToOpen != -1) { //If was opened from Notification, extra will be given
            handleNotificationOpen(i, tabToOpen, action);
        } else {
            handleContactsReading();
        }

        //********************* Tabs Listener  ******************************
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


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
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


    //Function to calculate angle from (x,y) of 2 points
    private static double calculateAngle(double x1, double y1, double x2,
                                         double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        return (Math.atan2(dx, dy) * 180) / Math.PI;
    }

    /**
     * sets the location according to the requested method
     * @param source the method
     * @param myLoc the location JSON
     * @param otherLoc the other party's location JSON
     * @throws JSONException
     */
    public void selectLocationToShow(int source, JSONObject myLoc, JSONObject otherLoc) throws JSONException {
        if (locationService.getLastLocation() == null)
            return;
        currentLocation = locationService.getLastLocation();

        switch (source) {
            case Helper.GPS_SOURCE:
                try {
                    target.setLongitude(Float.valueOf(otherLoc.getString("lon")));
                    target.setLatitude(Float.valueOf(otherLoc.getString("lat")));
                    target.setAccuracy(currentLocation.getAccuracy());
                    setMethodTextView("GPS");
                    updateLocationView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case Helper.WIFI_SOURCE:
                //check if got real data
                boolean myLocError = myLoc.getString("error") == null;
                boolean otherLocError = otherLoc.getString("error") == null;
                //update needed data

                if (myLocError) {
                    setBestLocation(myLoc);
                }
                if (otherLocError) {
                    Log.i("ALGORITHM", "selectLocationToShow other location exist\n\t" + otherLoc.getString("error"));
                    setTargetToWifi(otherLoc);
                }
                updateLocationView();
                break;
        }
    }

    /**
     * set the target as Wi-Fi
     * @param loc the location to set as JSN
     * @throws JSONException
     */
    private void setTargetToWifi(JSONObject loc) throws JSONException {
        target.setLongitude(Float.valueOf(loc.getString("lon")));
        target.setLatitude(Float.valueOf(loc.getString("lat")));
        target.setAccuracy(Float.valueOf(loc.getString("accuracy")));

    }

    /**
     * select most accurate location between GPS and Wi-Fi
     * @param loc the Wi-Fi location JSON
     * @throws JSONException
     */
    private void setBestLocation(JSONObject loc) throws JSONException {
        currentLocation = locationService.getLastLocation();
        float locationAccuracy = currentLocation.getAccuracy();
        float wifiAccuracy = Float.valueOf(loc.getString("accuracy"));
        boolean smallerThan = locationAccuracy > wifiAccuracy;
        if (smallerThan) {
            setMethodTextView("Public Wifi");
            float lon = Float.valueOf(loc.getString("lon"));
            float lat = Float.valueOf(loc.getString("lat"));
            currentLocation.setLongitude(lon);
            currentLocation.setLatitude(lat);
            currentLocation.setAccuracy(wifiAccuracy);
        }
    }

    /**
     * the the location views on screen
     */
    private void updateLocationView() {
        d = currentLocation.distanceTo(target);
        JSONObject js = new JSONObject();
        try {
            js.put("distance", d);
            Helper.sendMessage(this, sendMessageIS, "message", memory.getString("to", ""), js.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (d < Helper.WIFI_DISTANCE)
            blueToothAndWifi();
        //Update friend's location
        setDistanceTextView(d);
        float acc = (currentLocation.getAccuracy() + target.getAccuracy()) / 2;
        setAccuracyTextView((int) acc);

    }

    public void setAccuracyTextView(int accuracy) {
        TextView m = (TextView) findViewById(R.id.textView);
        String loc = "Accuracy: " + accuracy + "m";
        m.setText(loc);
    }
    public void setDistanceTextView(float distance) {
        TextView tv1 = (TextView) findViewById(R.id.distanceText);
        String s = String.format("%.2f", distance);

        String dString = "Approx Distance: " + s + " m";
        tv1.setText(dString);
    }
    public void setMethodTextView(String method) {
        TextView tv1 = (TextView) findViewById(R.id.textView3);
        tv1.setText(method);
    }

    /**
     * function to get the wifis the from other end, and send both to server to get location
     * @param message the wifis
     */
    public void triangulation(String message) {
        String addr = memory.getString("WIFI-UUID", "");//get UUID
        List<ScanResult> resultList = wifi.getScanResults();//scan wifi
        //needed JSONs
        JSONArray jsonArray = new JSONArray();
        JSONObject jo = new JSONObject();
        JSONObject sendJson = new JSONObject();

        try {
            JSONObject tJo = new JSONObject(message);//create Json
            sendJson.put("data2", tJo);//add to the new JSON
            sendJson.put("from", memory.getString("to", ""));//add my phone
            sendJson.put("to", helper.encode(memory.getString("myphone", "")));//who to send to
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (ScanResult a : resultList) {
            JSONObject wifi = new JSONObject();//wifi JSON
            try {//create each wifi a json
                wifi.put("bssid", a.BSSID);
                wifi.put("signal", a.level);
                wifi.put("frequency", a.frequency);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //only from Marshmallow
                    wifi.put("channel", a.channelWidth);
                }
                jsonArray.put(wifi);//insert to a list
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!addr.equals("")) {
                if (a.BSSID.equals(addr)) {
                    //scheduled task to run wifi
                    double d = calculateDistance(a.level, a.frequency);
                    setMethodTextView("Wifi Strength");
                    setDistanceTextView((float) d);

                }

            }
        }
        try {
            /**
             * Get Cellular Data
             */
            TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tel.getNetworkOperator();

            if (!TextUtils.isEmpty(networkOperator)) {
                int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                int mnc = Integer.parseInt(networkOperator.substring(3));
                String network = tel.getNetworkOperator();
                int networkType = tel.getNetworkType();
                String netType = Helper.convertNetworkTpe(networkType);
                //add to JSON
                jo.put("homeMobileCountryCode", mcc);
                jo.put("homeMobileNetworkCode", mnc);
                jo.put("carrier", network);
                jo.put("radioType", netType);
            }
            //get cells ID
            JSONArray cellList = new JSONArray();
            List<NeighboringCellInfo> neighCells = tel.getNeighboringCellInfo();
            for (int i = 0; i < neighCells.size(); i++) {
                try {
                    JSONObject cellObj = new JSONObject();
                    NeighboringCellInfo thisCell = neighCells.get(i);
                    cellObj.put("cellId", thisCell.getCid());
                    cellList.put(cellObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cellList.length() > 0) {
                try {
                    jo.put("cellTowers", cellList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jo.put("found_wifi", jsonArray);
            sendJson.put("data", jo);
            Helper.sendMessage(getBaseContext(), sendMessageIS, "server", "", sendJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void stopGPS() {
        if (locationService != null)
            locationService.stopLocationServices();//stop location
    }
    //set views to search stopped
    private void setViews() {
        TextView m = (TextView) findViewById(R.id.textView);
        //change texts
        m.setText("Stopped Search");
        TextView locationText = (TextView) findViewById(R.id.distanceText);
        locationText.setText("");
        if (compassView != null)
            compassView.setAngle(-90);

    }

    private void stopReceivers() {
        //disable receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDialogShow);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBTReceiver);//close bt receiver
    }

    private void stopServices() {
//        stopService(service);//stop wifi
        unbindService(mConnection);
    }

    private void stopListeners() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(sensorEventListener);//unregister the arrow listener
    }

    private void stopBluetooth() {
        if (mBluetoothAdapter.isEnabled()) { //if bt enabled, close it
            mBluetoothAdapter.disable();
        }
    }
        //stop wifi access points
    private void stopAP() {
        Log.d("AP", "Ap is " + ApManager.isApOn(MainScreenActivity.this));
        if (ApManager.isApOn(MainScreenActivity.this))
            ApManager.configApState(MainScreenActivity.this); // change Ap state :boolean
        wifi.setWifiEnabled(true);
    }
    //stop wifi scanner
    private void stopWS() {
        if (ws != null)
            ws.stopSearch();
    }
    //clear session from the memory
    private void clearSessions() {
        memory.edit().putString("WIFI-UUID", "").apply();
    }
    // send to server end message
    private void sendEndSessionMessage() {
        Helper.sendMessage(this, sendMessageIS, "message", memory.getString("to", ""), Helper.STOP_SEARCH);//send stop message
        String session = memory.getString("session", "");
        Helper.sendMessage(this, sendMessageIS, "end", "", session);

    }

    /**
     * stopping the search mode
     */
    public void stopSearch() {
        setViews();
        receiving = false;
        mBound = false;
        setSearchStatus(false, 1);//disable search
        sendEndSessionMessage();
        stopGPS();
        stopWS();
        stopAP();
        clearSessions();
        stopBluetooth();
        stopListeners();
        stopReceivers();
        stopServices();
    }

    /**
     * Set colors and the icon of fab depends on search status
     * @param status the new search status
     * @param tab the tab to affect
     */
    private void setSearchStatus(boolean status, int tab) {
        if (tab == 0) {
            setFloatingActionButtonColors(fab, colorIntArray[BLUE], colorIntArray[PINK], iconIntArray[ADD]);
        } else {

            if (status) {
                setFloatingActionButtonColors(fab, colorIntArray[RED], colorIntArray[PINK], iconIntArray[PAUSE]);
            } else {
                setFloatingActionButtonColors(fab, colorIntArray[PINK], colorIntArray[BLUE], iconIntArray[PLAY]);
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
        if (primaryColor == colorIntArray[BLUE])
            color = BLUE;
        else
            color = PINK;
        toolbar.setBackgroundColor(getResources().getColor(colorIntArray2[color]));
        tab.setBackgroundColor(getResources().getColor(colorIntArray2[color]));

        ColorStateList colorStateList = new ColorStateList(states, colors);
        fab.setImageDrawable(getResources().getDrawable(icon));

    }

    /**
     * catch and handle results from popups
     *
     * @param requestCode = the opened popup
     * @param resultCode  = the result of the action
     * @param data        = extended data and info
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GPS_ON) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                GPS = true;
            else {
                stopSearch();
            }
        } else if (requestCode == WIFI_ON) {
            if (resultCode == RESULT_OK)
                WIFI = true;
        } else if (requestCode == BT_ON) {
            if (resultCode == RESULT_OK)
                BT = true;
        } else if (requestCode == BT_SHOW) {
            if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, R.string.bt_disabled, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, R.string.bt_started, Toast.LENGTH_LONG).show();
        }

    }
    //use Bluetooth RSSI method
    public void blueToothAndWifi() {
        if (mBluetoothAdapter != null) {
            //if bluetooth is on
            if (mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable :)
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                //check if bt discoverable
                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    //show the "get discoverable" activity
                    Intent discoverableIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(discoverableIntent, BT_SHOW);
                }
                String type = memory.getString("bt_status", "");
                if (type.equals("client")) {
                    // Register the BroadcastReceiver
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBTReceiver, filter); // Don't forget to unregister during onDestroy
                    mBluetoothAdapter.startDiscovery();
                } else if (type.equals("server")) {
                    BlueToohServer bs = new BlueToohServer(getBaseContext());
                }
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
            } else { //if bluetooth is off
                mBluetoothAdapter.enable();
            }
        }
    }
    //wifi scanner activation
    public void wifiScan(final String wifiBSSID) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> result = scheduler.scheduleAtFixedRate
                (new Runnable() {//run every 15 seconds
                    public void run() {
                        List<ScanResult> resultList = wifi.getScanResults();//get wifi around
                        for (ScanResult a : resultList) {
                            if (a.BSSID.equals(wifiBSSID)) {
                                double distance = calculateDistance(a.level, a.frequency);
                                if (distance < d) {
                                    setDistanceTextView((float) distance);
                                    setMethodTextView("WiFi Strength");
                                }
                            }

                        }
                    }
                }, 0, 7, TimeUnit.SECONDS);
    }

    /**
     * function to calculate wifi distance between two devices
     * using public Wifi RSSI
     */
    public void wifi() {

        mChannel = mManager.initialize(this, getMainLooper(), null);
        String type = memory.getString("bt_status", "");
        if (wifi.isWifiEnabled())
            if (!ApManager.isApOn(MainScreenActivity.this)) {
                if (type.equals("server")) {
                    if (!ApManager.isApOn(MainScreenActivity.this)) {
                        if (Build.VERSION.SDK_INT > 22)
                            if (!Settings.System.canWrite(getApplicationContext())) {
                                Log.d(Helper.WIFI_TAG, "Server Mode - need permissions");
                                Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                startActivity(grantIntent);
                            } else {
                                String address = ApManager.changeAPStateAndReturnSSID(MainScreenActivity.this); // change Ap state :boolean
                                if (address.compareTo("Failed") != 0) {
                                    Helper.sendMessage(this, sendMessageIS, "message", memory.getString("to", ""), "WIFI " + address);
                                }
                            }
                    }
                }
            }

    }

    /**
     * method to calculate approx distance
     *
     * @param signalLevelInDb - the RSSI
     * @param freqInMHz       - the web frequency
     * @return - approx distance
     */
    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }


    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        // Need to register again all broadcast receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Helper.MESSAGE_RECEIVER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDialogShow,
                new IntentFilter(Helper.SHOW_DIALOG));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Helper.BT_DATA));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Helper.WIFI_DATA));
        if (mSensorManager != null)
            mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        //Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDialogShow);
        flag = true;
    }

    // Alert message asking for user to enable GPS
    public void buildAlertMessageNoGps(final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String posType = type == GPS_ON ? "GPS" : Helper.WIFI_TAG;
        final String cancel = type == GPS_ON ? "Cancel Search" : "No Thanks";
        builder.setMessage("Your " + posType + " seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        switch (type) {
                            case GPS_ON:
                                if (!GPS)
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ON);
                                break;
                            case WIFI_ON:
                                if (!WIFI)
                                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), WIFI_ON);
                                break;
                        }
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if (type == GPS_ON) {
                            Toast.makeText(getApplicationContext(), "Search Disabled", Toast.LENGTH_LONG).show();
                            stopSearch();
                        } else {
                            Toast.makeText(getApplicationContext(), "WiFi Disabled", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Private function, used to set message jumping when floating button long pressed
    private void setMessage(final int loc) {
        switch (loc) {
            case 0:
                message = "Add Contact";
                break;
            case 1:
                if (mBound)
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
            case 0: //tab 0 selected, add contact
                setFloatingActionButtonColors(fab, colorIntArray[BLUE], colorIntArray[PINK], iconIntArray[ADD]);
                AddContactDialogFragment alert = new AddContactDialogFragment();
                alert.show(getFragmentManager(), null);
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getContacts();
                        if (memory.getBoolean("add", false)) {
                            ArrayList<String> l = new ArrayList<String>();
                            ArrayList<String> l2 = new ArrayList<String>();
                            l.add(memory.getString("to", ""));
                            l2.add(memory.getString("name", ""));
                            sendToServer(1, l, l2);
                        }
                    }
                });
                break;
            case 1: // tab 1 selected, stop location transmission
                m = (TextView) findViewById(R.id.textView);
                if (!mBound && !receiving) {
                    Snackbar.make(view, "Please Select a Contact", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if (mBound) {
                        try {
                            setSearchStatus(false, 1);
                            stopService(service);
                            mBound = false;
                            m.setText("Paused Location");
                            if (mSensorManager != null)
                                mSensorManager.unregisterListener(sensorEventListener);
                        } catch (SecurityException s) {
                            Log.e(TAG, "Security exception");
                        }
                    } else {
                        bindService(service, mConnection, Context.BIND_AUTO_CREATE);
                        setSearchStatus(true, 1);
                        if (mSensorManager != null)
                            mSensorManager.registerListener(sensorEventListener, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
                        m.setText("looking for location...");
                        mBound = true;

                    }
                    break;
                }
        }

    }

    @Override
    public boolean onLongClick(View v) {
        if (v == fab) {
            if (mBound) {
                stopSearch();
            } else {
                Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }
        return true;
    }

    //Function to calculate angle of the phone relative to friend's location using sensors
    private void getDirection() {
        compassView = (CompassView) findViewById(R.id.myView);
        double angle = 0;
        if (currentLocation == null)
            currentLocation = locationService.getLastLocation();
        angle = calculateAngle(currentLocation.getLongitude(), currentLocation.getLatitude(), target.getLongitude(), target.getLatitude());
//        angle = calculateAngle(currentLocation.getLongitude(), currentLocation.getLatitude(),31.776518, 35.199980);
        //Correction;
//        angle -= 90;
        //Correction for azimuth
        angle -= azimuth;
        while (angle < 0) angle = angle + 360;
        if (compassView != null) {
            compassView.angle = (float) angle;
            compassView.invalidate();
        }
    }

    //Sensor listener
    public void onSensorChanged(SensorEvent event) {
        if (!memory.getString("bt_status", "").equals("server")) {
            azimuth = event.values[0];
            getDirection();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
                int p = position == 0 ? 0 : position - 1;
                int pos = position;
                if (mBound && (position == 1))
                    pos = PAUSE;
                setFloatingActionButtonColors(fab, colorIntArray[position], colorIntArray2[p], iconIntArray[pos]);
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


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Unregister sensor listener
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDialogShow);
        stopService(service);
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }

    }

    // This function sending to GCM server list of contacts from phonebook and getting all registered friends,
    // adding them to list of contacts
    public void sendToServer(final int src, final ArrayList<String> phoneList, final ArrayList<String> names) {
        String serverAddr = Helper.SERVER_ADDR + "contacts";
        context = this;
        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        ArrayList<String> list = new ArrayList<String>();

        for (String i : phoneList) {
            list.add(helper.encode(i));
        }
        JSONArray arr = new JSONArray(list);
        JSONObject jsonBody = new JSONObject();
        try { //Got the list of friends, need to add them to contacts list
            jsonBody.put("contacts", arr);
        } catch (JSONException e) {
            Log.e(TAG, "json error" + e.getMessage());
        }


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (src == 1) {
            dialog.show();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                serverAddr,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.cancel();
                            JSONArray result = response.getJSONArray("response");
                            for (int i = 0; i < result.length(); i++) {
                                String phone = result.get(i).toString().replace("\n", "").trim();

                                for (int j = 0; j < phoneList.size(); j++) {
                                    if (phone.equals(helper.encode(phoneList.get(j)).trim())) {
                                        dal.addEntries(names.get(j), phoneList.get(j));
                                    }
                                }
                            }
                            //If user asked to add specific user, and he is not registered, ask if he want to invite him
                            if (result.length() == 0 && src == 1) {
                                inviteFriend();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error on response " + e.getMessage());
                        }
                        getContacts();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.cancel();
                        try {
                            Log.e(TAG, error.toString());
                        } catch (NullPointerException e) {
                            Log.e(TAG, "Volley Error");
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
    public void inviteFriend() {
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
                    if (pCur.moveToNext()) {
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


    //Function to update contacts list after adding to DB.
    private void getContacts() {
        cursorListView = (ListView) findViewById(R.id.cursorListView);
        Cursor cursor = dal.getAllTimeEntriesCursor();
        String[] entries = new String[]{Contacts.ContactsTable.userName, Contacts.ContactsTable.phoneNum, Contacts.ContactsTable.userID};
        int[] viewsID = new int[]{R.id.userNameTextView, R.id.userPhoneTextView, R.id.userIdTextView};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.contact, cursor, entries, viewsID, BIND_ABOVE_CLIENT);
        cursorListView.setAdapter(cursorAdapter);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
//the activities themselves
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
        private static final String ARG_SECTION_NUMBER = "section_number";
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private Cursor cursor;
        private DAL dal;
        private SimpleCursorAdapter cursorAdapter;
        private ListView cursorListView;
        private View rootView;
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

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.activity_contacts, container, false);
                updateList();
                cursorListView = (ListView) rootView.findViewById(R.id.cursorListView);
                cursorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        new AlertDialog.Builder(context)
                                .setMessage("Do you want to look for " + dal.getName(position) + "?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Helper.sendMessage(getContext(), null, "message", dal.getPhone(position), Helper.REQUEST);
                                        new AlertDialog.Builder(context)
                                                .setMessage("Request sent to " + dal.getName(position) + ". You will be notified when your friend reply")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }

                });

                cursorAdapter = new SimpleCursorAdapter(context, R.layout.contact, cursor, entries, viewsID, 0);
                cursorListView.setAdapter(cursorAdapter);
                return rootView;

            }
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_search, container, false);
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

        //Function to update contacts list after adding to DB in fragment class
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

    //init the main data needed for the onCreate
    private void initData() {
        service = new Intent(this, LocationService.class);
        memory = getSharedPreferences("currentLoc", MODE_PRIVATE);
        mBound = false;
        setSupportActionBar(toolbar);
        dal = new DAL(this);
        context = this;
        helper = new Helper();
        sensorEventListener = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        queue = Volley.newRequestQueue(context);
        sendMessageIS = new SendMessageIntentService("");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        target = new Location("");
        //set all broadcast receivers
        mDialogShow = new DialogShow();
        mBTReceiver = new BluetoothBR();
        mMessageReceiver = new NotificationBR();
        mMessageReceiver.setMainActivityHandler(this);
        mDialogShow.setMainActivityHandler(this);
        mBTReceiver.setMainActivityHandler(this);
    }

    //Initialize views
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tab = (TabLayout) findViewById(R.id.tabs);
        cursorListView = (ListView) findViewById(R.id.cursorListView);
        compassView = (CompassView) findViewById(R.id.myView);
    }
    //get user's phone
    private void getPhoneNumber() {
        /*************************************************************************/
        /*prompt for phone number and name only when application first installed */
        /*************************************************************************/
        if (memory.getString("myphone", "").isEmpty()) {
            FirstPageDialog alert = new FirstPageDialog();
            alert.setCancelable(false);
            alert.show(getFragmentManager(), null);
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //register with number and name that user gave
                    Helper.sendMessage(getBaseContext(), sendMessageIS, Helper.REGISTER, null, null);
                }
            });
        } else {
            Helper.sendMessage(getBaseContext(), sendMessageIS, Helper.REGISTER, null, null); //register with number and name that saved in SP
        }
    }

    /**
     * handles the notification clicks, opening the search or main page
     * @param i the calling intent
     * @param tabToOpen the tab to be opened to
     * @param action the notification we got
     */
    private void handleNotificationOpen(Intent i, int tabToOpen, String action) {
        NotificationManager nm = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (action.equals(Helper.MODE_APPROVE)) { // make sure action is approve
            m = (TextView) findViewById(R.id.textView);
            mViewPager.setCurrentItem(1);
            setSearchStatus(true, 1);
            setMessage(1);
            switch (tabToOpen) {
                case 1://when approving request
                    requestApproval(i, nm);
                    break;
                case 2:
                    nm.cancel(1); //clean notification
                    receiving = true;
                    break;
            }
            bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        } else if (action.equals(Helper.MODE_STOP)) {//if stop mode

            handleStopSearchRequest(nm);
        }
    }

    /**
     * closes the search when other user requested
     * @param nm the notification
     */
    public void handleStopSearchRequest(NotificationManager nm) {
        stopWS();
        closeSessionRequest();
        nm.cancel(2);//delete notification
        stopAP();
        clearSessions();
        stopServices();
        stopReceivers();
        setSearchStatus(false, 0);
        receiving = false;
        stopBluetooth();
        Toast.makeText(this, "Search stopped", Toast.LENGTH_SHORT).show();
    }

    /**
     * send request to close the session
     */
    private void closeSessionRequest() {
        String session = memory.getString("session", "");//get session
        Helper.sendMessage(this, sendMessageIS, "end", "", session);
    }

    /**
     * handles the approval by entering server mode and sending an approval message
     * @param i the calling intent
     * @param nm the notification pressed
     */
    private void requestApproval(Intent i, NotificationManager nm) {
        nm.cancel(0);
        String to = i.getStringExtra("to");
        SharedPreferences.Editor edit = memory.edit();
        edit.putString("to", to);//save recipient
        edit.putString("bt_status", "server");//change bluetooth mode to server
        edit.apply();
        Helper.sendMessage(getApplicationContext(), sendMessageIS, "message", to, Helper.APPROVED);//send approval message
        ws = new WifiScanner(getApplicationContext());
        ws.run();
    }


    private void handleContactsReading() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) { //android 5+ permission handling
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            Helper.MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                readContacts();
            }
        } else
            readContacts();
    }

}


