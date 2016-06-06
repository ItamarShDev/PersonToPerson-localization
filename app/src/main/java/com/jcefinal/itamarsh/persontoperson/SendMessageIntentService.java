package com.jcefinal.itamarsh.persontoperson;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

public class SendMessageIntentService extends IntentService {
    private SharedPreferences memory;
    private GoogleCloudMessaging gcm;
    private Context context;
    private String token;
    private RequestQueue queue;
    private Helper helper = new Helper();

    public SendMessageIntentService() {
        super("SendMessageIntentService");
    }

    public SendMessageIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager nm = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(0);
        String op = intent.getStringExtra("operation");
        String to = intent.getStringExtra("to");
        String content = intent.getStringExtra("content");

        context = getApplicationContext();
        String authorizedEntity = Helper.SENDER_ID; // Project id from Google Developer Console
        token = "";

        String scope = "GCM";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            token = InstanceID.getInstance(context).getToken(authorizedEntity, scope);

        } catch (IOException ex) {
            Log.d(Helper.CONNECTION_TAG, "IN SendMessageIntentService - Failed to complete token refresh", ex);
            ex.printStackTrace();
        }
        prepareOperation(op, to, content);
    }

    /* POST to GCM server using volley library */
    public void sendToServer(String op, JSONObject jo) {
        final String serverAddr = Helper.SERVER_ADDR + op;
        queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                serverAddr,
                jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("response");

                        } catch (JSONException e) {
                            Log.i(Helper.CONNECTION_TAG, "IN SendMessageIntentService - Error on response " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Got error", Toast.LENGTH_LONG).show();
                        try {

                            Log.e(Helper.CONNECTION_TAG, "IN SendMessageIntentService - " + error.toString());
                            Log.e(Helper.CONNECTION_TAG, "IN SendMessageIntentService - " + error.getLocalizedMessage());

                        } catch (NullPointerException e) {
                            Log.e(Helper.CONNECTION_TAG, "IN SendMessageIntentService - Volley Error");
                        }

                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("REQUEST");
        queue.add(request);
    }


    /* preparing json body for sending message to other device using GCM server  */
    public void sendMessage(String to, String message) {
        String op = "message";
        JSONObject jsonBody = new JSONObject();
        memory = getSharedPreferences("currentLoc", MODE_PRIVATE);
        String myPhone = helper.encode(memory.getString("myphone", ""));
        try {
            jsonBody.put("from", myPhone);
            jsonBody.put("to", to);
            jsonBody.put("message", message);

        } catch (JSONException e) {
            Log.e(Helper.CONNECTION_TAG, "IN SendMessageIntentService - json error" + e.getMessage());
        }
        sendToServer(op, jsonBody);
    }

    private void updateServer(String message) {
        try {
            String op = "get_location";
            JSONObject json = new JSONObject(message);
            sendToServer(op, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*preparing json body for registration to GCM server*/
    private void registerToServer() {
        String op = "register";
        SharedPreferences memory;
        memory = context.getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
        String hashString = helper.encode(memory.getString("myphone", ""));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", token);
            jsonBody.put("id", hashString);
            jsonBody.put("username", memory.getString("myname", "almoni"));

        } catch (JSONException e) {
            Log.i(Helper.CONNECTION_TAG, "IN SendMessageIntentService - json error" + e.getMessage());
        }
        sendToServer(op, jsonBody);
    }

    private void findContact(String to) {
        String op = "contact";
        String hashString = helper.encode(to);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("contact", hashString);
        } catch (JSONException e) {
            Log.e(Helper.CONNECTION_TAG, "IN SendMessageIntentService - json error" + e.getMessage());
        }
        sendToServer(op, jsonBody);
    }

    /* prepareOperation - preparing json body to send to server depending on operation required*/
    public void prepareOperation(String op, String to, String message) {
        switch (op) {
            case "register":
                registerToServer();
                break;
            case "message":
                sendMessage(to, message);
                break;
            case "contact":
                findContact(to);
                break;
            case "wifi":
                updateServer(message);
                break;
            case "end":
                endConnection(message);
        }
    }

    private void endConnection(String message) {
        try {
            String op = "close_session";
            JSONObject json = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

