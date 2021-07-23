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
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private,streaming,playlist-read-private,user-top-read";

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

        mAccessToken = SplashActivity.getmAccessToken();
        bottomNavigationView();
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