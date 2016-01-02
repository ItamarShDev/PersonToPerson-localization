package com.jcefinal.itamarsh.persontoperson;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by olesya on 29-Dec-15.
 */
public class GcmRegistrationIntent extends IntentService{
    private GoogleCloudMessaging gcm;
    private Context context;
    private static final String PROJECT_ID = "p2p-gcm-server";
    private static final String TAG = "myDebug";
    private RequestQueue queue;
    private static final String SENDER_ID = "186698592995";


    public GcmRegistrationIntent() {
        super("GcmRegistrationIntent");
    }

    public GcmRegistrationIntent( Context context)
    {
        super("GcmRegistrationIntent");
        this.context = context;
    }

    public void register() {
        String authorizedEntity = PROJECT_ID; // Project id from Google Developer Console
        String token = "";
        String scope = "GCM";
        String server_addr = "p2p-gcm-server.appspot.com/register";
        try {
            token = InstanceID.getInstance(context).getToken(authorizedEntity, scope);
            Log.i(TAG, "GCM Registration Token: " + token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
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
                            Log.e(TAG, "got good response");
                            JSONArray array = response.getJSONArray("value");

                        }
                        catch (JSONException e)
                        {
                            Log.i(TAG, e.getMessage());
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

    @Override
    protected void onHandleIntent(Intent intent) {
        register();
    }
}
