package com.jcefinal.itamarsh.persontoperson;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
 * Created by itamar on 28-Nov-15.
 * This class responsible for Add Contacts dialog
 * The dialog will responsible for adding new contact to contact list and saving it in DB
 */
public class AddContactDialogFragment extends DialogFragment {

    private DAL dal;
    private DialogInterface.OnDismissListener onDismissListener;
    private Helper helper = new Helper();
    private final static String TAG = "mydebug";
    private Context context;
    private EditText mName, mPhone;
    private ProgressDialog dialog;
    private  AlertDialog.Builder builder;
    private View v;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_add_contact, null);
        mName = (EditText)v.findViewById(R.id.username);
        mPhone = (EditText)v.findViewById(R.id.phone);
        dal = new DAL(this.getActivity());

        builder.setView(v)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        findContact(mPhone.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { // User cancelled the dialog
                        AddContactDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void findContact(String to)
    {
        String op = "contact";
        String hashString = helper.encode(to);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("contact", hashString);
        }
        catch (JSONException e)
        {
            Log.i(TAG, "json error" + e.getMessage());
        }
        Log.i(TAG, "in find contact after building json " + jsonBody);
        sendToServer(op, jsonBody);
    }

    private void sendToServer(String op, JSONObject jo){
        String serverAddr = "http://p2p-gcm-server.appspot.com/"+op;
        context = getActivity().getBaseContext();
        RequestQueue queue = Volley.newRequestQueue(context);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                serverAddr,
                jo,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.cancel();
                            String res = response.getString("response");
                            Log.i(TAG, "response is: " + res);
                            if(res.equals(Helper.EXIST))
                                dal.addEntries(mName.getText().toString(), mPhone.getText().toString());
                            else
                            {
                                Toast.makeText(context, "contact won't be added", Toast.LENGTH_LONG).show();

                            }
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
                        dialog.cancel();
                        Toast.makeText(context, "Got error", Toast.LENGTH_LONG).show();
                        try{
                            Log.i(TAG, error.getMessage());
                        }
                        catch (NullPointerException e)
                        {
                            Log.i(TAG, "Volley Error");
                        }

                    }
                }

        );
        request.setTag("REQUEST");
        queue.add(request);
    }

}