package com.torres.companionshipapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    // Declare global variables and objects
    Button registerButton;
    String username;
    String email;
    String password;
    String message;
    String userId;
    EditText name;
    EditText registrationEmail;
    EditText registrationPassword;
    ProgressBar registrationProgressBar;
    FirebaseAuth authenticationRegistration;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    private boolean isValidatedUsername;
    private boolean isValidatedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Get the Firebase authentication instance
        authenticationRegistration = FirebaseAuth.getInstance();

        // Call the supportive methods
        setUpActionBar();
        checkNetworkAvailability();

        // Get references of the objects from the register.xml file
        name = (EditText)findViewById(R.id.edit_text_username);
        registrationEmail = (EditText)findViewById(R.id.edit_text_register_email);
        registrationPassword = (EditText)findViewById(R.id.edit_text_register_password);
        registrationProgressBar = (ProgressBar)findViewById(R.id.register_progress_bar);
        registerButton = (Button)findViewById(R.id.button_register);

        // Call the methods with assigned Listeners
        addListenerToRegistrationButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Registration Button **************************
    // ******************** Authenticate the user email with password ******************************
    // ******************** Redirect to the User Profile Page **************************************
    // *********************************************************************************************
    public void addListenerToRegistrationButton () {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the user name, email and password from the Edit Text fields
                email = registrationEmail.getText().toString().trim();
                password = registrationPassword.getText().toString().trim();
                username = name.getText().toString().trim();

                final int passwordLength = 5;
                String emailWarning = "Enter email address";
                String passwordWarning = "Enter password";
                String nameWarning = "Enter your name";
                String passwordTooShort = "Password must have " + passwordLength + " or more characters";
                String validateOnlyLetters = "Use letters only";
                String validateEmailFormat = "Use correct email format: name.surname@gmail.com";

                // Email Edit Text field has been left empty
                if (TextUtils.isEmpty(email)) {
                    message = emailWarning;
                    showDialog();
                    return;
                }

                // User email validation (matcher plus pattern)
                validateInputEmail();

                // User email has no correct format
                if (!isValidatedEmail) {
                    message = validateEmailFormat;
                    showDialog();
                    return;
                }

                // Password Edit Text field has been left empty
                if (TextUtils.isEmpty(password)) {
                    message = passwordWarning;
                    showDialog();
                    return;
                }

                // Password length is too short
                if (password.length() < passwordLength) {
                    message = passwordTooShort;
                    showDialog();
                    return;
                }

                // Username Edit Text field has been left empty
                if (TextUtils.isEmpty(username)) {
                    message = nameWarning;
                    showDialog();
                    return;
                }

                // User name validation (to use only letters)
                validateInputUsername();

                // User name input field contains non-alphabetical values
                if (!isValidatedUsername) {
                    message = validateOnlyLetters;
                    showDialog();
                    return;
                }

                // Enable the Progress Bar
                registrationProgressBar.setVisibility(View.VISIBLE);

                // Authenticate the user with associated email address as an user login
                authenticationRegistration.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            String userCreated = "You are registered";
                            String authenticationFailed = "Try again";

                            // Disable the Progress Bar
                            registrationProgressBar.setVisibility(View.GONE);

                            if (!task.isSuccessful()) {
                                Toast.makeText(Registration.this, authenticationFailed, Toast.LENGTH_LONG).show();
                            }
                            else {
                                // Add a user and details to Database
                                saveUserToFirebaseDatabase();

                                Toast.makeText(Registration.this, userCreated, Toast.LENGTH_LONG).show();

                                // Prepare the intent and send the username to Profile Page activity
                                Intent registrationIntent = new Intent (getApplicationContext(), ProfilePage.class);
                                registrationIntent.putExtra("username", username);
                                startActivity(registrationIntent);
                                finish();
                            }
                        }
                });
            }
        });
    }

    // *********************************************************************************************
    // ******************** Disable the Progress Bar on Resume activity ****************************
    // *********************************************************************************************
    @Override
    protected void onResume() {
        super.onResume();
        registrationProgressBar.setVisibility(View.GONE);
    }

    // *********************************************************************************************
    // ******************** Validate the username with Matcher and Pattern *************************
    // *********************************************************************************************
    public void validateInputUsername() {

        String usernamePattern = "^[a-zA-Z ]+$";

        // Validate the input so it can contains only letters
        Pattern pattern = Pattern.compile(usernamePattern);
        Matcher matcher = pattern.matcher(username);

        // Check if matcher matches the pattern (regular expression)
        isValidatedUsername = matcher.matches();
    }

    // *********************************************************************************************
    // ******************** Validate the username with Matcher and Pattern *************************
    // *********************************************************************************************
    public void validateInputEmail() {

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.+[a-zA-Z]{2,}$";

        // Validate the input so it can contains only letters
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);

        // Check if matcher matches the pattern (regular expression)
        isValidatedEmail = matcher.matches();
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
    // ******************** Check the Network Availability *****************************************
    // *********************************************************************************************
    private void checkNetworkAvailability() {

        String wifiConnected = "Connected to WiFi";
        String mobileDataConnected = "Connected to Mobile Data";
        String noNetworkConnection = "Connect to the Internet";
        final String MY_TAG = "MY_TAG";

        // Get Connection Manager and Information about Network Connectivity
        ConnectivityManager connectManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            // Checking for Wi-Fi network connection
            if (networkInfo.getTypeName().equalsIgnoreCase("WiFi")) {
                if (networkInfo.isConnected()) {
                    Log.i(MY_TAG, wifiConnected);
                    //Toast.makeText(Registration.this, wifiConnected, Toast.LENGTH_LONG).show();
                }
            }
            // Checking for Mobile Data network connection
            if (networkInfo.getTypeName().equalsIgnoreCase("Mobile")) {
                if (networkInfo.isConnected()) {
                    Log.i(MY_TAG, mobileDataConnected);
                    //Toast.makeText(Registration.this, mobileDataConnected, Toast.LENGTH_LONG).show();
                }
            }
        }
        // No network accessible
        else {
            Log.i(MY_TAG, noNetworkConnection);
            message = noNetworkConnection;
            showDialog();
            //Toast.makeText(Registration.this, noNetworkConnection, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Save User with details to Database *************************************
    // *********************************************************************************************
    private void saveUserToFirebaseDatabase() {

        // Values used for the Hash Map (to save to Firebase database)
        String key1 = "username";
        String key2 = "email";
        String key3 = "hobby";
        String key4 = "age";
        String hobby = "Dummy";
        String age = "Dummy";

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/");

        // Create a Hash Map of User's details
        Map<String, String> userDetailsHashMap = new HashMap<>();

        // Get the Firebase User instance
        firebaseUser = authenticationRegistration.getCurrentUser();

        // Retrieve Firebase User Id for currently logged in user
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        }
        else {
            // More likely it will not happen
            Toast.makeText(Registration.this, "Empty User Value!", Toast.LENGTH_LONG).show();
        }

        // Put User's details to the Hash Map
        userDetailsHashMap.put(key1, username);
        userDetailsHashMap.put(key2, email);
        userDetailsHashMap.put(key3, hobby);
        userDetailsHashMap.put(key4, age);

        // Save User's details in the Firebase database (at root "users")
        databaseReference.child("users").child(userId).setValue(userDetailsHashMap);
    }
}
