package com.torres.companionshipapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends DialogFragment {

    Button buttonOk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Create the View
        View customDialogView = inflater.inflate(R.layout.custom_dialog, container, false);

        // Retrieve the dialog buttons layout
        buttonOk = (Button)customDialogView.findViewById(R.id.custom_dialog_button_ok);

        String key = "message";
        String value = "value";

        // Retrieve the String message using Shared Preferences (Custom Dialog Box message)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String message = sharedPreferences.getString(key, value);

        // Set text message to the Custom Dialog Box
        TextView dialogTextView = (TextView) customDialogView.findViewById(R.id.dialog_custom);
        dialogTextView.setText(message);

        // Call the methods with assigned Listeners
        addListenerToOkButton();

        return customDialogView;
    }

    public void addListenerToOkButton () {
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });
    }

     /*   @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the Layout Inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
                .setCancelable(false)
                //.setMessage("JUST TESTING DIALOG")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
               *//* .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });*//*
        // Create the AlertDialog object and return it
        return builder.create();
    }*/
}
