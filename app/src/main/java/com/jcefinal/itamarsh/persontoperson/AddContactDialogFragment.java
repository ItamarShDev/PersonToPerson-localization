package com.jcefinal.itamarsh.persontoperson;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by itamar on 28-Nov-15.
 * This class responsible for Add Contacts dialog
 * The dialog will responsible for adding new contact to contact list and saving it in DB
 */
public class AddContactDialogFragment extends DialogFragment {

    private DAL dal;
    private DialogInterface.OnDismissListener onDismissListener;
    private Helper helper = new Helper();

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
        View v = inflater.inflate(R.layout.dialog_add_contact, null);
        final EditText mName = (EditText)v.findViewById(R.id.username);
        final EditText mPhone = (EditText)v.findViewById(R.id.phone);
        dal = new DAL(this.getActivity());

        builder.setView(v)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dal.addEntries(mName.getText().toString(), mPhone.getText().toString());

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