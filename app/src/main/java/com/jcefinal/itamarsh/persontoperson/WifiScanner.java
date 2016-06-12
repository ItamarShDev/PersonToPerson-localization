package com.jcefinal.itamarsh.persontoperson;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    public WifiScanner(Context c) {
        Log.d("ALGORITHM", "in WiFi Scanner");
        wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) c.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(c, c.getMainLooper(), null);
        this.context = c;
        memory = c.getApplicationContext().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        edit = memory.edit();
        wifiDB = new WIfiListDBHelper(context);

    }

    public void run() {
        Log.d("ALGORITHM", "RUN in WiFi Scanner");
        scheduler =
                Executors.newSingleThreadScheduledExecutor();
        result = scheduler.scheduleAtFixedRate
                (new Runnable() {//run every 15 seconds
                    public void run() {
                        Log.d(Helper.WIFI_TAG, "In WiFi Scanner");
                        List<ScanResult> resultList = wifi.getScanResults();//get wifi around
                        if (!checkIfExistInDB(resultList)) {// get in only if the list isnt the same as last

                            try {
                                String token = InstanceID.getInstance(context).getToken(Helper.SENDER_ID, "GCM");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            JSONArray json = new JSONArray();
                            JSONObject jo = new JSONObject();

                            for (ScanResult a : resultList) {

                                JSONObject wifi = new JSONObject();
                                try {
                                    wifi.put("bssid", a.BSSID);
                                    wifi.put("signal", a.level);
                                    wifi.put("frequency", a.frequency);
                                    Log.d("WifiDB", "adding wifi");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        wifi.put("channel", a.channelWidth);
                                        addWifi(a.BSSID, a.level, a.channelWidth, a.frequency);
                                    } else {
                                        addWifi(a.BSSID, a.level, -1, a.frequency);
                                    }
                                    json.put(wifi);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                jo.put("found_wifi", json);
                                Log.d(Helper.WIFI_TAG, "Sending to server:");
                                String toSend = jo.toString();
                                Log.d(Helper.WIFI_TAG, toSend);

//                                Helper.sendMessage(context.getApplicationContext(), "wifi-message", memory.getString("to", ""), toSend);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, 0, 5, TimeUnit.SECONDS);

    }

    private boolean checkIfExistInDB(List<ScanResult> results) {
        Log.d("WifiDB", "checking existence");
//        wifiDB = new WIfiListDBHelper(context);
        db = wifiDB.getReadableDatabase();
        Log.d("WifiDB", "after getReadableDatabase");
        Cursor c = getDB();
        int count = c.getCount();
        Log.d("WifiDB", "in db cursor count:" + count);
        boolean ret = true;
        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level < rhs.level ? -1 : (lhs.level == rhs.level ? 0 : 1));
            }
        };
        Collections.sort(results, comparator);
        if (results.size() != count) {
            ret = false;
        } else {


            Log.d("WifiDB", "comparing lists");

            try {
                while (c.moveToNext()) {
                    ScanResult s = results.get(c.getPosition());
                    if (!s.BSSID.equals(c.getString(c.getPosition()))) {
                        ret = false;
                        Log.d("WifiDB", s.BSSID + " != " + c.getString(c.getPosition()));
                        break;
                    }

                }
            } finally {
                c.close();
            }

//            for (ScanResult s : results) {
//                Log.d("WifiDB", "counter "+c.getPosition());
//
//                try {
////                    c.moveToNext();
//                    cc++;
//                } finally {
//                    c.close();
//                }
//            }
        }
        if (!ret)
            wifiDB.reserDb(db);
        return ret;
    }

    public Cursor getDB() {
        Log.d("WifiDB", "WIfiListDBHelper getDB");
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                WifiList.WifiTable._ID,
                WifiList.WifiTable.BSSID,
                WifiList.WifiTable.SIGNAL
        };


// How you want the results sorted in the resulting Cursor

        Cursor c = db.query(
                WifiList.WifiTable.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                WifiList.WifiTable.SIGNAL                                 // The sort order
        );
        Log.d("WifiDB", "WIfiListDBHelper getDB after query");

        c.moveToFirst();
        return c;
    }

    public long addWifi(String bssid, int signal, int channel, int frequency) {
        // Gets the data repository in write mode
        db = wifiDB.getWritableDatabase();
        Log.d("WifiDB", "saving to db");
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
            Log.d("WifiDB", "got " + id + " " + values.toString());
        }

        return newRowId;
    }

    public void stopSearch() {
        result.cancel(true);
        db = wifiDB.getWritableDatabase();
        wifiDB.reserDb(db);
    }
}
