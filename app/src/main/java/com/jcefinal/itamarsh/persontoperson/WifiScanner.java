package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    public WifiScanner(Context c) {
        Log.d("ALGORITHM", "in WiFi Scanner");
        wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) c.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(c, c.getMainLooper(), null);
        this.context = c;
        memory = c.getApplicationContext().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        edit = memory.edit();
    }

    public void run() {
        Log.d("ALGORITHM", "RUN in WiFi Scanner");
        scheduler =
                Executors.newSingleThreadScheduledExecutor();
        result = scheduler.scheduleAtFixedRate
                (new Runnable() {//run every 15 seconds
                    public void run() {
                        Log.i(Helper.WIFI_TAG, "In WiFi Scanner");
                        List<ScanResult> resultList = wifi.getScanResults();//get wifi around
                        if (!checkIfExistInDB(resultList)) {// get in only if the list isnt the same as last
                            WIfiListDBHelper wifiDB = new WIfiListDBHelper(context);
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
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        wifi.put("channel", a.channelWidth);
                                        wifiDB.addWifi(a.BSSID, a.level, a.channelWidth, a.frequency);
                                    } else {
                                        wifiDB.addWifi(a.BSSID, a.level, -1, a.frequency);
                                    }
                                    json.put(wifi);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                jo.put("found_wifi", json);
                                Log.i(Helper.WIFI_TAG, "Sending to server:");
                                String toSend = jo.toString();
                                Log.i(Helper.WIFI_TAG, toSend);

                                Helper.sendMessage(context.getApplicationContext(), "wifi-message", memory.getString("to", ""), toSend);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, 0, 15, TimeUnit.SECONDS);

    }

    private boolean checkIfExistInDB(List<ScanResult> results) {
        WIfiListDBHelper wifiDB = new WIfiListDBHelper(context);
        Cursor c = wifiDB.getDB();
        Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level < rhs.level ? -1 : (lhs.level == rhs.level ? 0 : 1));
            }
        };
        Collections.sort(results, comparator);
        if (results.size() != c.getCount())
            return false;
        for (ScanResult s : results) {
            if (s.BSSID != c.getString(c.getPosition()))
                return false;

            try {
                c.moveToNext();
            } finally {
                c.close();
            }
        }
        return true;
    }

    public void stopSearch() {
        result.cancel(true);
    }
}
