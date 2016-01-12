package com.jcefinal.itamarsh.persontoperson;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

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
    private SharedPreferences.Editor edit;
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                Log.e("Bundle", "" + extras.keySet());
                String m = extras.getString("message", "empty");
                Log.e("Bundle", "Got message");
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
                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendMessage(String data) {
        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

//    private void saveToMemory(String message) {
//        String temp[];
//        temp = message.split(",");
//        edit = memory.edit();
//        edit.putString("long", temp[0]);
//        edit.putString("lat", temp[1]);
//        edit.putString("message", "false");
//        edit.apply();
//    }
    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context c = getBaseContext();
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
                    edit.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
}