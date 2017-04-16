package com.torres.companionshipapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Name: MainActivity <br>
 * This class is a starting point for any user.
 * Users can go to Log In or Register activities from this activity.
 * @author B00073668 Artur Sukiennik
 * @version 2, date: 5.01.2017
 */
public class MainActivity extends AppCompatActivity {

    // Declare global variables and objects
    Button goToLoginButton;
    Button goToRegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Call the supportive methods
        setUpActionBar();

        // Get references of the objects from the activity_main.xml file
        goToLoginButton = (Button)findViewById(R.id.buttonMainActivityLogin);
        goToRegistrationButton = (Button)findViewById(R.id.buttonMainActivityRegister);

        // Call the methods with assigned Listeners
        addListenerToLoginButton();
        addListenerToRegistrationButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Login Button *********************************
    // ******************** Redirect to the Login Page *********************************************
    // *********************************************************************************************
    public void addListenerToLoginButton () {
        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent (getApplicationContext(), Login.class);
                startActivity(loginIntent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Registration Button **************************
    // ******************** Redirect to the Registration Page **************************************
    // *********************************************************************************************
    public void addListenerToRegistrationButton () {
        goToRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrationIntent = new Intent (getApplicationContext(), Registration.class);
                startActivity(registrationIntent);
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
