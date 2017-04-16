package com.torres.companionshipapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import static android.location.LocationManager.NETWORK_PROVIDER;

public class MapPlaces extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap googleMap;
    LatLng positionCoordinates;
    LocationManager locationManager;
    Location myLocation;
    Marker marker;
    Geocoder geocoder;
    final String EVENT_ON_MAP = "eventOnMap";
    String eventOnMapToFind;
    String message;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_google_map);

        // Initialize Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        // Call the supportive methods
        retrieveIntent();
        getCurrentCoordinates();
    }

    // *********************************************************************************************
    // ******************** Get intent with the Event Name and Date ********************************
    // *********************************************************************************************
    public void retrieveIntent() {

        // Retrieve the Event Name and Date from EventSelectorDate activity
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventOnMapToFind = extras.getString(EVENT_ON_MAP);
            //Toast.makeText(getApplicationContext(), eventOnMapToFind, Toast.LENGTH_LONG).show();
        }
    }

    // *********************************************************************************************
    // ******************** Get current position coordinates ***************************************
    // *********************************************************************************************
    public void getCurrentCoordinates() {

        // Check for Network permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //myLocation = LocationServices.FusedLocationApi.getLastLocation(googleClient);
        myLocation = locationManager.getLastKnownLocation(NETWORK_PROVIDER);

        // Get the current position coordinates
        latitude = myLocation.getLatitude();
        longitude = myLocation.getLongitude();
    }

    // *********************************************************************************************
    // ******************** Add marker and set up camera when the map is ready *********************
    // *********************************************************************************************
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker to indicated the current location on the map
        positionCoordinates = new LatLng(latitude, longitude);

        // Create a Marker
        marker = googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon))
                .position(positionCoordinates)
                .title(getAddressFromCurrentLocation(positionCoordinates))
                .snippet("You are Here Now"));

        // Set camera to follow the current location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(positionCoordinates));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        this.marker = marker;
        marker.showInfoWindow();
        return true;
    }

    // *********************************************************************************************
    // ******************** Get address of the current location from Geocoder **********************
    // *********************************************************************************************
    private String getAddressFromCurrentLocation(LatLng currentLocation) {
        // New instance of Geocoder
        geocoder = new Geocoder(MapPlaces.this);

        String address = "Default Address";

        try {
            address = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1).get(0).getAddressLine(0);
        }
        catch (IOException exception) {}

        return address;
    }
}
