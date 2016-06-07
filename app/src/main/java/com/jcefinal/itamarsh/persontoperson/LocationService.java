package com.jcefinal.itamarsh.persontoperson;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/*
 * This class responsible for running location listener, getting updates, in background
 */
public class LocationService extends Service implements LocationListener {
    private static final String TAG = "LocationService";
    private final static int NETWORK_ON = 0, GPS_ON = 2;
    private final IBinder mBinder = new LocalBinder();
    private int WIFI_ON = 1;
    private Location currentLocation;
    private boolean gpsOn, networkOn;
    private SharedPreferences memory;
    private LocationListener locListener;
    private LocationManager locationManager;

    public LocationService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locListener = this;
        memory = getSharedPreferences("currentLoc", MODE_PRIVATE);
        gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.i(TAG, "GPS is " + gpsOn);
        if (!gpsOn) {
            showDialog(GPS_ON);
        }
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        final String locationProvider = locationManager.getBestProvider(c, true);
        try {
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locListener);

        } catch (SecurityException s) {
            Log.i(TAG, "Security Exception");

        }

        return super.onStartCommand(intent, flags, startId);
    }

    //This function will call function from main activity - to show dialog
    private void showDialog(int type) {
        Intent intent = new Intent("show-dialog");
        intent.putExtra("type", type);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Location getLastLocation() {
        return currentLocation;
    }

    public void stopLocationServices() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "OnDestroy");
        locListener = this;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        String type = memory.getString("bt_status", "");
        // Called when a new location is found by the network location provider.
        if (type.equals("server"))
            Helper.sendMessage(getBaseContext(), "message", memory.getString("to", ""), location.getLongitude() + "," + location.getLatitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }
}
