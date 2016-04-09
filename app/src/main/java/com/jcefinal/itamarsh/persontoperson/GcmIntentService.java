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

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class responsible for treat of arriving GCM messages, using seperate thread
 */
public class GcmIntentService extends IntentService {
    private SharedPreferences memory;
    private SharedPreferences.Editor edit;

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
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                String m = extras.getString("message", "empty");
                Log.i(Helper.CONNECTION_TAG, "Got message\nMessage: " + m);
                if (m.compareTo("registered") == 0) {
                    Log.i(Helper.CONNECTION_TAG, "Registration to GCM server completed successfully");
                } else {
                    try {
                        JSONObject js = new JSONObject(m);
                        String str = js.getString("message");
                        if (str.contains(",")) {
                            sendMessage(str);
                        } else if (str.contains("UUID")) {
                            btMessage(str);
                        } else {
                            showNotification(extras.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.i("bundle", "json exception");
                        e.printStackTrace();
                    }
                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    //function to activate the Bluetooth receiver
    private void btMessage(String data) {
        Log.d(Helper.BT_TAG, "GcmIntentService in btMessage " + data);
        Intent intent = new Intent(Helper.BT_DATA);
        intent.putExtra("info", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //Function send broadcast to main activity, to treat location message
    private void sendMessage(String data) {
        Intent intent = new Intent(Helper.MESSAGE_RECEIVER);
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Function show's notification and act depending on it's kind
    protected void showNotification(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context c = getBaseContext();
                String from = "", m = "", fromPhone = "";
                try {
                    JSONObject responseJSON;
                    DAL dal = new DAL(getBaseContext());

                    responseJSON = new JSONObject(message);
                    fromPhone = responseJSON.getString("from");
                    from = dal.getName(fromPhone);
                    m = responseJSON.getString("message");
                    memory = getApplication().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
                    edit = memory.edit();
                    edit.putString("to", fromPhone);
                    edit.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Prepare notification builder

                NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
                builder.setDefaults(Notification.DEFAULT_ALL);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                    builder.setLargeIcon(bm);
                    builder.setSmallIcon(R.drawable.ic_message_black_24dp);
                } else {
                    builder.setSmallIcon(R.drawable.icon);
                }
                builder.setContentTitle("Find Your Friend");
                builder.setContentText(from + ": " + m);
                builder.setAutoCancel(true);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                if (m.equals(Helper.REQUEST)) { //got request
                    //set click listener on approve button
                    Intent approve = new Intent(getBaseContext(), MainScreenActivity.class);
                    approve.putExtra("loc", 1);
                    approve.setAction(Helper.MODE_APPROVE);
                    approve.putExtra("to", fromPhone);

                    stackBuilder.addParentStack(MainScreenActivity.class);
                    stackBuilder.addNextIntent(approve);
                    PendingIntent approvePendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

                    builder.addAction(0, Helper.MODE_APPROVE, approvePendingIntent);

                    //set click listener on refuse button
                    Intent refuse = new Intent(getBaseContext(), SendMessageIntentService.class);
                    refuse.setAction(Helper.MODE_REFUSE);
                    refuse.putExtra("operation", "message");
                    refuse.putExtra("to", fromPhone);
                    refuse.putExtra("content", Helper.REFUSE);
                    stackBuilder.addNextIntent(refuse);

                    PendingIntent refusePendingIntent =
                            PendingIntent.getService(getBaseContext(), 2, refuse, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setPriority(Notification.PRIORITY_HIGH);

                    builder.addAction(0, Helper.MODE_REFUSE, refusePendingIntent);
                    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(0, builder.build());

                } else if (m.equals(Helper.APPROVED)) { //got approved
                    memory.edit().putString("bt_status", "client").apply();
                    Log.d(Helper.BT_TAG, "Got Approved, Client Mode");
                    //set click listener on notification
                    Intent approve = new Intent(getBaseContext(), MainScreenActivity.class);
                    approve.putExtra("loc", 2);
                    approve.setAction(Helper.MODE_APPROVE);

                    stackBuilder.addParentStack(MainScreenActivity.class);
                    stackBuilder.addNextIntent(approve);
                    PendingIntent approvePendingIntent =
                            stackBuilder.getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setContentIntent(approvePendingIntent);
                    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(1, builder.build());
                } else if (m.equals(Helper.REFUSE)) {
                    //set click listener on notification
                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle("Search Refused");
                    inboxStyle.addLine("From: " + from);
                    inboxStyle.addLine(m);
                    builder.setStyle(inboxStyle);
                    stackBuilder.addParentStack(MainScreenActivity.class);
                    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(1, builder.build());
                } else if (m.equals(Helper.STOP_SEARCH)) {
                    //set click listener on notification
                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle("Search Stopped");
                    inboxStyle.addLine("From: " + from);
                    inboxStyle.addLine(Helper.SEARCH_STOPPED);
                    builder.setStyle(inboxStyle);
                    Intent approve = new Intent(getBaseContext(), MainScreenActivity.class);
                    approve.putExtra("loc", 3);
                    approve.setAction(Helper.MODE_STOP);
                    stackBuilder.addParentStack(MainScreenActivity.class);
                    stackBuilder.addNextIntent(approve);
                    PendingIntent approvePendingIntent =
                            stackBuilder.getPendingIntent(2, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setContentIntent(approvePendingIntent);
                    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(1, builder.build());
                }
            }
        });
    }

}