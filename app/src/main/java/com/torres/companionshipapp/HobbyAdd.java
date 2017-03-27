package com.torres.companionshipapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HobbyAdd extends AppCompatActivity {

    // Declare global variables and objects
    Button addMyHobbyButton;
    EditText addMyHobbyEditText;
    String hobbyFromEditText;
    String message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hobby_add);

        // Call the supportive methods
        setUpActionBar();

        // Retrieve the reference of the objects from the hobby_add.xml file
        addMyHobbyButton = (Button)findViewById(R.id.button_hobby_add);
        addMyHobbyEditText = (EditText)findViewById(R.id.edit_text_hobby_add);

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
                // Get the email and password from the Edit Text fields
                hobbyFromEditText = addMyHobbyEditText.getText().toString().trim();

                validateInputAddHobby(hobbyFromEditText);
                //showDialog();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Validate the input taken from the User Edit Text field *****************
    // *********************************************************************************************
    public void validateInputAddHobby(String string) {

        // Check if the Edit Text field has not been left empty
        if (string == null || string.length() == 0) {
            message = "Enter Your Hobby";
            showDialog();
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else {
            // Validate the input so it can contains only letters
            Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
            Matcher matcher = pattern.matcher(string);

            // Check if matcher matches the pattern (regular expression)
            boolean isValidated = matcher.matches();

            // The matcher does not match the regular expression
            if (!isValidated) {
                message = "Use Letters Only";
                showDialog();
                //Toast.makeText(getApplicationContext(), validatedWrong, Toast.LENGTH_LONG).show();
            }
            else {
                String validatedGood = "Validation successful";
                Toast.makeText(getApplicationContext(), validatedGood, Toast.LENGTH_LONG).show();
            }
        }
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
    // ******************** Instantiate the Custom Dialog class ************************************
    // ******************** Retrieve the Custom Dialog View ****************************************
    // *********************************************************************************************
    private void showDialog() {

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
        //message = "Enter Your Hobby";

        // Create object of Shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Assign the string value for the Custom Dialog title
        editor.putString(key, message);
        // Commit the Edit
        editor.apply();
    }
}
