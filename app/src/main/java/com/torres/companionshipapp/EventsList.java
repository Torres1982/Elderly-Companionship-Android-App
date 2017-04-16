package com.torres.companionshipapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Name: EventsList <br>
 * This class retrieves the list of events from Firebase DB.
 * User can click on the list view item to send email to the event creator.
 * @author B00073668 Artur Sukiennik
 * @version 1, date: 27.03.2017
 */
public class EventsList extends AppCompatActivity {

    // Declare global variables and objects
    String selectedValue;
    String message;
    String userId;
    String loginEmail;
    String eventCity;
    String eventCreator;
    String eventDate;
    String eventEmail;
    String eventName;
    String eventStreet;
    String eventTime;
    String tokenEmail;
    String clearedEmail;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView eventsListView;
    List<String> eventsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_list);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Get reference of the objects
        eventsListView = (ListView) findViewById(R.id.events_list_view);

        // Create an Array List of Friends details
        eventsArrayList = new ArrayList<>();

        // Call the supportive methods
        setUpActionBar();
        receiveEmailFromSharedPreferences();

        // Call the methods with associated Listeners
        getEventsDetailsFromFirebaseDatabase();
        getClickedValueFromListView();
    }

    // *********************************************************************************************
    // ******************** Get the Events Details from Database ***********************************
    // *********************************************************************************************
    public void getEventsDetailsFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Assign Firebase User Id for the user
            userId = firebaseUser.getUid();
            // Get Firebase User email
            //userEmail = firebaseUser.getEmail();
        }

        // Get only queries that relate to currently logged in user
        Query eventsDetails = databaseReference.child("events");

        eventsDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Iterate through the friends JSON tree (for current user)
                for (DataSnapshot mainLoop : dataSnapshot.getChildren()) {
                    for (DataSnapshot myDatabase : mainLoop.getChildren()) {
                        eventCity = myDatabase.child("city").getValue(String.class);
                        eventCreator = myDatabase.child("creator").getValue(String.class);
                        eventDate = myDatabase.child("date").getValue(String.class);
                        eventEmail = myDatabase.child("email").getValue(String.class);
                        eventName = myDatabase.child("name").getValue(String.class);
                        eventStreet = myDatabase.child("street").getValue(String.class);
                        eventTime = myDatabase.child("time").getValue(String.class);

                        String message = "Click to send email to:";
                        eventsArrayList.add(message + " \n" +
                                eventName + " \n" +
                                eventDate + " \n" +
                                eventTime + " \n" +
                                eventCreator + " \n" +
                                eventEmail + " \n" +
                                eventCity + " \n" +
                                eventStreet);

                        //Toast.makeText(FriendsList.this, eventName + "\n" + eventEmail, Toast.LENGTH_LONG).show();
                        /*
                        if (loginEmail.equals(eventEmail)) {
                            eventsListView.setClickable(false);
                        }
                        */
                    }
                }
                createListView();

                if (eventsArrayList.isEmpty()) {
                    showDialog();
                    prepareIntent();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }

    // *********************************************************************************************
    // ******************** Redirect to EventsFinderDateHolder activity if list is empty ***********
    // *********************************************************************************************
    public void prepareIntent() {

        // Redirect to Event
        Intent eventIntent = new Intent(getApplicationContext(), EventsFinderDateHolder.class);
        startActivity(eventIntent);
        finish();
    }

    // *********************************************************************************************
    // ******************** Instantiate the Custom Dialog class ************************************
    // ******************** Retrieve the Custom Dialog View ****************************************
    // *********************************************************************************************
    private void showDialog() {

        // Get the message to display on dialog
        constructSharedPreferences();

        CustomDialog customDialog = new CustomDialog();
        customDialog.setCancelable(false);
        customDialog.show(getSupportFragmentManager(), "CUSTOM DIALOG");
    }

    // *********************************************************************************************
    // ******************** Create Shared Preferences **********************************************
    // ******************** Send the title for the Custom Dialog ***********************************
    // *********************************************************************************************
    public void constructSharedPreferences() {

        String key = "message";
        message = "You do not have any events added yet";

        // Create object of Shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Assign the string value for the Custom Dialog title
        editor.putString(key, message);
        // Commit the Edit
        editor.apply();
    }

    // *********************************************************************************************
    // ******************** Create a List View with Friends details ********************************
    // *********************************************************************************************
    public void createListView() {
        // Set Array Adapter to create a Scrollable List View
        eventsListView.setAdapter(new ArrayAdapter<>(this, R.layout.custom_simple_list_item, eventsArrayList));
    }

    // *********************************************************************************************
    // ******************** Get the Value from Selected List View **********************************
    // *********************************************************************************************
    public void getClickedValueFromListView() {

        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = (String) parent.getItemAtPosition(position);
                //Toast.makeText(EventsList.this, selectedValue, Toast.LENGTH_LONG).show();

                tokenizeSelectedValueFromListView(selectedValue);

                String [] addresses = {eventEmail};
                String subject = "Hello from Companionship App User";

                // Below email address used for testing only to check for incoming emails on my email box
                //String [] addresses = {"artur.sukiennik82@gmail.com"};

                // Call the email intent
                createGMail(addresses, subject);
            }
        });
    }

    // *********************************************************************************************
    // ******************** Split the Selected value from the List View ****************************
    // *********************************************************************************************
    public void tokenizeSelectedValueFromListView(String value) {

        String [] tokenStrings = value.split(" ");

        for (int i = 0; i < tokenStrings.length; i++) {
            //Toast.makeText(EventsList.this, i + " " + tokenStrings[i], Toast.LENGTH_LONG).show();

            // Assign the values from the array to user detail fields
            tokenEmail = tokenStrings[9];

            removeSpacesFromTokenizedStrings();

            eventEmail = clearedEmail;
        }
    }

    // *********************************************************************************************
    // ******************** Remove "\n" characters from the Strings ********************************
    // *********************************************************************************************
    public void removeSpacesFromTokenizedStrings() {

        clearedEmail = tokenEmail.replace("\n", "");
    }

    // *********************************************************************************************
    // ******************** GMail intent ***********************************************************
    // *********************************************************************************************
    public void createGMail(String[] addresses, String subject) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // *********************************************************************************************
    // ******************** Get email from login page **********************************************
    // *********************************************************************************************
    public void receiveEmailFromSharedPreferences() {

        String keyEmail = "email";
        String value = "";

        // Retrieve the String message using Shared Preferences (Custom Dialog Box message)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginEmail = sharedPreferences.getString(keyEmail, value);

        //Toast.makeText(EventsList.this, loginEmail, Toast.LENGTH_LONG).show();
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

        // Get reference of the object from the action_bar_title.xml file
        TextView myTitleTextView = (TextView) myCustomView.findViewById(R.id.action_bar_title);

        // Set up custom Action Bar
        String actionBarTitle = "Elderly Companionship";
        myTitleTextView.setText(actionBarTitle);
        myCustomActionBar.setCustomView(myCustomView);
        myCustomActionBar.setDisplayShowCustomEnabled(true);
    }
}
