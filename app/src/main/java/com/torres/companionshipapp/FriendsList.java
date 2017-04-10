package com.torres.companionshipapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsList extends AppCompatActivity {

    // Declare global variables and objects
    String userId;
    String databaseUser;
    String databaseEmail;
    String age;
    String hobby;
    String message;
    FirebaseAuth firebaseAuthentication;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView friendsListView;
    List<String> friendsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        // Get the Firebase authentication instance
        firebaseAuthentication = FirebaseAuth.getInstance();

        // Get reference of the objects from the friends_list.xml file
        friendsListView = (ListView) findViewById(R.id.friends_list_view);

        // Create an Array List of Friends details
        friendsArrayList = new ArrayList<>();

        // Call the supportive methods
        setUpActionBar();
        getFriendsDetailsFromFirebaseDatabase();
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
    // ******************** Get the User Details from Database *************************************
    // *********************************************************************************************
    public void getFriendsDetailsFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Assign Firebase User Id for the user
            userId = firebaseUser.getUid();
        }

        // Get only queries that relate to currently logged in user
        Query friendsDetails = databaseReference.child("friends").child(userId);

        friendsDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Iterate through the friends JSON tree (for current user)
                for (DataSnapshot myDatabase : dataSnapshot.getChildren()) {
                    databaseUser = myDatabase.child("username").getValue(String.class);
                    databaseEmail = myDatabase.child("email").getValue(String.class);
                    age = myDatabase.child("age").getValue(String.class);
                    hobby = myDatabase.child("hobby").getValue(String.class);

                    String friend = "Click to send email to:";
                    friendsArrayList.add(friend + " \n" + databaseUser + " \n" + age + " \n" + databaseEmail + " \n" + hobby);
                    //Toast.makeText(FriendsList.this, databaseUser + "\n" + databaseEmail + "\n" + age + "\n" + hobby, Toast.LENGTH_LONG).show();
                }
                createListView();

                if (friendsArrayList.isEmpty()) {
                    showDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }
    /*
    public void getFriendsDetailsFromFirebaseDatabase() {

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the Firebase User instance
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Assign Firebase User Id for the user
            userId = firebaseUser.getUid();
        }

        Query friendsDetails = databaseReference.child("friends").child(userId);

        friendsDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    databaseUser = dataSnapshot.child("username").getValue(String.class);
                    databaseEmail = dataSnapshot.child("email").getValue(String.class);
                    age = dataSnapshot.child("age").getValue(String.class);
                    hobby = dataSnapshot.child("hobby").getValue(String.class);

                    Toast.makeText(FriendsList.this, databaseUser + "\n" + databaseEmail + "\n" + age + "\n" + hobby, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    */
    /*
    public void getFriendsDetailsFromFirebaseDatabase() {

        boolean isUserId = false;

        // Get the Database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://companionship-app.firebaseio.com/friends");

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

                // Clear the content of the Array List before displaying a new set of rows
                friendsArrayList.clear();

                // Iterate through the database
                for (DataSnapshot myDatabase : dataSnapshot.getChildren()) {
                    for (DataSnapshot subchild : myDatabase.getChildren()) {
                        // Get User email from Firebase database
                        databaseEmail = subchild.child("email").getValue(String.class);
                        // Get User name from Firebase database
                        databaseUser = subchild.child("username").getValue(String.class);
                        // Get User hobby from Firebase database
                        hobby = subchild.child("hobby").getValue(String.class);
                        // Get User age from Firebase database
                        age = subchild.child("age").getValue(String.class);

                        //Toast.makeText(FriendsList.this, databaseUser + "\n" + databaseEmail + "\n" + age + "\n" + hobby, Toast.LENGTH_LONG).show();

                        // Avoid adding currently logged in user to list of friends
                        if (userEmailFromSession.equals(databaseEmail)) {
                            // Nothing to do here
                            // Example: Anna is a currently logged in user and we do not want to
                            // add Anna to her list of friends
                        }
                        else {
                            String friend = "Click to send email to:";
                            // Add Friends details to Array List
                            friendsArrayList.add(friend + " \n" + databaseUser + " \n" + age + " \n" + databaseEmail + " \n" + hobby);
                        }
                    }
                }
                createListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nothing to do here
            }
        });
    }
    */
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
        message = "You do not have any friends added yet";

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
}
