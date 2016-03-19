package com.jcefinal.itamarsh.persontoperson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private List peerList = new ArrayList();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainScreenActivity mActivity;
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            // Out with the old, in with the new.
            peerList.clear();
            peerList.addAll(peers.getDeviceList());

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
            if (peerList.size() == 0) {
                Log.d("WiFiDirectActivity", "No devices found");
                return;
            } else {
                Log.d("WiFiDirectActivity", "found " + peerList.size());

            }
        }

    };
    public WiFiDirectBroadcastReceiver() {
        super();
    }
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainScreenActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                WifiP2pDevice device = (WifiP2pDevice) peerList.get(0);

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                Log.i("P2P", "P2P Enabled");
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        // Code for when the discovery initiation is successful goes here.
                        // No services have actually been discovered yet, so this method
                        // can often be left blank.  Code for peer discovery goes in the
                        // onReceive method, detailed below.
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        // Code for when the discovery initiation fails goes here.
                        // Alert the user that something went wrong.
                    }
                });
            } else {
                // Wi-Fi P2P is not enabled
                Log.i("P2P", "P2P Disabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            // Respond to this device's wifi state changing
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            String thisDeviceName = device.deviceAddress;
            Log.i("P2P", "My Device Name "+thisDeviceName);
       }
    }
}
//f2:27:65:d4:c3:81