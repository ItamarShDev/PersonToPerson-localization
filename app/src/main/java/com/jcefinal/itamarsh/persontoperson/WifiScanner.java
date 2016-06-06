package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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


    public WifiScanner(Context c) {
        wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) c.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(c, c.getMainLooper(), null);
        this.context = c;
        memory = c.getApplicationContext().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        edit = memory.edit();
    }

    public void run() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        List<ScanResult> resultList = wifi.getScanResults();
                        JSONObject jo = new JSONObject();
                        try {
                            String token = InstanceID.getInstance(context).getToken(Helper.SENDER_ID, "GCM");
                            jo.put("from", token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray json = new JSONArray();
                        for (ScanResult a : resultList) {
                            JSONObject wifi = new JSONObject();
                            try {
                                wifi.put("bssid", a.BSSID);
                                wifi.put("signal", a.level);
                                wifi.put("frequency", a.frequency);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    wifi.put("channel", a.channelWidth);
                                }
                                json.put(wifi);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            jo.put("data", json);
                            Log.i(Helper.WIFI_TAG, json.toString());
                            String session = memory.getString("session", "");
                            jo.put("data", session);
                            Helper.sendMessage(context.getApplicationContext(), "wifi", "", jo.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, 0, 5, TimeUnit.SECONDS);
    }
}
