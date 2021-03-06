package com.jcefinal.itamarsh.persontoperson;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by itamar on 06-Jun-16.
 */
public class WifiScanner extends Thread {
    private WifiManager wifi;
    private GoogleCloudMessaging gcm;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Context context;
    private SharedPreferences memory;
    private SharedPreferences.Editor edit;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> result;
    private WIfiListDBHelper wifiDB;
    private SQLiteDatabase db;
    private Location current;
    private Intent service;
    private SendMessageIntentService sendMessageIS;

    //constructor
    public WifiScanner(Context c) {
        // init services
        wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) c.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(c, c.getMainLooper(), null);
        this.context = c; //save application context
        // read from memory
        memory = c.getApplicationContext().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        //create wifi helper
        wifiDB = new WIfiListDBHelper(context);
        // create service intent
        service = new Intent(c.getApplicationContext(), LocationService.class);

    }

    /**
     * scanner main method
     */
    public void run() {
        context.startService(service);//start location service
        sendMessageIS = new SendMessageIntentService("");//create send message reference
        //prepare to run in scheduled periods
        scheduler = Executors.newSingleThreadScheduledExecutor();
        result = scheduler.scheduleAtFixedRate
                (new Runnable() {//run every 15 seconds
                    public void run() {
                        List<ScanResult> resultList = wifi.getScanResults();//get wifi around
                        if (!checkIfExistInDB(resultList)) {// get in only if the list isnt the same as last
                            JSONArray json = new JSONArray();
                            JSONObject jo = new JSONObject();

                            for (ScanResult a : resultList) {//cycle results
                                //save to JSON
                                JSONObject wifi = new JSONObject();
                                try {
                                    wifi.put("bssid", a.BSSID);
                                    wifi.put("signal", a.level);
                                    wifi.put("frequency", a.frequency);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        wifi.put("channel", a.channelWidth);
                                        addWifi(a.BSSID, a.level, a.channelWidth, a.frequency);
                                    } else {
                                        addWifi(a.BSSID, a.level, -1, a.frequency);
                                    }
                                    json.put(wifi);//save to main json
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                jo.put("found_wifi", json);

                                TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                                String networkOperator = tel.getNetworkOperator();

                                if (!TextUtils.isEmpty(networkOperator)) {
                                    int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                                    int mnc = Integer.parseInt(networkOperator.substring(3));
                                    String network = tel.getNetworkOperator();
                                    int networkType = tel.getNetworkType();
                                    String netType = networkConversion(networkType);

                                    jo.put("homeMobileCountryCode", mcc);
                                    jo.put("homeMobileNetworkCode", mnc);
                                    jo.put("carrier", network);
                                    jo.put("radioType", netType);
                                }

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
                                String toSend = jo.toString();
                                Helper.sendMessage(context.getApplicationContext(), sendMessageIS, "wifi-message", memory.getString("to", ""), toSend);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, 0, 7, TimeUnit.SECONDS);

    }

    /**
     * returns the string by number given from OS
     *
     * @param networkType - the network type represented with a constant int
     * @return a string represents the type
     */
    private String networkConversion(int networkType) {

        String netType = "";
        switch (networkType) {
            case 0:
                netType = "Unknown";
                break;
            case 1:
                netType = "gsm";
                break;
            case 3:
                netType = "wcdma";
                break;
            case 4:
                netType = "cdma";
                break;
            case 8:
                netType = "gsm";
                break;
            case 9:
                netType = "gsm";
                break;
            case 10:
                netType = "gsm";
                break;
            case 13:
                netType = "lte";
                break;
            case 15:
                netType = "gsm";
                break;
        }
        return netType;
    }

    /**
     * check if the scanned wifi list is already in db
     *
     * @param results the scannes networks
     * @return true if exist
     */
    private boolean checkIfExistInDB(List<ScanResult> results) {
        //init
        db = wifiDB.getReadableDatabase();
        Cursor c = getDB();
        int count = c.getCount();
        boolean ret = true;
        //create comparator
        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                //mark equals or not
                return ((lhs.BSSID.compareTo(rhs.BSSID) < 0) ? -1 : lhs.BSSID.equals(rhs.BSSID) ? 0 : 1);
            }
        };
        Collections.sort(results, comparator);//sort by MAC
        //if not the same length, not equal
        if (results.size() != count) {
            ret = false;
        } else {
            try {//iterate through all the list and compare
                while (c.moveToNext()) {
                    ScanResult s = results.get(c.getPosition());
                    if (s.level != c.getInt(0)) {
                        ret = false;
                        break;
                    } else {
                        ret = true;
                    }
                }
            } finally {
                c.close();
            }
        }
        if (!ret)//if not the same, reset the db
            wifiDB.resetDb(db);
        return ret;
    }

    public Cursor getDB() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                WifiList.WifiTable.SIGNAL,
                WifiList.WifiTable.BSSID
        };


// How you want the results sorted in the resulting Cursor

        Cursor c = db.query(
                WifiList.WifiTable.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                WifiList.WifiTable.BSSID                                 // The sort order
        );
        c.moveToFirst();

        return c;
    }

    public long addWifi(String bssid, int signal, int channel, int frequency) {
        // Gets the data repository in write mode
        db = wifiDB.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WifiList.WifiTable.BSSID, bssid);
        values.put(WifiList.WifiTable.FREQUENCY, frequency);
        values.put(WifiList.WifiTable.SIGNAL, signal);
        long newRowId = -1;
        if (channel > -1)
            values.put(WifiList.WifiTable.CHANNEL, channel);
        int id = (int) db.insertWithOnConflict(WifiList.WifiTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            newRowId = db.update(WifiList.WifiTable.TABLE_NAME, values, "BSSID=?", new String[]{bssid});
        } else {
        }

        return newRowId;
    }

    /**
     * stop wifi search
     */
    public void stopSearch() {
        result.cancel(true);
        db = wifiDB.getWritableDatabase();
        wifiDB.resetDb(db);
        wifiDB.close();
    }
}
