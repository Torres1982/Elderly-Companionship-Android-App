package com.torres.companionshipapp;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsFinder extends AppCompatActivity {

    // Declare global variables and objects
    String spinnerValue;
    Spinner spinner;
    Button findFriendsButton;
    String userId;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_finder);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Get reference of the objects from the friends_finder.xml file
        findFriendsButton = (Button)findViewById(R.id.button_find_friends);
        spinner = (Spinner) findViewById(R.id.hobby_spinner);

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with associated Listeners
        addListenerToFindFriendsButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends Button **************************
    // ******************** Redirect ... ***********************************************************
    // *********************************************************************************************
    public void addListenerToFindFriendsButton () {
        findFriendsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

                getUserDetailsFromFirebaseDatabase();

                // Get the value from the selected dropdown list
                spinnerValue = String.valueOf(spinner.getSelectedItem());

                String chosenValue = "Value chosen: ";
                Toast.makeText(FriendsFinder.this, chosenValue + spinnerValue, Toast.LENGTH_LONG).show();
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

        // Get reference of the object from the action_bar_title.xml file
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

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.hobby_array, R.layout.spinner_edit);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    // *********************************************************************************************
    // ******************** Get the User Details from Database *************************************
    // *********************************************************************************************
    public void getUserDetailsFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/users");
        //databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Assign Firebase User Id for the user
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        }

        //Query userDetails = databaseReference.child("users").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String hobby;
                String databaseUser;

                // Iterate through the database
                for (DataSnapshot myDatabase : dataSnapshot.getChildren()) {

                    // Get User name from Firebase database
                    databaseUser = myDatabase.child("username").getValue(String.class);
                    // Get User hobby from Firebase database
                    hobby = myDatabase.child("hobby").getValue(String.class);

                    if (spinnerValue.equals(hobby)) {
                        Toast.makeText(FriendsFinder.this, databaseUser + " " + hobby, Toast.LENGTH_LONG).show();
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }
}
