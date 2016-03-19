package com.jcefinal.itamarsh.persontoperson;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by itamar on 25-Dec-15.
<<<<<<< HEAD
=======
 * This class responsible for gcm messages receiving, and will call GcmIntentService to treat the message that arrived
>>>>>>> 70bae33d36f3c9597a7581fc2b6aadd5b04f45d3
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}