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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

/**
 * Name: EventsFinderDateListView <br>
 * This class displays the list view of events retrieved from Firebase DB.
 * It retrieves start and end dates of event from previous activity through intents.
 * Selected events are added to Firebase DB (at root: event partakers).
 * Operations on dates are performed (formatting and parsing dates).
 * @author B00073668 Artur Sukiennik
 * @version 6, date: 10.04.2017
 */
public class EventsFinderDateListView extends AppCompatActivity {

    // Declare global variables and objects
    String day;
    String month;
    String year;
    String newDate;
    String selectedValue;
    String userId;
    String message;
    String startDate;
    String endDate;
    String eventFixedDate;
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
    final String START_DATE_KEY = "startDate";
    final String END_DATE_KEY = "endDate";
    Date dateToStart;
    Date dateToFinish;
    Date dateEventFixed;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView eventsListView;
    List<String> eventsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_finder_date_list_view);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Get reference of the objects
        eventsListView = (ListView) findViewById(R.id.events_date_list_view);

        // Create an Array List of Friends details
        eventsArrayList = new ArrayList<>();

        // Call the supportive methods
        setUpActionBar();
        retrieveEventNameAndDateFromIntent();
        getEventDetailsFromFirebaseDatabase();
        getClickedValueFromListView();
    }

    // *********************************************************************************************
    // ******************** Get intent with the Event Name and Date ********************************
    // *********************************************************************************************
    public void retrieveEventNameAndDateFromIntent() {

        // Retrieve the Event Name and Date from EventSelectorDate activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            startDate = extras.getString(START_DATE_KEY);
            endDate = extras.getString(END_DATE_KEY);
            //Toast.makeText(getApplicationContext(), startDate + " " + endDate, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Get the Event Details from Database ************************************
    // *********************************************************************************************
    public void getEventDetailsFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/events");

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
                        //Toast.makeText(EventsFinderDateListView.this, eventDate, Toast.LENGTH_LONG).show();

                        tokenizeEventDate(eventDate);
                        editEventDate();
                        formatDates();

                            // Display only events with matching event date range
                            if (dateEventFixed.after(dateToStart) && dateEventFixed.before(dateToFinish)) {
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
        message = "There are no events found within specified date";

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
        Intent friendsListIntent = new Intent(getApplicationContext(), EventsList.class);
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
    // ******************** Fix the event date *****************************************************
    // *********************************************************************************************
    public void editEventDate() {

        //tokenizeEventDate(eventDate);

        // Format date from "dd-MM-yyyy" to "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        // Get instance of Calendar
        Calendar dateCalendar = Calendar.getInstance();

        try {
            dateCalendar.setTime(dateFormat.parse(newDate));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        // Add the number of days to event date
        dateCalendar.add(Calendar.DATE, 0);
        // Format date back to "dd-MM-yyyy"
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        // The last acceptable date used for searching the events
        eventFixedDate = dateFormat2.format(dateCalendar.getTime());

        //Toast.makeText(EventsFinderDateListView.this, eventFixedDate, Toast.LENGTH_LONG).show();
    }

    // *********************************************************************************************
    // ******************** Split the event date ***************************************************
    // *********************************************************************************************
    public void tokenizeEventDate(String oldDate) {

        // Splitting the String
        String [] tokenDateStrings = oldDate.split("-");
        List<String> list = new ArrayList<>();

        for (int i = 0; i < tokenDateStrings.length; i++) {
            String string = tokenDateStrings[i];
            list.add(string);
        }

        day = list.get(0);
        month = list.get(1);
        year = list.get(2);

        // Reverse the day, month and year of the date
        newDate = year + "-" + month + "-" + day;
    }

    // *********************************************************************************************
    // ******************** Format dates to perform operations on them *****************************
    // *********************************************************************************************
    public void formatDates() {

        // This Object can read dates in format "dd-MM-yyyy"
        DateFormat datesFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        try {
            // Convert from String to Date
            dateToStart = datesFormat.parse(startDate);
            dateToFinish = datesFormat.parse(endDate);
            dateEventFixed = datesFormat.parse(eventFixedDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        //Toast.makeText(EventsFinderDateListView.this, dateToStart + " " + dateToFinish + " " + dateEventFixed, Toast.LENGTH_LONG).show();
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
