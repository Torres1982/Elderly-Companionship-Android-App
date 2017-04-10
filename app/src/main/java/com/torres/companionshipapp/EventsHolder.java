package com.torres.companionshipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventsHolder extends AppCompatActivity {

    // Declare global variables and objects
    Button createEventsButton;
    Button showEventsByNameButton;
    Button showEventsByDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_holder);

        // Retrieve the reference of the objects from the profile_page.xml file
        createEventsButton = (Button)findViewById(R.id.create_event);
        showEventsByNameButton = (Button)findViewById(R.id.find_event_by_name);
        showEventsByDateButton = (Button)findViewById(R.id.find_event_by_date);

        // Call the supportive methods
        setUpActionBar();

        // Call the methods with assigned Listeners
        addListenerToCreateEventsButton();
        addListenerToShowEventsByNameButton();
        addListenerToShowEventsByDateButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Create Events Button *************************
    // ******************** Redirect to the Create Events Activity *********************************
    // *********************************************************************************************
    public void addListenerToCreateEventsButton() {
        createEventsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Show Events by Name Button *******************
    // ******************** Redirect to the Show Events by Name Activity ***************************
    // *********************************************************************************************
    public void addListenerToShowEventsByNameButton() {
        showEventsByNameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), EventsFinderName.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Show Events by Date Button *******************
    // ******************** Redirect to the Show Events by Date Activity ***************************
    // *********************************************************************************************
    public void addListenerToShowEventsByDateButton() {
        showEventsByDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

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
}
