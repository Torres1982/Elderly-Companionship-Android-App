package com.torres.companionshipapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MapPlaces extends AppCompatActivity {

    final String EVENT_ON_MAP = "";
    String eventOnMapToFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_google_map);

        // Call the supportive methods
        setUpActionBar();
        retrieveIntent();
    }

    // *********************************************************************************************
    // ******************** Get intent with the Event Name and Date ********************************
    // *********************************************************************************************
    public void retrieveIntent() {

        // Retrieve the Event Name and Date from EventSelectorDate activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventOnMapToFind = extras.getString(EVENT_ON_MAP);
            //Toast.makeText(getApplicationContext(), startDate + " " + endDate, Toast.LENGTH_LONG).show();
        }
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
