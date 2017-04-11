package com.torres.companionshipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class EventSelectorDate extends AppCompatActivity {

    // Declare global variables and objects
    String eventName;
    final String EVENT_NAME_KEY = "eventName";
    int day;
    int month;
    int year;
    Button addEventDateButton;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_selector_date);

        // Retrieve the reference of the objects from the profile_page.xml file
        addEventDateButton = (Button)findViewById(R.id.select_events_by_date);
        datePicker = (DatePicker)findViewById(R.id.date_picker_event_selector);

        // Call the supportive methods
        setUpActionBar();
        retrieveEventNameFromIntent();

        // Call the methods with associated Listeners
        addListenerToAddEventDateButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Add Event Date Button ************************
    // *********************************************************************************************
    public void addListenerToAddEventDateButton() {
        addEventDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateFromDatePicker();
                prepareIntent();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Get intent with the Event Name *****************************************
    // *********************************************************************************************
    public void retrieveEventNameFromIntent() {

        // Retrieve the Event Name from EventSelectorName activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventName = extras.getString(EVENT_NAME_KEY);
            //Toast.makeText(getApplicationContext(), eventName, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Retrieve the date from Date Picker *************************************
    // *********************************************************************************************
    public void getDateFromDatePicker() {
        // Retrieve the values for day, month and year from a Date Picker
        day = datePicker.getDayOfMonth();
        // Month starts at 0 so we need to add 1
        month = datePicker.getMonth() + 1;
        year = datePicker.getYear();
    }

    // *********************************************************************************************
    // ******************** Send intent with the Event Name and Date *******************************
    // *********************************************************************************************
    public void prepareIntent() {

        String key = "eventDate";
        String eventDate = day + "-" + month + "-" + year;

        // Prepare the intent and send the event name to next activity
        Intent eventIntent = new Intent (getApplicationContext(), EventSelectorTime.class);
        eventIntent.putExtra(EVENT_NAME_KEY, eventName);
        eventIntent.putExtra(key, eventDate);
        startActivity(eventIntent);
        finish();
        //Toast.makeText(getApplicationContext(), eventName + " " + eventDate, Toast.LENGTH_LONG).show();
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
}
