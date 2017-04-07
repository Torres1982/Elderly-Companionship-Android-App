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
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class FriendsFinder extends AppCompatActivity {

    // Declare global variables and objects
    String spinnerValue;
    Spinner spinner;
    String message;
    String hobby;
    String userId;
    String userEmailFromSession;
    String databaseUser;
    String databaseEmail;
    Button findFriendsButton;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView friendsListView;
    List<String> friendsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_finder);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Get reference of the objects from the friends_finder.xml file
        findFriendsButton = (Button)findViewById(R.id.button_find_friends);
        spinner = (Spinner) findViewById(R.id.hobby_spinner);
        friendsListView = (ListView) findViewById(R.id.list_view);

        // Create an Array List of Friends details
        friendsArrayList = new ArrayList<>();

        // Call the supportive methods
        setUpActionBar();
        setUpSpinner();

        // Call the methods with associated Listeners
        addListenerToFindFriendsButton();
        getClickedValueFromListView();
    }

    // *********************************************************************************************
    // ******************** Register Listener for the Find Friends Button **************************
    // ******************** Redirect ... ***********************************************************
    // *********************************************************************************************
    public void addListenerToFindFriendsButton () {
        findFriendsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

            // Get the value from the selected dropdown list
            spinnerValue = String.valueOf(spinner.getSelectedItem());

            getUserDetailsFromFirebaseDatabase();
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

        if (firebaseUser != null) {
            // Assign Firebase User Id for the user
            userId = firebaseUser.getUid();
            // Get the Firebase User email from current session
            userEmailFromSession = firebaseUser.getEmail();
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String hobby;
                boolean isHobbyFound = false;

                // Clear the content of the Array List before displaying a new set of rows
                friendsArrayList.clear();

                // Iterate through the database
                for (DataSnapshot myDatabase : dataSnapshot.getChildren()) {

                    // Get User email from Firebase database
                    databaseEmail = myDatabase.child("email").getValue(String.class);
                    // Get User name from Firebase database
                    databaseUser = myDatabase.child("username").getValue(String.class);
                    // Get User hobby from Firebase database
                    hobby = myDatabase.child("hobby").getValue(String.class);

                    // Display users' details with matching hobby
                    if (spinnerValue.equals(hobby)) {
                        // Avoid adding currently logged in user to list of friends
                        if (userEmailFromSession.equals(databaseEmail)) {
                            // Nothing to do here
                            // Example: Anna is a currently logged in user and we do not want to
                            // add Anna to her list of friends
                        }
                        else {
                            String friend = "Click to add to your Friends:";
                            // Add Friends details to Array List
                            friendsArrayList.add(friend + "\n" + databaseUser + "\n" + databaseEmail + "\n" + hobby);
                        }
                        isHobbyFound = true;
                    }
                }
                createListView();

                if(isHobbyFound) {
                    isHobbyFound = false;
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
        message = "There are no friends with " + spinnerValue + " hobby found";

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
    // ******************** Create a List View with Friends details ********************************
    // *********************************************************************************************
    public void createListView() {
        // Set Array Adapter to create a Scrollable List View
        friendsListView.setAdapter(new ArrayAdapter<>(this, R.layout.custom_simple_list_item, friendsArrayList));
    }

    // *********************************************************************************************
    // ******************** Get the Value from Selected List View **********************************
    // *********************************************************************************************
    public void getClickedValueFromListView() {

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue =(String) parent.getItemAtPosition(position);
                Toast.makeText(FriendsFinder.this, selectedValue, Toast.LENGTH_LONG).show();

                redirectToFriendsListIntent();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Redirect User to Friends List activity *********************************
    // *********************************************************************************************
    public void redirectToFriendsListIntent() {

        // Prepare the intent and send the username to Profile Page activity
        Intent friendsListIntent = new Intent (getApplicationContext(), FriendsList.class);
        startActivity(friendsListIntent);
        finish();
    }

    // *********************************************************************************************
    // ******************** Redirect User to Friends List activity *********************************
    // *********************************************************************************************
    public void saveFriendsToYourFriendsList() {

    }
}
