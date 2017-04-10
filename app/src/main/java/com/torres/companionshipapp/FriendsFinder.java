package com.torres.companionshipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FriendsFinder extends AppCompatActivity {

    // Declare global variables and objects
    Button findFriendsByHobbyButton;
    Button findFriendsByAgeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_finder);

        // Retrieve the reference of the objects from the profile_page.xml file
        findFriendsByHobbyButton = (Button)findViewById(R.id.find_friends_hobby_button);
        findFriendsByAgeButton = (Button)findViewById(R.id.find_friends_age_button);

        // Call the supportive methods
        setUpActionBar();

        // Call the methods with assigned Listeners
        addListenerToFindFriendsByHobbyButton();
        addListenerToFindFriendsByAgeButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends by Hobby Button *****************
    // ******************** Redirect to the Find Friends by Hobby Activity *************************
    // *********************************************************************************************
    public void addListenerToFindFriendsByHobbyButton() {
        findFriendsByHobbyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), FriendsFinderHobby.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends by Age Button *******************
    // ******************** Redirect to the Find Friends by Age Activity ***************************
    // *********************************************************************************************
    public void addListenerToFindFriendsByAgeButton() {
        findFriendsByAgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), FriendsFinderAge.class);
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
