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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                final int passwordLength = 4;
                String emailWarning = "Enter email address";
                String passwordWarning = "Enter password";
                String passwordTooShort = "Password must have " + passwordLength + " or more characters";
                String validateEmailFormat = "Use correct email format: example@gmail.com";

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

        String key = "email";

        // Create object of Shared Preferences
        SharedPreferences sharedPreferencesEmail = PreferenceManager.getDefaultSharedPreferences(this);
        // Get Editor
        SharedPreferences.Editor emailEditor = sharedPreferencesEmail.edit();
        // Assign the string value to the email
        emailEditor.putString(key, email);
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
            message = noNetworkConnection;
            showDialog();
            //Toast.makeText(Login.this, noNetworkConnection, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Get the User Name from Database ****************************************
    // *********************************************************************************************
    /*public void getUserNameFromFirebaseDatabase() {

        // Get the Database reference
        //databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/users");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Assign Firebase User Id for the user
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        }

        Query userDetails = databaseReference.child("users").child(userId);

        userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Iterate through the database
                for (DataSnapshot myDatabase : dataSnapshot.getChildren()) {
                    // Get the User Firebase email
                    //String firebaseUserEmail = firebaseUser.getEmail();
                    //String loggedUser = myDatabase.child(userId).child("username").getValue(String.class);
                    //Toast.makeText(Login.this, loggedUser, Toast.LENGTH_LONG).show();

                    String loggedUser = myDatabase.child("username").getValue(String.class);
                    Toast.makeText(Login.this, loggedUser, Toast.LENGTH_LONG).show();

                    // THIS WORKS !!!
                    //Toast.makeText(Login.this, userId, Toast.LENGTH_LONG).show();
                    // THIS WORKS !!!
                    //Toast.makeText(Login.this, firebaseUserEmail, Toast.LENGTH_LONG).show();

                    //String a = myDatabase.child("username").getValue(String.class);
                    //Toast.makeText(Login.this, "Username: " + a, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }*/
}
