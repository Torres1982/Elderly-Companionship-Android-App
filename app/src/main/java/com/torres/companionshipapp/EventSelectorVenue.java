package com.torres.companionshipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.*;

public class EventSelectorVenue extends AppCompatActivity {

    // Declare global variables and objects
    String citySpinnerValue;
    String dublinRegionSpinnerValue;
    String eventName;
    String eventDate;
    String eventTime;
    String eventStreet;
    String eventAddress;
    String userId;
    String databaseUser;
    final String EVENT_NAME_KEY = "eventName";
    final String EVENT_DATE_KEY = "eventDate";
    final String EVENT_TIME_KEY = "eventTime";
    final String CITY_DUBLIN = "Dublin";
    Spinner citySpinner;
    Spinner dublinRegionSpinner;
    Button createEventButton;
    EditText streetNameEditText;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_selector_venue);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Retrieve the reference of the objects
        streetNameEditText = (EditText)findViewById(R.id.edit_text_event_street);
        createEventButton = (Button)findViewById(R.id.create_event);
        citySpinner = (Spinner) findViewById(R.id.city_add_spinner);
        dublinRegionSpinner = (Spinner) findViewById(R.id.dublin_add_region_spinner);

        // Hide the Dublin Regions Spinner (will be shown when Dublin is selected from another spinner)
        dublinRegionSpinner.setVisibility(View.GONE);

        // Call the supportive methods
        setUpActionBar();
        retrieveEventNameAndDateAndTimeFromIntent();
        setUpSpinner();

        // Call the methods with assigned Listeners
        addListenerToSpinner();
        getUserNameFromFirebaseDatabase();
        addListenerToCreateEventButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Create Event Button **************************
    // *********************************************************************************************
    public void addListenerToCreateEventButton () {
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the value from the Spinner and Edit Text field
                citySpinnerValue = String.valueOf(citySpinner.getSelectedItem());
                dublinRegionSpinnerValue = String.valueOf(dublinRegionSpinner.getSelectedItem());
                eventStreet = streetNameEditText.getText().toString().trim();

                // Get the city only if there is no street name provided
                if (eventStreet.equals("")) {
                    if (citySpinnerValue.equals(CITY_DUBLIN)) {
                        eventAddress = dublinRegionSpinnerValue;
                    }
                    else {
                        eventAddress = citySpinnerValue;
                    }
                }
                // Get the city and the street name
                else {
                    if (citySpinnerValue.equals(CITY_DUBLIN)) {
                        eventAddress = eventStreet + ", " + dublinRegionSpinnerValue;
                    }
                    else {
                        eventAddress = eventStreet + ", " + citySpinnerValue;
                    }
                }

                Toast.makeText(getApplicationContext(), eventAddress, Toast.LENGTH_LONG).show();

                addEventToFirebaseDatabase();
                redirectToEventsHolderActivity();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Get intent with the Event Name, Date and Time **************************
    // *********************************************************************************************
    public void retrieveEventNameAndDateAndTimeFromIntent() {

        // Retrieve the Event Name and Date from EventSelectorDate activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventName = extras.getString(EVENT_NAME_KEY);
            eventDate = extras.getString(EVENT_DATE_KEY);
            eventTime = extras.getString(EVENT_TIME_KEY);
            //Toast.makeText(getApplicationContext(), eventName + "\n" + eventDate + "\n" + eventTime, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Redirect User to Event Holder activity *********************************
    // *********************************************************************************************
    public void redirectToEventsHolderActivity() {

        // Prepare the intent and send the username to Profile Page activity
        Intent friendsListIntent = new Intent (getApplicationContext(), EventsHolder.class);
        startActivity(friendsListIntent);
        finish();
    }

    // *********************************************************************************************
    // ******************** Set up custom Spinners *************************************************
    // ******************** Edit Spinner text size and colors **************************************
    // *********************************************************************************************
    public void setUpSpinner() {

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.city_array, R.layout.spinner_edit);
        citySpinner.setAdapter(spinnerArrayAdapter);

        ArrayAdapter spinnerArrayAdapter2 = ArrayAdapter.createFromResource(this, R.array.dublin_regions_array, R.layout.spinner_edit);
        dublinRegionSpinner.setAdapter(spinnerArrayAdapter2);
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Spinner **************************************
    // *********************************************************************************************
    public void addListenerToSpinner() {
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // Get the city from the selected dropdown list
                citySpinnerValue = String.valueOf(citySpinner.getSelectedItem());

                    // Show or hide the Dublin Regions Spinner
                    if (citySpinnerValue.equals(CITY_DUBLIN)) {
                        dublinRegionSpinner.setVisibility(View.VISIBLE);
                    }
                    else {
                        dublinRegionSpinner.setVisibility(View.GONE);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nothing to be done here
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
    // ******************** Save User with details to Database *************************************
    // *********************************************************************************************
    private void addEventToFirebaseDatabase() {

        // Values used for the Hash Map (to save to Firebase database)
        String key1 = "date";
        String key2 = "time";
        String key3 = "city";
        String key4 = "street";
        String key5 = "partaker";
        String key6 = "creator";
        String defaultPartaker = "Partaker";

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/");

        // Create a Hash Map of User's details
        java.util.Map<String, String> userDetailsHashMap = new HashMap<>();

        // Put User's details to the Hash Map
        userDetailsHashMap.put(key1, eventDate);
        userDetailsHashMap.put(key2, eventTime);
        userDetailsHashMap.put(key3, citySpinnerValue);
        userDetailsHashMap.put(key4, eventStreet);
        userDetailsHashMap.put(key5, defaultPartaker);
        userDetailsHashMap.put(key6, databaseUser);

        // Save Event details in the Firebase database (at root "events")
        databaseReference.child("events").child(eventName).push().setValue(userDetailsHashMap);
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
                    //Toast.makeText(EventSelectorVenue.this, databaseUser, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }
}
