package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by olesya on 23-Dec-15
 * This class have global functions that can be used in all other classes
 */
public class Helper {
    public static final String REQUEST = "I Want to Search For You",
            APPROVED = "Approved the search ",
            REFUSE = "Sorry friend. I'm hiding from you ;)",
            STOP_SEARCH = "Search Stopped",
            SEARCH_STOPPED = "The Search Has Been Stopped by the User";
    public static final int MY_SOCKET_TIMEOUT_MS = 10000;
    public static final String SERVER_ADDR = "http://p2p-gcm-server2.appspot.com/";
    public static final int BT_DISTANCE = 150, WIFI_DISTANCE = 30;
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    public static final String CONNECTION_TAG = "myDebug",
            LOCATION_TAG = "GPS-Debug",
            BT_TAG = "BlueTooth-Debug";
    public static final String BT_DATA = "bluetooth",
            WIFI_DATA = "wifi",
            REGISTER = "register",
            MODE_APPROVE = "approve",
            MODE_REFUSE = "refuse",
            MODE_STOP = "stop_search",
            MESSAGE_RECEIVER = "my-event",
            SHOW_DIALOG = "show-dialog";

    //This function call to send message intent service with arguments that was sent to function
    public static void sendMessage(Context context, String op, String to, String content) {
        Intent msgIntent = new Intent(context, SendMessageIntentService.class);
        Log.d(CONNECTION_TAG, "Helper.sendMessage, " +
                "content " + content +
                " op " + op +
                " to " + to);
        msgIntent.putExtra("operation", op);
        msgIntent.putExtra("to", to);
        msgIntent.putExtra("content", content);
        context.startService(msgIntent);
    }

    /* This function responsible for encoding string to SHA-256*/
    public String encode(String num)
    {
        String hashString = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(num.getBytes());
            byte[] digest = md.digest();
            hashString =  Base64.encodeToString(digest, Base64.DEFAULT);
        }
        catch (NoSuchAlgorithmException e)
        {}
        return hashString;
    }
}
