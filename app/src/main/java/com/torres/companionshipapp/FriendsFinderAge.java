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

import java.util.*;

/**
 * Name: FriendsFinderAge <br>
 * This class allows a user to select the specific age to find friends.
 * Customized Spinner is used to look for friends.
 * Selected Friend from the list view is added to Firebase DB (at root: friends).
 * @author B00073668 Artur Sukiennik
 * @version 2, date: 11.03.2017
 */
public class FriendsFinderAge extends AppCompatActivity {

    // Declare global variables and objects
    String spinnerValue;
    Spinner spinner;
    String message;
    String hobby;
    String userId;
    String userEmailFromSession;
    String databaseUser;
    String databaseEmail;
    String age;
    String selectedValue;
    String clearedUser;
    String clearedAge;
    String clearedEmail;
    String clearedHobby;
    String tokenUser;
    String tokenAge;
    String tokenEmail;
    String tokenHobby;
    Button findFriendsButton;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView friendsListView;
    List<String> friendsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_finder_age);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Get reference of the objects from the friends_finder_age.xml file
        findFriendsButton = (Button)findViewById(R.id.button_find_friends_age);
        spinner = (Spinner) findViewById(R.id.age_spinner);
        friendsListView = (ListView) findViewById(R.id.list_view_friends_finder_age);

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
    // ******************** Set up custom Spinner **************************************************
    // ******************** Edit Spinner text size and colors **************************************
    // *********************************************************************************************
    public void setUpSpinner() {

        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this, R.array.age_array, R.layout.spinner_edit);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    // *********************************************************************************************
    // ******************** Get the User Details from Database *************************************
    // *********************************************************************************************
    public void getUserDetailsFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/users");

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
                boolean isAgeFound = false;

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
                    // Get User age from Firebase database
                    age = myDatabase.child("age").getValue(String.class);

                    // Display users' details with matching hobby
                    if (spinnerValue.equals(age)) {
                    //if (startAgeFromSpinner == userAge && userAge <= ageRangeFinal) {
                        // Avoid adding currently logged in user to list of friends
                        if (userEmailFromSession.equals(databaseEmail)) {
                            // Nothing to do here
                            // Example: Anna is a currently logged in user and we do not want to
                            // add Anna to her list of friends
                        }
                        else {
                            String friend = "Click to add to your Friends:";
                            // Add Friends details to Array List
                            friendsArrayList.add(friend + " \n" + databaseUser + " \n" + age + " \n" + databaseEmail + " \n" + hobby);
                        }
                        isAgeFound = true;
                    }
                }
                createListView();

                if(isAgeFound) {
                    isAgeFound = false;
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
        message = "There are no friends at age " + spinnerValue + " found";

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
                selectedValue = (String) parent.getItemAtPosition(position);

                tokenizeSelectedValueFromListView(selectedValue);
                addFriendToFriendListFirebaseDb();
                redirectToFriendsListIntent();
            }
        });
    }

    // *********************************************************************************************
    // ******************** Split the Selected value from the List View ****************************
    // *********************************************************************************************
    public void tokenizeSelectedValueFromListView(String value) {

        String [] tokenStrings = value.split(" ");

        for (int i = 0; i < tokenStrings.length; i++) {

            // Assign the values from the array to user detail fields
            tokenUser = tokenStrings[6];
            tokenAge = tokenStrings[7];
            tokenEmail = tokenStrings[8];
            tokenHobby = tokenStrings[9];

            removeSpacesFromTokenizedStrings();

            databaseUser = clearedUser;
            age = clearedAge;
            databaseEmail = clearedEmail;
            hobby = clearedHobby;
        }
    }

    // *********************************************************************************************
    // ******************** Remove "\n" characters from the Strings ********************************
    // *********************************************************************************************
    public void removeSpacesFromTokenizedStrings() {

        clearedUser = tokenUser.replace("\n", "");
        clearedAge = tokenAge.replace("\n", "");
        clearedEmail = tokenEmail.replace("\n", "");
        clearedHobby = tokenHobby.replace("\n", "");
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
    // ******************** Save Friend with details to Database ***********************************
    // *********************************************************************************************
    public void addFriendToFriendListFirebaseDb() {

        // Values used for the Hash Map (to save to Firebase database)
        String key1 = "username";
        String key2 = "age";
        String key3 = "email";
        String key4 = "hobby";

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/");

        // Create a Hash Map of Friends' details
        java.util.Map<String, String> friendsDetailsHashMap = new HashMap<>();

        // Get the Firebase User instance
        firebaseUser = firebaseAuthentication.getCurrentUser();

        // Retrieve Firebase User Id for currently logged in user
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        }
        else {
            // More likely it will not happen
            Toast.makeText(FriendsFinderAge.this, "Empty User Value!", Toast.LENGTH_LONG).show();
        }

        // Put User's details to the Hash Map
        friendsDetailsHashMap.put(key1, databaseUser);
        friendsDetailsHashMap.put(key2, age);
        friendsDetailsHashMap.put(key3, databaseEmail);
        friendsDetailsHashMap.put(key4, hobby);

        // Save User's details in the Firebase database (at root "friends")
        databaseReference.child("friends").child(userId).push().setValue(friendsDetailsHashMap);
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
}
