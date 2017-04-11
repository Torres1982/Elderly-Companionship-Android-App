package com.torres.companionshipapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class EventSelectorDate extends AppCompatActivity {

    String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_selector_date);

        retrieveEventNameFromIntent();
    }

    // *********************************************************************************************
    // ******************** Get intent with the Event Name *****************************************
    // *********************************************************************************************
    public void retrieveEventNameFromIntent() {

        String key = "spinnerValue";

        // Retrieve the Event Name from EventSelectorName activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventName = extras.getString(key);
            Toast.makeText(getApplicationContext(), eventName, Toast.LENGTH_LONG).show();
        }
    }
}
