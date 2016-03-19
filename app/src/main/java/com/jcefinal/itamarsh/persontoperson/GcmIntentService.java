package com.jcefinal.itamarsh.persontoperson;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
<<<<<<< HEAD
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
=======
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
<<<<<<< HEAD
import android.widget.Toast;
=======
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

<<<<<<< HEAD
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GcmIntentService extends IntentService {
    private SharedPreferences memory;
    Context c;
=======
/*
 * This class responsible for treat of arriving GCM messages, using seperate thread
 */
public class GcmIntentService extends IntentService {
    private SharedPreferences memory;
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    private SharedPreferences.Editor edit;
    public GcmIntentService() {
        super("GcmIntentService");
    }
<<<<<<< HEAD

=======
    //***************************************************************************
    //                  Intent Service getting GCM message                      *
    // **************************************************************************
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
<<<<<<< HEAD
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
=======
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                Log.e("Bundle", "" + extras.keySet());
                String m = extras.getString("message", "empty");
                Log.e("Bundle", "Got message");
<<<<<<< HEAD
                Log.e("Bundle", m);
//                memory = getApplication().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
                try {
                    JSONObject js = new JSONObject(extras.getString("message"));
                    String str = js.getString("message");
                    if (str.contains(",")) {
//                        saveToMemory(extras.getString("message"));
                        sendMessage(str);
                    } else {
                        showToast(extras.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
=======
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
                            sendMessage(str);
                        } else {
                            showNotification(extras.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.i("bundle", "json exception");
                        e.printStackTrace();
                    }
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
<<<<<<< HEAD

    private void sendMessage(String data) {
        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    protected void showToast(final String message) {
=======
    //Function send broadcast to main activity, to treat location message
    private void sendMessage(String data) {
        Intent intent = new Intent("my-event");
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //Function show's notification and act depending on it's kind
    protected void showNotification(final String message) {
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context c = getBaseContext();
<<<<<<< HEAD
                String from = "", m = "";
                try {
                    JSONObject responseJSON;
                    DAL dal = new DAL(getBaseContext());
                    responseJSON = new JSONObject(message);
                    from = dal.getName(responseJSON.getString("from"));
                    m = responseJSON.getString("message");
                    memory = getApplication().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
                    edit = memory.edit();
                    edit.putString("to", responseJSON.getString("from"));
=======
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
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
                    edit.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
<<<<<<< HEAD

                NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
                builder.setDefaults(Notification.DEFAULT_ALL);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("Person2Person");
                builder.setContentText(from + ": " + m);
                builder.setAutoCancel(true);
                //set click listener
                Intent nmIntent = new Intent(getBaseContext(), MainScreenActivity.class);
                nmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                nmIntent.putExtra("loc", 4);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                stackBuilder.addParentStack(MainScreenActivity.class);
                stackBuilder.addNextIntent(nmIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
                NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);

                nm.notify(0, builder.build());
            }
        });
    }
=======
                //Prepare notification builder

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

>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
}