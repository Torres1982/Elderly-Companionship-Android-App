package com.torres.companionshipapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Name: Login <br>
 * This class allows a user to log in to the application.
 * Firebase authentication is used.
 * Each user's login is stored in Firebase database (under root: users).
 * User email and password are used as credentials.
 * Custom validation is implemented.
 * Network connectivity is checked upon the login (if there is no network connection -
 * user is shown a custom dialog box to choose whether to exit the application or to be redirected
 * to Wi-Fi settings to switch it on).
 * @author B00073668 Artur Sukiennik
 * @version 8, date: 15.01.2017
 */
public class Login extends AppCompatActivity {

    // Declare global variables and objects
    Button loginButton;
    String email;
    String password;
    String message;
    EditText loginEmail;
    EditText loginPassword;
    ProgressBar loginProgressBar;
    private boolean isValidatedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep the user's session in memory
        // Only clicking the Log Out button will clear it
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(Login.this, ProfilePage.class));
            finish();
        }

        setContentView(R.layout.login);

        // Call the supportive methods
        setUpActionBar();
        checkNetworkAvailability();

        // Get references of the objects from the login.xml file
        loginButton = (Button)findViewById(R.id.button_login);
        loginEmail = (EditText)findViewById(R.id.edit_text_login_email);
        loginPassword = (EditText)findViewById(R.id.edit_text_login_password);
        loginProgressBar = (ProgressBar)findViewById(R.id.login_progress_bar);

        // Call the methods with assigned Listeners
        addListenerToLoginButton();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Login Button *********************************
    // ******************** Authenticate the user email with password ******************************
    // ******************** Redirect to the User Profile Page **************************************
    // *********************************************************************************************
    public void addListenerToLoginButton () {
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Get the email and password from the Edit Text fields
                email = loginEmail.getText().toString().trim();
                password = loginPassword.getText().toString().trim();

                final int passwordLength = 5;
                String emailWarning = "Enter email address";
                String passwordWarning = "Enter password";
                String passwordTooShort = "Password must have " + passwordLength + " or more characters";
                String validateEmailFormat = "Use correct email format: name1234@gmail.com";

                // Email Edit Text field has been left empty
                if (TextUtils.isEmpty(email)) {
                    message = emailWarning;
                    showDialog();
                    //Toast.makeText(getApplicationContext(), emailWarning, Toast.LENGTH_LONG).show();
                    return;
                }

                // User email validation (matcher plus pattern)
                validateInputEmail();

                // User email has no correct format
                if (!isValidatedEmail) {
                    message = validateEmailFormat;
                    showDialog();
                    //Toast.makeText(getApplicationContext(), validateEmailFormat, Toast.LENGTH_LONG).show();
                    return;
                }

                // Password Edit Text field has been left empty
                if (TextUtils.isEmpty(password)) {
                    message = passwordWarning;
                    showDialog();
                    //Toast.makeText(getApplicationContext(), passwordWarning, Toast.LENGTH_LONG).show();
                    return;
                }

                // Password length is too short
                if (password.length() < passwordLength) {
                    message = passwordTooShort;
                    showDialog();
                    //Toast.makeText(getApplicationContext(), passwordTooShort, Toast.LENGTH_LONG).show();
                    return;
                }

                // Enable the Progress Bar
                loginProgressBar.setVisibility(View.VISIBLE);

                // Authenticate the user with associated email address as an user login
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //String userCreated = "You are logged in";
                            String authenticationFailed = "Email does not match password. Try again";

                            // Disable the Progress Bar
                            loginProgressBar.setVisibility(View.GONE);

                            if (!task.isSuccessful()) {
                                //Toast.makeText(Login.this, authenticationFailed, Toast.LENGTH_LONG).show();
                                message = authenticationFailed;
                                showDialog();
                            }
                            else {
                                // Send User email to Event List activity
                                constructSharedPreferencesEmail();

                                // Prepare the intent and send the username to Profile Page activity
                                Intent loginIntent = new Intent (getApplicationContext(), ProfilePage.class);
                                startActivity(loginIntent);
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
        loginProgressBar.setVisibility(View.GONE);
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
    // ******************** Create Shared Preferences **********************************************
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
    // ******************** Create Shared Preferences **********************************************
    // ******************** Send the email to Event List activity **********************************
    // *********************************************************************************************
    public void constructSharedPreferencesEmail() {

        String key2 = "email";

        // Create object of Shared Preferences
        SharedPreferences sharedPreferencesEmail = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Editor
        SharedPreferences.Editor emailEditor = sharedPreferencesEmail.edit();
        // Assign the string value to the email
        emailEditor.putString(key2, email);
        // Commit the Edit
        emailEditor.apply();
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
                    //Toast.makeText(Login.this, wifiConnected, Toast.LENGTH_LONG).show();
                }
            }
            // Checking for Mobile Data network connection
            if (networkInfo.getTypeName().equalsIgnoreCase("Mobile")) {
                if (networkInfo.isConnected()) {
                    Log.i(MY_TAG, mobileDataConnected);
                    //Toast.makeText(Login.this, mobileDataConnected, Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            Log.i(MY_TAG, noNetworkConnection);
            switchWiFiOn();
            //Toast.makeText(Login.this, noNetworkConnection, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Display Network connection custom Dialog *******************************
    // *********************************************************************************************
    public void switchWiFiOn() {

        // Create a new instance of Dialog builder
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this, R.style.CustomDialog);

        // Get the Layout Inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Build the Dialog Box using a builder
        dialogBuilder.setCancelable(false)
                //.setMessage(alertMessage)
                .setView(inflater.inflate(R.layout.custom_dialog_wifi_connection, null))
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // Get the reference to positive and negative buttons
        Button positiveButton =  alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        // Set colour of Dialog Buttons
        positiveButton.setTextColor(Color.DKGRAY);
        negativeButton.setTextColor(Color.DKGRAY);
    }
}
