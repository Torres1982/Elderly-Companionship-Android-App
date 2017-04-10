package com.torres.companionshipapp;

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

public class EventsFinderName extends AppCompatActivity {

    // Declare global variables and objects
    String spinnerValue;
    Spinner spinner;
    Button findEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_finder_name);

        // Get reference of the objects from the events_finder_name.xmle.xml file
        findEventsButton = (Button)findViewById(R.id.button_find_events);
        spinner = (Spinner) findViewById(R.id.event_spinner);

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with associated Listeners
        addListenerToFindEventsButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends Button **************************
    // ******************** Redirect ... ***********************************************************
    // *********************************************************************************************
    public void addListenerToFindEventsButton () {
        findEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

                // Get the value from the selected dropdown list
                spinnerValue = String.valueOf(spinner.getSelectedItem());

                String chosenValue = "Value chosen: ";
                Toast.makeText(EventsFinderName.this, chosenValue + spinnerValue, Toast.LENGTH_LONG).show();
            }
        });
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
