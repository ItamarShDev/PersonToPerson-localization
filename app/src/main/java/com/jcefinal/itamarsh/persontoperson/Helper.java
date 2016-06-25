package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by olesya on 23-Dec-15
 * This class have global functions that can be used in all other classes
 */
public class Helper {
    public final static int GPS_SOURCE = 0, WIFI_SOURCE = 1;
    public static final String SENDER_ID = "186698592995";
    public static final String REQUEST = "I Want to Search For You",
            APPROVED = "Approved the search ",
            REFUSE = "Sorry friend. I'm hiding from you ;)",
            STOP_SEARCH = "Search Stopped",
            SEARCH_STOPPED = "The Search Has Been Stopped by the User";
    public static final int MY_SOCKET_TIMEOUT_MS = 10000;
    public static final String SERVER_ADDR = "http://p2p-gcm-server3.appspot.com/";
    public static final int BT_DISTANCE = 20, WIFI_DISTANCE = 300;
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    public static final String CONNECTION_TAG = "myDebug",
            LOCATION_TAG = "GPS-Debug",
            WIFI_TAG = "WIFI-Debug",
            BT_TAG = "BlueTooth-";
    public static final String BT_DATA = "bluetooth",
            WIFI_DATA = "wifi",
            REGISTER = "register",
            MODE_APPROVE = "approve",
            MODE_REFUSE = "refuse",
            MODE_STOP = "stop_search",
            MESSAGE_RECEIVER = "my-event",
            STOP = "stop",
            SHOW_DIALOG = "show-dialog";

    //This function call to send message intent service with arguments that was sent to function
    public static void sendMessage(Context context, SendMessageIntentService sendMessageIS, String op, String to, String content) {
        Intent msgIntent;
        if (sendMessageIS == null)
            msgIntent = new Intent(context, SendMessageIntentService.class);
        else
            msgIntent = new Intent(context, sendMessageIS.getClass());
        msgIntent.putExtra("operation", op);
        msgIntent.putExtra("to", to);
        msgIntent.putExtra("content", content);
        context.startService(msgIntent);
    }

    /* This function responsible for encoding string to SHA-256*/
    public String encode(String num) {
        String hashString = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(num.getBytes());
            byte[] digest = md.digest();
            hashString = Base64.encodeToString(digest, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
        }
        return hashString;
    }
    public static String convertNetworkTpe(int networkType){
        switch (networkType) {
            case 4:
                return  "cdma";
            case 1:
                return "gsm";

            case 8:
                return "gsm";
            case 10:
                return "gsm";
            case 15:
                return "gsm";
            case 9:
                return "gsm";
            case 13:
                return "lte";
            case 3:
                return "wcdma";
            case 0:
                return "Unknown";
        }
        return "";
    }
}
