package com.torres.companionshipapp;

import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Name: ProfilePage <br>
 * This class is a holder to redirect user to other activities:
 * (user can choose to find friends, events, add own details or show details, and log out).
 * Logged in user name is retrieved from Firebase DB (users tree) and displayed as a welcome message.
 * Log Out authentication is performed to kill the session.
 * @author B00073668 Artur Sukiennik
 * @version 1, date: 17.01.2017
 */
public class ProfilePage extends AppCompatActivity {

    // Declare global variables and objects
    String username;
    String welcome;
    String userId;
    String loggedUser;
    TextView usernameTextView;
    Button findFriendsButton;
    Button findEventsButton;
    Button logOutButton;
    Button addHobbyButton;
    Button deleteHobbyButton;
    FirebaseAuth logOutAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        // Get the Firebase authentication instance
        logOutAuthentication = FirebaseAuth.getInstance();

        // Assign the name of the user to the Text View
        usernameTextView = (TextView)findViewById(R.id.shown_name_text_view);

        // Call the supportive methods
        setUpActionBar();
        getUserNameFromFirebaseDatabase();

        // Retrieve the username through the intent (from Registration activity)
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            username = extras.getString("username");
            // Set text view with welcome message
            welcome = "Welcome " + username;
            usernameTextView.setText(welcome);
        }

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
        addListenerToAddDetailsButton();
        addListenerToShowDetails();
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
                Intent intent = new Intent (getApplicationContext(), EventsHolder.class);
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
    public void addListenerToAddDetailsButton () {
        addHobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), DetailUpdater.class);
                startActivity(intent);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Delete Hobby Button **************************
    // ******************** Redirect to the HobbyDelete page ***************************************
    // *********************************************************************************************
    public void addListenerToShowDetails () {
        deleteHobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), DetailsHolder.class);
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
        positiveButton.setTextColor(Color.DKGRAY);
        negativeButton.setTextColor(Color.DKGRAY);
    }

    // *********************************************************************************************
    // ******************** Log Out the user *******************************************************
    // ******************** Destroy the user session ***********************************************
    // *********************************************************************************************
    private void logOutAuthenticate() {

        // Sign the user out
        logOutAuthentication.signOut();
        // Get the user's authentication session
        FirebaseUser user = logOutAuthentication.getCurrentUser();

        if (user == null) {
            executeIntent();
        }
        else {
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

    // *********************************************************************************************
    // ******************** Get the User Name from Database ****************************************
    // *********************************************************************************************
    public void getUserNameFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Assign Firebase User Id for the user
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        }

        Query userDetails = databaseReference.child("users").child(userId);

        userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot databaseSnapshot) {

                if (databaseSnapshot.exists()) {
                    loggedUser = databaseSnapshot.child("username").getValue(String.class);
                    //Toast.makeText(ProfilePage.this, loggedUser, Toast.LENGTH_LONG).show();

                    // Set text view with welcome message
                    welcome = "Welcome " + loggedUser;
                    usernameTextView.setText(welcome);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }
}
