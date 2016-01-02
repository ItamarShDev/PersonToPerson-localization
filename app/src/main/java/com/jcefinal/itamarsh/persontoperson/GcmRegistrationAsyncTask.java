
package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    //private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;
    private static final String TAG ="myDebug";
    private static final String SENDER_ID = "186698592995";
    private static final String PROJECT_ID = "p2p-gcm-server";
    private String token;
    private RequestQueue queue;

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d(TAG,"Running");
        String authorizedEntity = SENDER_ID; // Project id from Google Developer Console
        token = "";

        String scope = "GCM";
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            token = InstanceID.getInstance(context).getToken(authorizedEntity, scope);
            Log.i(TAG, "GCM Registration Token: " + token);

            registerToServer();
            msg = "Device registered, registration ID=" + token;

        } catch (IOException ex) {
            Log.d(TAG, "Failed to complete token refresh", ex);
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Log.i(TAG, "in post execute");
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        Bundle data = new Bundle();
        data.putString("my_message", "G2 say hello!");
        data.putString("my_action", "SAY_HELLO");
        try {
            gcm.send(SENDER_ID + "@gcm.googleapis.com", "2212", data);
            Log.d(TAG, "sent message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerToServer()
    {
        String server_addr = "http://p2p-gcm-server.appspot.com/register";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", token);
            jsonBody.put("id", "054");
            jsonBody.put("username", "olesya");

        }
        catch (JSONException e)
        {
            Log.i(TAG, "json error" + e.getMessage());

        }
        Log.i(TAG, "after building json");
        queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                server_addr,
                jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("response");
                            Log.i(TAG, "response is: " + res);
                        }
                        catch (JSONException e)
                        {
                            Log.i(TAG,"Error on response " +  e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Got error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, error.getMessage());
                    }
                }

        );
        request.setTag("REQUEST");
        queue.add(request);
    }
}
