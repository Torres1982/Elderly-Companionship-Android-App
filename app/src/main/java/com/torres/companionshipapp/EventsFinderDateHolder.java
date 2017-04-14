package com.torres.companionshipapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventsFinderDateHolder extends AppCompatActivity {

    // Declare global variables and objects
    int day;
    int month;
    int year;
    Spinner spinner;
    String message;
    String userId;
    String dateFromDatePicker;
    String startDate;
    String endDate;
    String spinnerValue;
    TextView displayDateTextView;
    Button findEventsByDateButton;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_finder_date_holder);

        // Retrieve the reference of the objects from the profile_page.xml file
        displayDateTextView = (TextView) findViewById(R.id.text_view_date_chooser);
        findEventsByDateButton = (Button)findViewById(R.id.find_events_by_date);
        spinner = (Spinner) findViewById(R.id.event_number_spinner);
        datePicker = (DatePicker)findViewById(R.id.date_picker);

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with associated Listeners
        addListenerToFindEventsByDateButton();
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

                addNumberOfDaysToCurrentDate();
                prepareIntent();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Set up custom Spinner **************************************************
    // ******************** Edit Spinner text size and colors **************************************
    // *********************************************************************************************
    public void setUpSpinner() {

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.number_of_days_array, R.layout.spinner_edit);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    // *********************************************************************************************
    // ******************** Send intent with the Date Strings to next activity *********************
    // *********************************************************************************************
    public void prepareIntent() {

        final String keyStartDate = "startDate";
        final String keyEndDate = "endDate";

        // Prepare the intent and send the event name to next activity
        Intent eventIntent = new Intent (getApplicationContext(), EventsFinderDateListView.class);
        eventIntent.putExtra(keyStartDate, startDate);
        eventIntent.putExtra(keyEndDate, endDate);
        startActivity(eventIntent);
        finish();
    }

    // *********************************************************************************************
    // ******************** Calculate the range of days between dates ******************************
    // ******************** Within what number of days we should look for to find events ***********
    // *********************************************************************************************
    public void addNumberOfDaysToCurrentDate() {

        // Create the date string
        dateFromDatePicker = year + "-" + month + "-" + day;

        // Format date from "dd-MM-yyyy" to "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        // Get instance of Calendar
        Calendar dateCalendar = Calendar.getInstance();
        Calendar dateCalendar2 = Calendar.getInstance();

            try {
                dateCalendar.setTime(dateFormat.parse(dateFromDatePicker));
                dateCalendar2.setTime(dateFormat.parse(dateFromDatePicker));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        // Parse the spinner value string to integer (number of days)
        int numberOfDays = Integer.parseInt(spinnerValue);

        // Add the number of days to current date
        dateCalendar.add(Calendar.DATE, numberOfDays);
        dateCalendar2.add(Calendar.DATE, 0);

        // Format date back to "dd-MM-yyyy"
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        // Get the start date
        startDate = dateFormat2.format(dateCalendar2.getTime());
        // The last acceptable date used for searching the events
        endDate = dateFormat2.format(dateCalendar.getTime());

        //Toast.makeText(EventsFinderDateHolder.this, startDate + " " + endDate, Toast.LENGTH_LONG).show();
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
