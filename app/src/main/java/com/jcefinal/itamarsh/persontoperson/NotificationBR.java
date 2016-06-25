package com.jcefinal.itamarsh.persontoperson;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationBR extends BroadcastReceiver {
    private MainScreenActivity main = null;

    void setMainActivityHandler(MainScreenActivity main) {
        this.main = main;
    }

    public NotificationBR() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!main.mBound)
            return;
        main.currentLocation = main.locationService.getLastLocation();//get current location
        if (main.flag) {
            //initialize all sensors
            main.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            main.mOrientation = main.mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            main.mSensorManager.registerListener(main.sensorEventListener, main.mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
            main.flag = false;
        }
        SharedPreferences memory = context.getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = memory.edit();
        // Extract data included in the Intent
        String stopIntent = intent.getStringExtra(Helper.STOP);
        String distance = intent.getStringExtra("distance"); //got distance
        String message = intent.getStringExtra("gpsMessage"); //got gps location
        String btMessage = intent.getStringExtra("bt_info"); //got bluetooth info
        String wifiMessage = intent.getStringExtra("wifi_info");//got wifi info
        String session = intent.getStringExtra("session");//opens new session
        String wifiLocation = intent.getStringExtra("wifiLocation");//got wifi based location
        String wifiList = intent.getStringExtra("wifiList");//got wifi list
        boolean hasWifis = wifiList != null && wifiList.contains("found_wifi");
        if (stopIntent != null) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (main.isVisible)
                main.stopSearch();
            else
                main.handleStopSearchRequest(nm);
        } else {


            if (session != null) {
                edit.putString("session", session).apply();
            }
            if (distance != null) {
                if (!main.wifiOn) {
                    main.wifi();
                    main.wifiOn = true;
                }
                main.blueToothAndWifi();
            } else if (btMessage != null) {
                btMessage = btMessage.split("UUID")[1];
                edit.putString("BT-UUID", btMessage).apply();

            } else if (wifiMessage != null) {

                wifiMessage = wifiMessage.split("WIFI ")[1];
                edit.putString("WIFI-UUID", wifiMessage).apply();
                main.wifiScan(wifiMessage);
            } else if (wifiList != null) {
                main.triangulation(wifiList);
            } else if (wifiLocation != null) {
                try {
                    JSONObject loc = new JSONObject(wifiLocation);
                    JSONObject myLoc = loc.getJSONObject("your_location");
                    JSONObject otherLoc = loc.getJSONObject("friend_location");
                    main.selectLocationToShow(Helper.WIFI_SOURCE, myLoc, otherLoc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONObject jsonMessage = new JSONObject();
                String location[] = message.split(",");
                try {
                    jsonMessage.put("lon", location[0]);
                    jsonMessage.put("lat", location[1]);
                    main.selectLocationToShow(Helper.GPS_SOURCE, null, jsonMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
