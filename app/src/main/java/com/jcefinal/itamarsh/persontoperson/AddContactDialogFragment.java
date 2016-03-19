package com.jcefinal.itamarsh.persontoperson;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
    private SharedPreferences.Editor edit;
    private SharedPreferences memory;
    private final String MobilePattern = "[0-9]{10}";

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
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_add_contact, null);
        mName = (EditText)v.findViewById(R.id.username);
        mPhone = (EditText)v.findViewById(R.id.phone);
        dal = new DAL(this.getActivity());

        builder.setView(v)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String phoneNum =  mPhone.getText().toString();
                        String name = mName.getText().toString();
                        memory = getActivity().getBaseContext().getSharedPreferences("currentLoc", Context.MODE_PRIVATE);
                        edit = memory.edit();
                        edit.putBoolean("add",false);
                        edit.apply();

                        if (phoneNum.isEmpty() || name.isEmpty())
                            Toast.makeText(getActivity(), "Do not leave empty fields!", Toast.LENGTH_LONG).show();
                        else if (!phoneNum.matches(MobilePattern))
                        {
                            Toast.makeText(getActivity(), "Phone number is not valid!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            String phone = mPhone.getText().toString();
                            edit = memory.edit();
                            edit.putBoolean("add", true);
                            edit.putString("to", phone);
                            edit.putString("name",name);
                            edit.apply();
                        }
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
}