package com.codepath.bop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.codepath.bop.R;
import com.codepath.bop.fragments.NearbyUsersFragment;
import com.codepath.bop.fragments.PlaylistFragment;
import com.codepath.bop.fragments.ProfileFragment;
import com.codepath.bop.fragments.BrowseFragment;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //reference to views
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        authenticateSpotify();


    }

    private void authenticateSpotify() {
        //build request with correct scopes
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

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

                // Most likely auth flow was cancelled
                default:
                    Log.i(TAG, "auth flow was cancelled");
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
}