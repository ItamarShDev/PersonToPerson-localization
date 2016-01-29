package com.jcefinal.itamarsh.persontoperson;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    Context c;
    private SharedPreferences.Editor edit;
    private Helper helper = new Helper();
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
                Log.e("Bundle", m);
                try {
                    JSONObject js = new JSONObject(extras.getString("message"));
                    String str = js.getString("message");
                    if (str.contains(",")) {
                        sendMessage(str);
                    } else {
                        showNotification(extras.getString("message"));
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
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    protected void showNotification(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context c = getBaseContext();
                String from = "", m = "", fromPhone = "";
                Log.i("Here ", message );
                try {
                    JSONObject responseJSON;
                    DAL dal = new DAL(getBaseContext());

                    responseJSON = new JSONObject(message);
                    fromPhone = responseJSON.getString("from");
                    from = dal.getName(fromPhone);
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
                builder.setSmallIcon(R.drawable.icon);
                builder.setContentTitle("Find Your Friend");
                builder.setContentText(from + ": " + m);
                builder.setAutoCancel(true);
                if(m.startsWith("I"))
                 {
                    //set click listener on approve button
                    Intent approve = new Intent(getBaseContext(), MainScreenActivity.class);
                    approve.putExtra("loc", 1);
                    approve.setAction("approve");
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
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
                    refuse.putExtra("content", helper.REFUSE);
                    stackBuilder.addNextIntent(refuse);

                    PendingIntent refusePendingIntent =
                            PendingIntent.getService(getBaseContext(), 2, refuse, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setPriority(Notification.PRIORITY_HIGH);

                    builder.addAction(0, "refuse", refusePendingIntent);

                }
                NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

                nm.notify(0, builder.build());
            }
        });
    }

}