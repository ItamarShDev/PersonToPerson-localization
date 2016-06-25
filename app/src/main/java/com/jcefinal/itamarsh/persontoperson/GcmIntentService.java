package com.jcefinal.itamarsh.persontoperson;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class responsible for treat of arriving GCM messages, using separate thread
 */
public class GcmIntentService extends IntentService {
    private static final int APPROVE_TYPE = 1, APPROVED_TYPE = 2, STOP_TYPE = 3;
    private static final int SEARCH_REFUSED = 3, SEARCH_STOPPED = 2, SEARCH_APPROVED = 1, NOTIFICATION = 0;
    private SharedPreferences memory;
    private SharedPreferences.Editor edit;
    private String Session = null;
    private Intent service, approve, refuse;
    private Context context;
    private TaskStackBuilder stackBuilder;
    private NotificationCompat.Builder builder;
    private String from, messageStr, fromPhone;
    private NotificationManager nm;
    private NotificationCompat.InboxStyle inboxStyle;
    private PendingIntent approvePendingIntent, refusePendingIntent;
    private DAL dal;
    private JSONObject responseJSON;


    public GcmIntentService() {

        super("GcmIntentService");
    }

    //***************************************************************************
    //                  Intent Service getting GCM message                      *
    // **************************************************************************
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                memory = getApplication().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
                edit = memory.edit();
                String m = extras.getString("message", "empty");
                if (m.compareTo("registered") == 0) {
                    Log.d(Helper.CONNECTION_TAG, "Registration to GCM server completed successfully");
                } else {
                    try {
                        JSONObject js = new JSONObject(m);
                        String str = js.getString("message");
                        if (js.has("session")) {
                            Session = js.getString("session");
                        }
                        if (str.contains("your_location")) {
                            wifiLocationeMessage(str);
                        } else if (str.contains("distance")) {
                            distanceMessage(str);
                        } else if (str.contains("found_wifi")) {
                            wifiListMessage(str);
                        } else if (str.contains("WIFI")) {
                            wifiMessage(str);
                        } else if (str.contains("UUID")) {
                            btMessage(str);
                        } else if (str.contains(",")) {
                            GPSMessage(str);
                        } else {
                            showNotification(extras.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("bundle", "json exception");
                        e.printStackTrace();
                    }
                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void distanceMessage(String str) {
        Intent intent = new Intent(Helper.WIFI_DATA);
        addSession(intent);
        intent.putExtra("distance", str);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Function send broadcast to main activity, to treat session message
    private void addSession(Intent i) {
        if (Session != null)
            i.putExtra("session", Session);
    }

    //Function send broadcast to main activity, to treat wifi message
    private void wifiMessage(String data) {
        Intent intent = new Intent(Helper.WIFI_DATA);
        addSession(intent);
        intent.putExtra("wifi_info", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //function to activate the Bluetooth receiver
    private void btMessage(String data) {
        Intent intent = new Intent(Helper.BT_DATA);
        addSession(intent);
        intent.putExtra("bt_info", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Function send broadcast to main activity, to treat location message
    private void wifiListMessage(String data) {
        Intent intent = new Intent(Helper.MESSAGE_RECEIVER);
        addSession(intent);
        intent.putExtra("wifiList", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Function send broadcast to main activity, to treat wifi based location message
    private void wifiLocationeMessage(String data) {
        Intent intent = new Intent(Helper.MESSAGE_RECEIVER);
        addSession(intent);
        intent.putExtra("wifiLocation", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Function send broadcast to main activity, to treat gps location message
    private void GPSMessage(String data) {
        Intent intent = new Intent(Helper.MESSAGE_RECEIVER);
        addSession(intent);
        intent.putExtra("gpsMessage", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void stopMessage() {
        Log.d("TEST", "in stopMessage");
        Intent intent = new Intent(Helper.MESSAGE_RECEIVER);
        intent.putExtra("stop", Helper.STOP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //Function show's notification and act depending on it's kind
    protected void showNotification(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //init needed vars
                context = getBaseContext();
                nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                inboxStyle = new NotificationCompat.InboxStyle();
                approve = new Intent(getBaseContext(), MainScreenActivity.class);
                refuse = new Intent(getBaseContext(), SendMessageIntentService.class);
                // init message strings
                from = "";
                messageStr = "";
                fromPhone = "";

                try { //read response
                    dal = new DAL(getBaseContext()); //create DAL
                    responseJSON = new JSONObject(message); //convert message to JSON
                    //save the needed strings
                    String s = responseJSON.getString("session");
                    Log.d("SESSION", "Got "+s);
                    edit.putString("session", s).apply();
                    fromPhone = responseJSON.getString("from");
                    from = dal.getName(fromPhone);
                    messageStr = responseJSON.getString("message");
                    //save to shared memory
                    edit.putString("to", fromPhone);
                    edit.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //intent to opening the location service
                service = new Intent(getApplicationContext(), LocationService.class);

                //Prepare notification builder
                builder = new NotificationCompat.Builder(context);
                builder.setDefaults(Notification.DEFAULT_ALL);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                    builder.setLargeIcon(bm);
                    builder.setSmallIcon(R.drawable.ic_message_black_24dp);
                } else {
                    builder.setSmallIcon(R.drawable.icon);
                }
                builder.setContentTitle("Find Your Friend");
                builder.setContentText(from + ": " + messageStr);
                builder.setAutoCancel(true);
                stackBuilder = TaskStackBuilder.create(getBaseContext());
                notificationHandler(messageStr);
            }
        });
    }

    /**
     * handler for showing and setting the notifications
     *
     * @param messageStr - the message received
     */
    private void notificationHandler(String messageStr) {
        switch (messageStr) {
            case Helper.REQUEST:  //got a request
                requestNotificationHandler();
                break;
            case Helper.APPROVED:  //user approved search
                searchApproved();
                break;
            case Helper.REFUSE: //refused search
                refusedSearch();
                break;
            case Helper.STOP_SEARCH: //search stopped by other user
                searchStopped();
                break;
        }
    }

    /**
     * sets the notification for the search request
     */
    private void requestNotificationHandler() {
        //set clicks
        setClickAction(APPROVE_TYPE);

        //add data
        approve.putExtra("to", fromPhone);

        //notification stack init
        stackBuilder.addParentStack(MainScreenActivity.class);
        //set click listener on approve button
        stackBuilder.addNextIntent(approve);
        initPendingIntent(NOTIFICATION);
        builder.addAction(NOTIFICATION, Helper.MODE_APPROVE, approvePendingIntent);

        refuse.setAction(Helper.MODE_REFUSE);
        refuse.putExtra("operation", "message");
        refuse.putExtra("to", fromPhone);
        refuse.putExtra("content", Helper.REFUSE);
        //set click listener on refuse button
        stackBuilder.addNextIntent(refuse);
        //init pendingIntents
        refusePendingIntent =
                PendingIntent.getService(getBaseContext(), SEARCH_REFUSED, refuse, PendingIntent.FLAG_CANCEL_CURRENT);
        //set priority
        builder.setPriority(Notification.PRIORITY_HIGH);
        //add click actions
        builder.addAction(NOTIFICATION, Helper.MODE_REFUSE, refusePendingIntent);
        nm.notify(NOTIFICATION, builder.build());
    }

    /**
     * sets the action on click
     *
     * @param type the action type
     */
    private void setClickAction(int type) {
        approve.putExtra("loc", type);
        if (type < STOP_TYPE)
            approve.setAction(Helper.MODE_APPROVE);
        else
            approve.setAction(Helper.MODE_STOP);

    }

    /**
     * handle search stopped
     */
    private void searchStopped() {
        stopMessage();
        getApplicationContext().stopService(service);
        //set click listener on notification
        inboxStyle.setBigContentTitle("Search Stopped");
        inboxStyle.addLine("From: " + from);
        inboxStyle.addLine(Helper.SEARCH_STOPPED);
        builder.setStyle(inboxStyle);
        setClickAction(STOP_TYPE);
        stackBuilder.addParentStack(MainScreenActivity.class);
        stackBuilder.addNextIntent(approve);
        nm.notify(SEARCH_STOPPED, builder.build());
        Helper.sendMessage(context, null, "end", "", memory.getString("session", ""));
        edit.remove("session");
    }

    private void searchApproved() {
        startService(service);
        //set click listener on notification
        setClickAction(APPROVED_TYPE);
        edit.putString("bt_status", "client");
        Log.d(Helper.BT_TAG, "entering client mode");
        edit.apply();
        stackBuilder.addParentStack(MainScreenActivity.class);
        stackBuilder.addNextIntent(approve);
        initPendingIntent(SEARCH_APPROVED);
        builder.setContentIntent(approvePendingIntent);
        nm.notify(SEARCH_APPROVED, builder.build());
    }

    private void refusedSearch() {
        //set click listener on notification
        inboxStyle.setBigContentTitle("Search Refused");
        inboxStyle.addLine("From: " + from);
        inboxStyle.addLine(messageStr);
        builder.setStyle(inboxStyle);
        stackBuilder.addParentStack(MainScreenActivity.class);
        nm.notify(SEARCH_REFUSED, builder.build());
        Helper.sendMessage(context, null, "end", "", memory.getString("session", ""));
        edit.remove("session");
    }

    private void initPendingIntent(int flag) {
        approvePendingIntent =
                stackBuilder.getPendingIntent(flag, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}