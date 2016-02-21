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
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
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

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                Log.e("Bundle", "" + extras.keySet());
                String m = extras.getString("message", "empty");
                Log.e("Bundle", "Got message");
                Log.e("Bundle", "Message: " + m);
                if(m.compareTo("registered") == 0)
                {
                   Log.i("mydebug", "Registration to GCM server completed successfully");
                }
                else {
                    try {
                        JSONObject js = new JSONObject(m);
                        String str = js.getString("message");
                        Log.i("bundle", "str " + str);
                        if (str.contains(",")) {
                            Log.i("bundle", "in if");
                            sendMessage(str);
                        } else {
                            Log.i("bundle", "in else");
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

    private void sendMessage(String data) {
        Intent intent = new Intent("my-event");
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    protected void showNotification(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context c = getBaseContext();
                String from = "", m = "", fromPhone = "";
                Log.i("MyDebug", "in show notification" + message);
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
                NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
                builder.setDefaults(Notification.DEFAULT_ALL);
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                    builder.setLargeIcon(bm);
                    builder.setSmallIcon(R.drawable.ic_message_black_24dp);
                }
                else {
                    builder.setSmallIcon(R.drawable.icon);
                }
                builder.setContentTitle("Find Your Friend");
                builder.setContentText(from + ": " + m);
                builder.setAutoCancel(true);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                if(m.equals(Helper.REQUEST))
                 {
                    //set click listener on approve button
                     Intent approve = new Intent(getBaseContext(), MainScreenActivity.class);
                     approve.putExtra("loc", 1);
                     approve.setAction("approve");
                     approve.putExtra("to", fromPhone);

                     stackBuilder.addParentStack(MainScreenActivity.class);
                     stackBuilder.addNextIntent(approve);
                     PendingIntent approvePendingIntent =
                             stackBuilder.getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT);

                     builder.addAction(0, "approve", approvePendingIntent);

                    //set click listener on refuse button
                    Intent refuse = new Intent(getBaseContext(), SendMessageIntentService.class);
                    refuse.setAction("refuse");
                     Log.i("mydebug", "put extra " + fromPhone);
                     refuse.putExtra("operation", "message");
                    refuse.putExtra("to", fromPhone);
                    refuse.putExtra("content", Helper.REFUSE);
                    stackBuilder.addNextIntent(refuse);

                    PendingIntent refusePendingIntent =
                            PendingIntent.getService(getBaseContext(), 2, refuse, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setPriority(Notification.PRIORITY_HIGH);

                    builder.addAction(0, "refuse", refusePendingIntent);
                     NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                     nm.notify(0, builder.build());

                }
                else if(m.equals(Helper.APPROVED))
                {
                    Log.i("APPROVED","Got Approved from "+from);
                    //set click listener on notification
                    Intent approve = new Intent(getBaseContext(), MainScreenActivity.class);
                    approve.putExtra("loc", 2);
                    approve.setAction("approve");

                    stackBuilder.addParentStack(MainScreenActivity.class);
                    stackBuilder.addNextIntent(approve);
                    PendingIntent approvePendingIntent =
                            stackBuilder.getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setContentIntent(approvePendingIntent);
                    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(1, builder.build());
                }
                else if(m.equals(Helper.REFUSE))
                {
                    Log.i("REFUSED", "Got refused from " + from);
                    //set click listener on notification
                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle("Search Refused");
                    inboxStyle.addLine("From: "+from);
                    inboxStyle.addLine(m);
                    builder.setStyle(inboxStyle);
                    stackBuilder.addParentStack(MainScreenActivity.class);
                    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(1, builder.build());
                }
                else if(m.equals(Helper.STOP_SEARCH))
                {
                    Log.i("SEARCH_STOPPED", "Search from " + from + " stopped");
                    //set click listener on notification
                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle("Search Stopped");
                    inboxStyle.addLine("From: " + from);
                    inboxStyle.addLine("The Search Has Been Stopped by the User");
                    builder.setStyle(inboxStyle);
                    Intent approve = new Intent(getBaseContext(), MainScreenActivity.class);
                    approve.putExtra("loc", 3);
                    approve.setAction("stop_search");
                    stackBuilder.addParentStack(MainScreenActivity.class);
                    stackBuilder.addNextIntent(approve);
                    PendingIntent approvePendingIntent =
                            stackBuilder.getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setContentIntent(approvePendingIntent);
                    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(1, builder.build());
                }
            }
        });
    }

}