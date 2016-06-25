package com.jcefinal.itamarsh.persontoperson;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.UUID;

public class BluetoothBR extends BroadcastReceiver {
    private MainScreenActivity main = null;
    void setMainActivityHandler(MainScreenActivity main){
        this.main=main;
    }


    public BluetoothBR() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences memory = context.getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            // Get the BluetoothDevice object from the Intent


            // Add the name and address to an array adapter to show in a ListView
            if (!memory.getString("BT-UUID", "").equals("")) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String uuidStr = memory.getString("BT-UUID", "").replace(" ", "");
                    Log.d(Helper.BT_TAG, "Got UUID "+uuidStr);
                    UUID uuid = UUID.fromString(uuidStr);
                    Log.d(Helper.BT_TAG, "UUID "+uuid.toString());
                    Log.d(Helper.BT_TAG, ""+device.getName());
                    for (ParcelUuid u : device.getUuids()) {
                        if (u.getUuid().compareTo(uuid) == 0) {
                            ConnectThread ct = new ConnectThread(device, uuid);
                            ct.run();
                        }
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(Helper.BT_TAG, e.getMessage());
                }

                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                this.main.setMethodTextView("Bluetooth Strength");
            }
        }
    }
}
