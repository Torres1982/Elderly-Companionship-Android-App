package com.torres.companionshipapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePage extends AppCompatActivity {

    // Declare global variables and objects
    String username;
    String welcome;
    TextView usernameTextView;
    Button findFriendsButton;
    Button findEventsButton;
    Button logOutButton;
    Button addHobbyButton;
    Button deleteHobbyButton;
    FirebaseAuth logOutAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        // Get the Firebase authentication instance
        logOutAuthentication = FirebaseAuth.getInstance();

        // Call the supportive methods
        setUpActionBar();

        // Retrieve the username through the intent (from Registration activity)
        //Intent profilePageIntent = getIntent();
        //username = profilePageIntent.getExtras().getString("username");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            welcome = "Welcome " + username;
        }
        else {
            welcome = "Welcome ";
        }
        // Retrieve the name from the Text View
        // Assign the name of the user to the Text View
        usernameTextView = (TextView)findViewById(R.id.shown_name_text_view);
        //welcome = "Welcome " + username;
        usernameTextView.setText(welcome);

        // Retrieve the reference of the objects from the profile_page.xml file
        findFriendsButton = (Button)findViewById(R.id.profile_page_button_find_friends);
        findEventsButton = (Button)findViewById(R.id.profile_page_button_find_events);
        logOutButton = (Button)findViewById(R.id.button_logout);
        addHobbyButton = (Button)findViewById(R.id.button_add_hobby);
        deleteHobbyButton = (Button)findViewById(R.id.button_delete_hobby);

        // Call the methods with assigned Listeners
        addListenerToFindFriendsButton();
        addListenerToFindEventsButton();
        addListenerToLogOutButton();
        addListenerToAddHobbyButton();
        addListenerToDeleteHobbyButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends Button **************************
    // ******************** Redirect to the Find Friends Page **************************************
    // *********************************************************************************************
    public void addListenerToFindFriendsButton () {
        findFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), FriendsFinder.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Events Button ***************************
    // ******************** Redirect to the Find Events Page ***************************************
    // *********************************************************************************************
    public void addListenerToFindEventsButton () {
        findEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), EventsFinder.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Log Out Button *******************************
    // ******************** Redirect to the Home Page **********************************************
    // *********************************************************************************************
    public void addListenerToLogOutButton () {
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLogOutDialog();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Add Hobby Button *****************************
    // ******************** Redirect to the HobbyAdd page ******************************************
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
    // ******************** Register Listener for the Delete Hobby Button **************************
    // ******************** Redirect to the HobbyDelete page ***************************************
    // *********************************************************************************************
    public void addListenerToDeleteHobbyButton () {
        deleteHobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), Map.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** This does the same what Log Out Button does (above) ********************
    // ******************** Redirect to the Home Page **********************************************
    // *********************************************************************************************
    @Override
    public void onBackPressed() {
        createLogOutDialog();
    }

    // *********************************************************************************************
    // ******************** Set up custom Log Out Dialog *******************************************
    // *********************************************************************************************
    public void createLogOutDialog() {

        // Create local variables
        //String alertMessage = "Do you want to exit ?";

        // Create a new instance of Dialog builder
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfilePage.this, R.style.CustomDialog);

        // Get the Layout Inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Build the Dialog Box using a builder
        dialogBuilder.setCancelable(false)
                //.setMessage(alertMessage)
                .setView(inflater.inflate(R.layout.custom_dialog_log_out, null))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logOutAuthenticate();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do not need to do anything here now
                        dialog.dismiss();
                    }
                });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // Get the reference to positive and negative buttons
        Button positiveButton =  alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        // Set colour of Dialog Buttons
        positiveButton.setTextColor(Color.YELLOW);
        negativeButton.setTextColor(Color.YELLOW);
    }

    // *********************************************************************************************
    // ******************** Log Out the user *******************************************************
    // ******************** Destroy the user's session *********************************************
    // *********************************************************************************************
    private void logOutAuthenticate() {

        // Sign the user out
        logOutAuthentication.signOut();
        // Get the user's authentication session
        FirebaseUser user = logOutAuthentication.getCurrentUser();

        if (user == null) {
            executeIntent();
        } else {
            // User would not be logged out
            // Used for testing only !!!
            String userEmail = user.getEmail();
            String userId = user.getUid();
            Toast.makeText(getApplicationContext(), user + " " + userId + " " + userEmail, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Execute intent and bring the user to the Main Activity *****************
    // *********************************************************************************************
    private void executeIntent() {

        //String userLoggedOut = "You are logged out";
        //Toast.makeText(getApplicationContext(), userLoggedOut, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
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
