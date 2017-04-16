package com.torres.companionshipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Name: DetailsHolder <br>
 * This class is a holder to redirect user to other activities:
 * (show friends list, show events list, and show the Google map).
 * @author B00073668 Artur Sukiennik
 * @version 5, date: 5.04.2017
 */
public class DetailsHolder extends AppCompatActivity {

    // Declare global variables and objects
    Button showFriendsButton;
    Button showEventsButton;
    Button showOnMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_holder);

        // Retrieve the reference of the objects from the profile_page.xml file
        showFriendsButton = (Button)findViewById(R.id.show_friends_button);
        showEventsButton = (Button)findViewById(R.id.show_events_button);
        showOnMapButton = (Button)findViewById(R.id.places_map_button);

        // Call the supportive methods
        setUpActionBar();

        // Call the methods with assigned Listeners
        addListenerToShowFriendsButton();
        addListenerToShowEventsButton();
        addListenerToShowOnMapButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Show Friends Button **************************
    // ******************** Redirect to the Show Friends Activity **********************************
    // *********************************************************************************************
    public void addListenerToShowFriendsButton() {
        showFriendsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), FriendsList.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Show Events Button ***************************
    // ******************** Redirect to the Show Events Activity ***********************************
    // *********************************************************************************************
    public void addListenerToShowEventsButton() {
        showEventsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), EventsList.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Show on Map Button ***************************
    // ******************** Redirect to the Map Activity *******************************************
    // *********************************************************************************************
    public void addListenerToShowOnMapButton() {
        showOnMapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), Map.class);
                startActivity(intent);
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
