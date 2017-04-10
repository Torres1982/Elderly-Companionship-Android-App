package com.torres.companionshipapp;

import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventsFinderDate extends AppCompatActivity {

    TextView displayDateTextView;
    Button findEventsByDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_finder_date);

        // Call Date Picker Dialog Fragment
        DateDialog.DatePickerFragment fragment = new DateDialog.DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "date");

        // Retrieve the reference of the objects from the profile_page.xml file
        displayDateTextView = (TextView) findViewById(R.id.today_date);
        findEventsByDateButton = (Button)findViewById(R.id.find_events_by_date);

        // Call the methods with associated Listeners
        addListenerToFindEventsByDateButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends Button **************************
    // ******************** Redirect ... ***********************************************************
    // *********************************************************************************************
    public void addListenerToFindEventsByDateButton () {
        findEventsByDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

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
