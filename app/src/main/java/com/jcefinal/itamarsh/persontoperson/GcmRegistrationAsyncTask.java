
package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


class GcmRegistrationAsyncTask extends AsyncTask<String, String, String> {
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
    protected String doInBackground(String... params) {
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
            sendToServer(params[0], params[1], params[2]);

        } catch (IOException ex) {
            Log.d(TAG, "Failed to complete token refresh", ex);
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
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
    private void sendToServer(String op, JSONObject jo){
        String serverAddr = "http://p2p-gcm-server.appspot.com/"+op;
        queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                serverAddr,
                jo,
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
    public void sendMessage(String to,String message){
        String op = "message";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("from", token);
            jsonBody.put("to", to);
            jsonBody.put("message", message);

        }
        catch (JSONException e)
        {
            Log.i(TAG, "json error" + e.getMessage());
        }
        Log.i(TAG, "after building json");
        sendToServer(op, jsonBody);
    }
    private void registerToServer()
    {
        String op = "register";
        String hashString = "";
        SharedPreferences memory;
        memory = context.getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        try {
            String phone =  memory.getString("myphone", "");
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(phone.getBytes());
            byte[] digest = md.digest();
            hashString =  Base64.encodeToString(digest, Base64.DEFAULT);
            Log.i(TAG,"hash " + hashString);
        }
        catch (NoSuchAlgorithmException e)
        {}
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", token);
            jsonBody.put("id", hashString);
            jsonBody.put("username", memory.getString("myname", "almoni"));

        }
        catch (JSONException e)
        {
            Log.i(TAG, "json error" + e.getMessage());
        }
        Log.i(TAG, "after building json " + jsonBody);
        Log.i(TAG, "GCM Registration Token: " + token);
        sendToServer(op, jsonBody);
    }
    public void sendToServer(String op, String to, String message){
        switch (op){
            case "register":
                registerToServer();
                break;
            case "message":
                sendMessage(to, message);
                break;
        }
    }
}
