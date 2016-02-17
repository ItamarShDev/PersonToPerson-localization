package com.jcefinal.itamarsh.persontoperson;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
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

import java.util.ArrayList;


public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, SensorEventListener {
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    /*Define variables */
    private WifiP2pManager mManager;
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
    private CompassView compassView;
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private float azimuth = 0; // degree
    private static final String TAG = "myDebug";
    private SharedPreferences memory;
    private Location currentLocation, target;
    private Context context;
    boolean gpsOn, networkOn;
    private Helper helper;
    private ProgressDialog dialog;
    private boolean flag = true;
    private SensorEventListener sensorEventListener;
    private final static int NETWORK_ON=0, WIFI_ON =1, GPS_ON = 2,BT_ON = 3;

    private ViewPager mViewPager; //The {@link ViewPager} that will host the section contents.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        initViews();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        memory = getSharedPreferences("currentLoc", MODE_PRIVATE);
        play = false;
        setSupportActionBar(toolbar);
        dal = new DAL(this);
        context = this;
        helper = new Helper();
        sensorEventListener = this;


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        target = new Location("");
        //FLOATING BUTTON
        fab.setOnLongClickListener(this);
        fab.setOnClickListener(this);
        tab.setupWithViewPager(mViewPager);

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
            if(action.equals("approve")) { // make sure action is approve
                m = (TextView) findViewById(R.id.textView);
                mViewPager.setCurrentItem(1);
                fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[2]));
                fab.setImageDrawable(getResources().getDrawable(iconIntArray[2]));
                play = true;
                if(tabToOpen == 1) {
                    String to = memory.getString("to", "");
                    sendMessage(getApplicationContext(), "message", to, Helper.APPROVED);
                }
                gpsLookout();
            }
        }

        //********************* Tab Listener  ******************************
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
                    sendMessage(getBaseContext(),"register", null, null);
                }
            });
        }
        else {
            sendMessage(getBaseContext(), "register", null, null);
        }
        if (tabToOpen == -1)
            readContacts();
    }
    //****************************************************************
    //*        Treatment for notification with location
    //****************************************************************
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
            if(currentLocation != null){
                String location[] = message.split(",");
                String loc = "\nYour Location: \n" + String.format("%.2f",currentLocation.getLongitude()) + ","
                        + String.format("%.2f", currentLocation.getLatitude());
                target.setLongitude(Float.valueOf(location[0]));
                target.setLatitude(Float.valueOf(location[1]));
                float l1 = Float.valueOf(location[0]);
                float l2 = Float.valueOf(location[1]);
                d = currentLocation.distanceTo(target);
                Log.i("DISTANCE", "" + d);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
      //  mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        flag = true;
        super.onPause();
    }

    //Private function, used to set message jumping when floating button long pressed
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

    //*********** Listener for floating button *************
    @Override
    public void onClick(final View view) {

        switch (mViewPager.getCurrentItem()) {
            case 0: //tab 0 selected, add contact
                AddContactDialogFragment alert = new AddContactDialogFragment();
                alert.show(getFragmentManager(), null);

                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.e("myDebug", "dialog on dismiss");
                        getContacts();
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
                currentLocation = location;
                // Called when a new location is found by the network location provider.
                sendMessage(getBaseContext() ,"message", memory.getString("to", ""), location.getLongitude() + "," + location.getLatitude());
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
                        currentLocation = locationManager.getLastKnownLocation(locationProvider);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    } catch (SecurityException s) {
                    }
                }

            }
        });
    }

    private void getDirection()
    {
        compassView = (CompassView)findViewById(R.id.myView);
        double angle = 0;
        if(currentLocation != null) {
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

    public void onSensorChanged(SensorEvent event) {
        azimuth = event.values[0];
        Log.i("mydebug","azimuth " + azimuth);
        getDirection();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static double calculateAngle(double x1, double y1, double x2,
                                        double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        return (Math.atan2(dx, dy) * 180) / Math.PI;
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
    public  static  void  sendMessage(Context context, String op, String to, String content)
    {
        Intent msgIntent = new Intent(context, SendMessageIntentService.class);
        Log.i(TAG, "in sendMessage, content" + content);
        msgIntent.putExtra("operation", op);
        msgIntent.putExtra("to", to);
        msgIntent.putExtra("content", content);
        context.startService(msgIntent);
    }

    public void sendToServer(final int src, final ArrayList<String> phoneList, final ArrayList<String> names){
        String serverAddr = "http://p2p-gcm-server.appspot.com/contacts";
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
        try {
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
                                        Log.i(TAG, "you did it!");
                                        dal.addEntries(names.get(j), phoneList.get(j));
                                    }
                                }
                            }
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
                        Toast.makeText(context, "Got error", Toast.LENGTH_LONG).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("REQUEST");
        queue.add(request);
    }

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
        for (String name : listNames)
            Log.i(TAG, "Contact Name:" + name);
        for(String pho: listPhones)
            Log.i(TAG, "Phone number" + pho);

        sendToServer(2, listPhones, listNames);

        cur.close();
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
        private Cursor cursor;
        private DAL dal;
        private SimpleCursorAdapter cursorAdapter;
        private static final String ARG_SECTION_NUMBER = "section_number";
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
                                        sendMessage(getContext(), "message", dal.getPhone(position), Helper.REQUEST);
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

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tab = (TabLayout) findViewById(R.id.tabs);
        cursorListView = (ListView) findViewById(R.id.cursorListView);
        compassView = (CompassView)findViewById(R.id.myView);

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


