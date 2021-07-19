package com.codepath.bop.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.codepath.bop.R;
import com.codepath.bop.fragments.NearbyUsersFragment;
import com.codepath.bop.fragments.PlaylistFragment;
import com.codepath.bop.fragments.ProfileFragment;
import com.codepath.bop.fragments.BrowseFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class MainActivity extends AppCompatActivity {

    //class constants
    public static final String TAG = "Main Activity";
    private static final String CLIENT_ID = "8d28149b161f40d1b429b265bcf79e4b";
    private static final String REDIRECT_URI = "com.codepath.bop://callback";
    private static final int REQUEST_CODE = 873;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private, streaming";

    //instance variables
    private static String mAccessToken;
    private BottomNavigationView bottomNavigationView;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    // initializing FusedLocationProviderClient object
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //reference to views
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        authenticateSpotify();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.i(TAG, "calling getLastLocation from OnCreate");
        // method to get the location
        getLastLocation();
    }

    private void authenticateSpotify() {
        Log.i(TAG, "successfully authenticated");
        //build request with correct scopes
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.i(TAG, "onActivityResult");

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    //need token for any call
                    mAccessToken = response.getAccessToken();
                    //go to correct view that was clicked on
                    bottomNavigationView();
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.i(TAG, "error when getting response");
                    break;

                case EMPTY:
                    Log.i(TAG, "empty");
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.i(TAG, "auth flow was cancelled " + response);
                    // Handle other cases
            }
        }
    }

    public void bottomNavigationView(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.bnSearch:
                        if(fragmentManager.findFragmentByTag("browse") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("browse")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.flContainer, new BrowseFragment(), "browse").commit();
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("playlist") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("playlist")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("nearbyUsers") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("nearbyUsers")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("profile") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("profile")).commit();
                        }
                        break;

                    case R.id.bnPlaylists:

                        if(fragmentManager.findFragmentByTag("playlist") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("playlist")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.flContainer, new PlaylistFragment(), "playlist").commit();
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("browse") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("browse")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("nearbyUsers") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("nearbyUsers")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("profile") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("profile")).commit();
                        }
                        break;

                    case R.id.bnNearbyUsers:
                        if(fragmentManager.findFragmentByTag("nearbyUsers") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("nearbyUsers")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.flContainer, new NearbyUsersFragment(), "nearbyUsers").commit();
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("browse") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("browse")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("playlist") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("playlist")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("profile") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("profile")).commit();
                        }
                        break;

                    case R.id.bnProfile:
                    default:
                        if(fragmentManager.findFragmentByTag("profile") != null) {
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("profile")).commit();
                        } else {
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction().add(R.id.flContainer, new ProfileFragment(), "profile").commit();
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("browse") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("browse")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("playlist") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("playlist")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("nearbyUsers") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("nearbyUsers")).commit();
                        }
                        break;
                }

                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.bnSearch);
    }

    public static String getmAccessToken() {
        return mAccessToken;
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last location from FusedLocationClient object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Toast.makeText(MainActivity.this, "latitude: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available, request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Toast.makeText(MainActivity.this, "Latitude: " + mLastLocation.getLatitude() + "", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Longitude: " + mLastLocation.getLongitude() + "", Toast.LENGTH_SHORT).show();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location on Android 10.0 and higher, use:
        //ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_ID);
    }

    // method to check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}