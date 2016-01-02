package com.jcefinal.itamarsh.persontoperson;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by olesya on 02-Jan-16.
 */
public class GcmSendMessage {
    private Context context;

    public GcmSendMessage(Context context)
    {
        this.context = context;
    }
    public void sendMessage(String phone_num) {
        String server_addr = "http://p2p-gcm-server.appspot.com/message";
        //String token = "";
        final String TAG = "myDebug";
        Log.i(TAG, "in send massage");
        RequestQueue queue = Volley.newRequestQueue(context);;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("to", phone_num);
            jsonBody.put("from", "054");
            jsonBody.put("message", "hi you");
        } catch (JSONException e) {
            Log.i(TAG, "json error" + e.getMessage());

        }
        Log.i(TAG, "in send meesage, after json build");
        //queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                server_addr,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("response");
                            Log.i(TAG, "response is: " + res);
                        } catch (JSONException e) {
                            Log.i(TAG, "Error on response " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Got error", Toast.LENGTH_LONG).show();
                        try {
                            Log.i(TAG, error.getMessage());
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG,"in error listener");
                        }
                    }
                }

        );
        request.setTag("REQUEST");
        queue.add(request);
    }
}
