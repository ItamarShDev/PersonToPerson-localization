package com.jcefinal.itamarsh.persontoperson;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by olesya on 23-Jan-16.
 */
public class FirstPageDialog extends DialogFragment {

    private DialogInterface.OnDismissListener onDismissListener;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.first_page_dialog, null);
        final EditText mPhone = (EditText)v.findViewById(R.id.editTextPhone);
        final EditText mName = (EditText)v.findViewById(R.id.editTextUserName);
        builder.setView(v)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences memory = getActivity().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = memory.edit();
                        edit.putString("myphone", mPhone.getText().toString());
                        edit.putString("myname", mName.getText().toString());
                        edit.apply();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
