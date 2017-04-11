package com.torres.companionshipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EventSelectorName extends AppCompatActivity {

    // Declare global variables and objects
    String spinnerValue;
    Spinner spinner;
    Button addEventNameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_selector_name);

        // Get reference of the objects from the event_selector_name.xml file
        addEventNameButton = (Button)findViewById(R.id.button_add_event_name);
        spinner = (Spinner) findViewById(R.id.add_event_name_spinner);

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with associated Listeners
        addListenerToAddEventNameButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Add Event Name Button ************************
    // *********************************************************************************************
    public void addListenerToAddEventNameButton() {
        addEventNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

                // Get the value from the selected dropdown list
                spinnerValue = String.valueOf(spinner.getSelectedItem());

                prepareIntent();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Send intent with the Event Name to next activity ***********************
    // *********************************************************************************************
    public void prepareIntent() {

        String key = "spinnerValue";

        // Prepare the intent and send the event name to next activity
        Intent eventIntent = new Intent (getApplicationContext(), EventSelectorDate.class);
        eventIntent.putExtra(key, spinnerValue);
        startActivity(eventIntent);
        finish();
    }

    // *********************************************************************************************
    // ******************** Set up custom Action Bar title *****************************************
    // ******************** Add a logo to the Action Bar *******************************************
    // *********************************************************************************************
    public void setUpActionBar() {

        ActionBar myCustomActionBar = getSupportActionBar();

        // Disable default Action Bar settings
        myCustomActionBar.setDisplayShowHomeEnabled(false);
        myCustomActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater myLayoutInflater = LayoutInflater.from(this);
        View myCustomView = myLayoutInflater.inflate(R.layout.custom_action_bar, null);

        // Get the reference from the action_bar_title.xml file
        TextView myTitleTextView = (TextView) myCustomView.findViewById(R.id.action_bar_title);

        // Set up custom Action Bar
        String actionBarTitle = "Elderly Companionship";
        myTitleTextView.setText(actionBarTitle);
        myCustomActionBar.setCustomView(myCustomView);
        myCustomActionBar.setDisplayShowCustomEnabled(true);
    }

    // *********************************************************************************************
    // ******************** Set up custom Spinner **************************************************
    // ******************** Edit Spinner text size and colors **************************************
    // *********************************************************************************************
    public void setUpSpinner() {

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.event_array, R.layout.spinner_edit);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}
