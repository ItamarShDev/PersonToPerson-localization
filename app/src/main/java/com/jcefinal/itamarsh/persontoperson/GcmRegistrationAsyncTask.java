package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.itamar.myapplication.backend.registration.Registration;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by itamar on 25-Dec-15.
 */

class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
    private static final String SENDER_ID = "863392495295";

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d("ASYNC_REG","Running");
        if (regService == null) {
            Log.d("ASYNC_REG","regService == null");
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://certain-grammar-116914.appspot.com/_ah/api/");
            regService = builder.build();
            Log.d("ASYNC_REG","after build()");

        }

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            regService.register(regId).execute();

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        Bundle data = new Bundle();
        data.putString("my_message", "G2 say hello!");
        data.putString("my_action", "SAY_HELLO");
        try {
            gcm.send(SENDER_ID + "@gcm.googleapis.com","2212",data);
            Log.d("GCM", "sent message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}