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

/**
 * Name: CustomDialog <br>
 * This class creates a Custom Dialog to display messages to the user.
 * It implements Shared Preferences to retrieve message Strings from different activities.
 * @author B00073668 Artur Sukiennik
 * @version 1.1, date: 13.02.2017
 */
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

    // *********************************************************************************************
    // ******************** Register Listener for OK Button ****************************************
    // *********************************************************************************************
    public void addListenerToOkButton () {
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
