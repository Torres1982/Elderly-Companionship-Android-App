package com.torres.companionshipapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Name: EventSelectorTime <br>
 * This class allows to choose the time of event of interest.
 * Customized Time Picker is used to choose the hour and minutes of event.
 * @author B00073668 Artur Sukiennik
 * @version 4, date: 15.04.2017
 */
public class EventSelectorTime extends AppCompatActivity {

    // Declare global variables and objects
    String eventName;
    String eventDate;
    final String EVENT_NAME_KEY = "eventName";
    final String EVENT_DATE_KEY = "eventDate";
    int hour;
    int minute;
    Button addEventTimeButton;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_selector_time);

        // Retrieve the reference of the objects
        addEventTimeButton = (Button)findViewById(R.id.select_events_by_time);
        timePicker = (TimePicker)findViewById(R.id.time_picker_event_selector);
        // Set Time to 24h format
        timePicker.setIs24HourView(true);

        // Call the supportive methods
        setUpActionBar();
        retrieveEventNameAndDateFromIntent();

        // Call the methods with associated Listeners
        addListenerToAddEventTimeButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Add Event Date Button ************************
    // *********************************************************************************************
    public void addListenerToAddEventTimeButton() {
        addEventTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTimeFromTimePicker();
                prepareIntent();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Get intent with the Event Name and Date ********************************
    // *********************************************************************************************
    public void retrieveEventNameAndDateFromIntent() {

        // Retrieve the Event Name and Date from EventSelectorDate activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventName = extras.getString(EVENT_NAME_KEY);
            eventDate = extras.getString(EVENT_DATE_KEY);
            //Toast.makeText(getApplicationContext(), eventName + " " + eventDate, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Retrieve the time from Time Picker *************************************
    // *********************************************************************************************
    @TargetApi(23)
    public void getTimeFromTimePicker() {

        // Retrieve the values for day, month and year from a Date Picker
        hour = timePicker.getHour();
        minute = timePicker.getMinute();
        //Toast.makeText(getApplicationContext(), hour + " " + minute, Toast.LENGTH_LONG).show();
    }

    // *********************************************************************************************
    // ******************** Send intent with the Event Name, Date and Time *************************
    // *********************************************************************************************
    public void prepareIntent() {

        String key = "eventTime";
        String eventTime = hour + ":" + minute;

        // Prepare the intent and send the event name and date to next activity
        Intent eventIntent = new Intent (getApplicationContext(), EventSelectorVenue.class);
        eventIntent.putExtra(EVENT_NAME_KEY, eventName);
        eventIntent.putExtra(EVENT_DATE_KEY, eventDate);
        eventIntent.putExtra(key, eventTime);
        startActivity(eventIntent);
        finish();
        //Toast.makeText(getApplicationContext(), eventName + " " + eventDate + " " + eventTime, Toast.LENGTH_LONG).show();
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
