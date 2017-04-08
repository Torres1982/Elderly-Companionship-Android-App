package com.torres.companionshipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailUpdater extends AppCompatActivity {

    // Declare global variables and objects
    Button addHobbyButton;
    Button addAgeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_details);

        // Call the supportive methods
        setUpActionBar();

        // Retrieve the reference of the objects from the profile_page.xml file
        addHobbyButton = (Button)findViewById(R.id.update_details_button_add_hobby);
        addAgeButton = (Button)findViewById(R.id.update_details_button_add_age);

        // Call the methods with assigned Listeners
        addListenerToAddHobbyButton();
        addListenerToAddAgeButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Add Hobby Button *****************************
    // ******************** Redirect to the Hobby Add Activity *************************************
    // *********************************************************************************************
    public void addListenerToAddHobbyButton () {
        addHobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), HobbyAdd.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Add Age Button *******************************
    // ******************** Redirect to the Age Add Activity ***************************************
    // *********************************************************************************************
    public void addListenerToAddAgeButton () {
        addHobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), AgeAdd.class);
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
