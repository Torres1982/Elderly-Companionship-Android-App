package com.torres.companionshipapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Name: HobbyAdd <br>
 * This class adds current user's hobby to the Firebase DB.
 * Customized Spinner is used to select the hobby.
 * @author B00073668 Artur Sukiennik
 * @version 3, date: 11.04.2017
 */
public class HobbyAdd extends AppCompatActivity {

    // Declare global variables and objects
    Button addMyHobbyButton;
    String message;
    String spinnerValue;
    Spinner spinner;
    String userId;
    FirebaseAuth firebaseAuthentication;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hobby_add);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Retrieve the reference of the objects from the hobby_add.xml file
        addMyHobbyButton = (Button)findViewById(R.id.button_hobby_add);
        spinner = (Spinner) findViewById(R.id.hobby_add_spinner);

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with assigned Listeners
        addListenerToHobbyAddButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Add Hobby Button *****************************
    // ******************** Add hobby to the Database **********************************************
    // *********************************************************************************************
    public void addListenerToHobbyAddButton () {
        addMyHobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the value from the selected dropdown list
                spinnerValue = String.valueOf(spinner.getSelectedItem());
                showDialog();
                addHobbyToFirebaseDatabase();
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
    // ******************** Create Shared Preferences *************************************************
    // ******************** Send the title for the Custom Dialog ***********************************
    // *********************************************************************************************
    public void constructSharedPreferences() {

        String key = "message";
        message = spinnerValue + " has been added to your hobbies";

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
    // ******************** Set up custom Spinner **************************************************
    // ******************** Edit Spinner text size and colors **************************************
    // *********************************************************************************************
    public void setUpSpinner() {

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.hobby_array, R.layout.spinner_edit);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    // Add a user and details to Database
    public void addHobbyToFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/");

        // Get the Firebase User instance
        firebaseUser = firebaseAuthentication.getCurrentUser();

        // Retrieve Firebase User Id for currently logged in user
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        }

        // Save User's details in the Firebase database (at root "users")
        databaseReference.child("users").child(userId).child("hobby").setValue(spinnerValue);
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
