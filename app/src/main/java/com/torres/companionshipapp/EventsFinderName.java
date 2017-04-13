package com.torres.companionshipapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.*;
import java.util.Map;

public class EventsFinderName extends AppCompatActivity {

    // Declare global variables and objects
    Spinner spinner;
    String spinnerValue;
    String databaseUser;
    String databaseEmail;
    String selectedValue;
    String message;
    String userId;
    String eventName;
    String eventDate;
    String eventTime;
    String eventStreet;
    String eventCity;
    String eventCreator;
    String creatorEmail;
    String tokenEventName;
    String tokenEventDate;
    String tokenEventTime;
    String tokenEventCity;
    String tokenEmail;
    String clearedEventName;
    String clearedEventDate;
    String clearedEventTime;
    String clearedEventCity;
    String clearedEmail;
    Button findEventsButton;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView eventsListView;
    List<String> eventsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_finder_name);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Get reference of the objects from the events_finder_name.xml file
        findEventsButton = (Button)findViewById(R.id.button_find_events);
        spinner = (Spinner) findViewById(R.id.event_spinner);
        eventsListView = (ListView) findViewById(R.id.list_view_events_by_name);

        // Create an Array List of Friends details
        eventsArrayList = new ArrayList<>();

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with associated Listeners
        getUserNameFromFirebaseDatabase();
        addListenerToFindEventsButton();
        getClickedValueFromListView();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends Button **************************
    // *********************************************************************************************
    public void addListenerToFindEventsButton () {
        findEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

                // Get the value from the selected dropdown list
                spinnerValue = String.valueOf(spinner.getSelectedItem());

                //String chosenValue = "Value chosen: ";
                //Toast.makeText(EventsFinderName.this, chosenValue + spinnerValue, Toast.LENGTH_LONG).show();

                getEventDetailsFromFirebaseDatabase();
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

    // *********************************************************************************************
    // ******************** Set up custom Spinner **************************************************
    // ******************** Edit Spinner text size and colors **************************************
    // *********************************************************************************************
    public void setUpSpinner() {

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.event_array, R.layout.spinner_edit);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    // *********************************************************************************************
    // ******************** Get the Event Details from Database ************************************
    // *********************************************************************************************
    public void getEventDetailsFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/events");

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Assign Firebase User Id for the user
            userId = firebaseUser.getUid();
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isEventFound = false;

                // Clear the content of the Array List before displaying a new set of rows
                eventsArrayList.clear();

                // Iterate through the database
                for (DataSnapshot mainLoop : dataSnapshot.getChildren()) {
                    for (DataSnapshot myDatabase : mainLoop.getChildren()) {
                        // Get Event details from Firebase database
                        eventName = myDatabase.child("name").getValue(String.class);
                        eventDate = myDatabase.child("date").getValue(String.class);
                        eventTime = myDatabase.child("time").getValue(String.class);
                        eventStreet = myDatabase.child("street").getValue(String.class);
                        eventCity = myDatabase.child("city").getValue(String.class);
                        eventCreator = myDatabase.child("creator").getValue(String.class);
                        creatorEmail = myDatabase.child("email").getValue(String.class);
                        //Toast.makeText(EventsFinderName.this, creatorEmail + " " + eventCreator, Toast.LENGTH_LONG).show();

                        // Display only events with matching event name
                        if (spinnerValue.equals(eventName)) {
                            String event = "Click to Attend Event:";
                            eventsArrayList.add(event + " \n" +
                                                eventName + " \n" +
                                                eventCreator + " \n" +
                                                creatorEmail + " \n" +
                                                eventDate + " \n" +
                                                eventTime + " \n" +
                                                eventCity + " \n" +
                                                eventStreet);

                            // At least one event has been found in the database
                            isEventFound = true;
                        }
                    }
                }
                createListView();

                if(isEventFound) {
                    isEventFound = false;
                }
                else {
                    showDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
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
        message = "There are no " + spinnerValue + " events found";

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
    // ******************** Create a List View with Events detail **********************************
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
                //Toast.makeText(EventsFinderName.this, selectedValue, Toast.LENGTH_LONG).show();

                tokenizeSelectedValueFromListView(selectedValue);
                addEventToEventsListFirebaseDb();
                redirectToEventsListActivity();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Split the Selected value from the List View ****************************
    // *********************************************************************************************
    public void tokenizeSelectedValueFromListView(String value) {

        // Splitting the String
        String [] tokenStrings = value.split(" ");

        for (int i = 0; i < tokenStrings.length; i++) {
            //Toast.makeText(EventsFinderName.this, i + " " + tokenStrings[i], Toast.LENGTH_LONG).show();

            // Assign the values from the array to user detail fields
            tokenEventName = tokenStrings[4];
            tokenEmail = tokenStrings[6];
            tokenEventDate = tokenStrings[7];
            tokenEventTime = tokenStrings[8];
            tokenEventCity = tokenStrings[9];

            removeSpacesFromTokenizedStrings();

            eventName = clearedEventName;
            creatorEmail = clearedEmail;
            eventDate = clearedEventDate;
            eventTime = clearedEventTime;
            eventCity = clearedEventCity;
        }
    }

    // *********************************************************************************************
    // ******************** Remove "\n" characters from the Strings ********************************
    // *********************************************************************************************
    public void removeSpacesFromTokenizedStrings() {

        clearedEventName = tokenEventName.replace("\n", "");
        clearedEventDate = tokenEventDate.replace("\n", "");
        clearedEventTime = tokenEventTime.replace("\n", "");
        clearedEventCity = tokenEventCity.replace("\n", "");
        clearedEmail = tokenEmail.replace("\n", "");
    }

    // *********************************************************************************************
    // ******************** Redirect User to Events List activity **********************************
    // *********************************************************************************************
    public void redirectToEventsListActivity() {

        // Prepare the intent and send the username to Profile Page activity
        Intent friendsListIntent = new Intent (getApplicationContext(), EventsList.class);
        startActivity(friendsListIntent);
        finish();
    }

    // *********************************************************************************************
    // ******************** Save Event with details to Database ************************************
    // *********************************************************************************************
    public void addEventToEventsListFirebaseDb() {

        // Values used for the Hash Map (to save to Firebase database)
        String key1 = "name";
        String key2 = "date";
        String key3 = "time";
        String key4 = "city";
        String key5 = "email";

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/");

        // Create a Hash Map of User's details
        Map<String, String> eventsDetailHashMap = new HashMap<>();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Assign Firebase User Id for the user
            userId = firebaseUser.getUid();
        }

        // Put Events details to the Hash Map
        eventsDetailHashMap.put(key1, eventName);
        eventsDetailHashMap.put(key2, eventDate);
        eventsDetailHashMap.put(key3, eventTime);
        eventsDetailHashMap.put(key4, eventCity);
        eventsDetailHashMap.put(key5, creatorEmail);

        // Save Event Partakers details in the Firebase database (at root "event partakers")
        databaseReference.child("event partakers").child(eventName).child(userId).push().setValue(eventsDetailHashMap);
    }

    // *********************************************************************************************
    // ******************** Get the User Name from Database ****************************************
    // *********************************************************************************************
    public void getUserNameFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Assign Firebase User Id for the user
            userId = firebaseUser.getUid();
        }

        // Get only queries that relate to currently logged in user
        Query eventCreatorName = databaseReference.child("users").child(userId);

        eventCreatorName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Iterate through the database
                if (dataSnapshot.exists()) {
                    // Get User name from Firebase database
                    databaseUser = dataSnapshot.child("username").getValue(String.class);
                    // Get User email from Firebase database
                    databaseEmail = dataSnapshot.child("email").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }
}
