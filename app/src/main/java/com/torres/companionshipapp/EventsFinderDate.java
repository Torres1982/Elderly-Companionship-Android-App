package com.torres.companionshipapp;

import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class EventsFinderDate extends AppCompatActivity {

    // Declare global variables and objects
    int day;
    int month;
    int year;
    Spinner spinner;
    String spinnerValue;
    TextView displayDateTextView;
    Button findEventsByDateButton;
    DatePicker datePicker;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView eventsListView;
    List<String> eventsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_finder_date);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Retrieve the reference of the objects from the profile_page.xml file
        displayDateTextView = (TextView) findViewById(R.id.text_view_date_chooser);
        findEventsByDateButton = (Button)findViewById(R.id.find_events_by_date);
        datePicker = (DatePicker)findViewById(R.id.date_picker);

        // Create an Array List of Friends details
        eventsArrayList = new ArrayList<>();

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with associated Listeners
        //getUserNameFromFirebaseDatabase();
        addListenerToFindEventsByDateButton();
        //getClickedValueFromListView();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Events by Date Button *******************
    // *********************************************************************************************
    public void addListenerToFindEventsByDateButton () {
        findEventsByDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                // Get the value from the selected dropdown list
                spinnerValue = String.valueOf(spinner.getSelectedItem());

                // Retrieve the values for day, month and year from a Date Picker
                day = datePicker.getDayOfMonth();
                // Month starts at 0 so we need to add 1
                month = datePicker.getMonth() + 1;
                year = datePicker.getYear();

                //getEventDetailsFromFirebaseDatabase();
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

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.number_of_days_array, R.layout.spinner_edit);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}
