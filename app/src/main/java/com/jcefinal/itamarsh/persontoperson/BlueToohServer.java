package com.jcefinal.itamarsh.persontoperson;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by itamar on 19-Mar-16.
 */
public class BlueToohServer extends Thread {
    private static final UUID MY_UUID = UUID.randomUUID();
    private final BluetoothServerSocket mmServerSocket;
    private final RequestQueue queue;
    private final SendMessageIntentService sendMessageIS;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private SharedPreferences memory;

    public BlueToohServer(Context c) {
        memory = c.getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        Log.d(Helper.BT_TAG, "In Server with " + MY_UUID);
        queue = Volley.newRequestQueue(c);
        sendMessageIS = new SendMessageIntentService("");

        Helper.sendMessage(c,sendMessageIS, "message", memory.getString("to", ""), "UUID " + MY_UUID);
        BluetoothServerSocket tmp = null;

        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(getName(), MY_UUID);
        } catch (IOException e) {
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            Log.d(Helper.BT_TAG, "accepted " + socket.isConnected());
            // If a connection was accepted
            if (socket != null) {
                //TODO Do work to manage the connection (in a separate thread)
//                    manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
        }
    }
}
