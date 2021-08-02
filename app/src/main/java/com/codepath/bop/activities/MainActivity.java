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
import com.codepath.bop.fragments.SearchFragment;
import com.codepath.bop.managers.SpotifyDataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
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
    private static SpotifyAppRemote mSpotifyAppRemote;
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
                            //set action bar title
                            setTitle("Browse");
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .show(fragmentManager.findFragmentByTag("browse"))
                                    .commit();
                        } else {
                            //set action bar title
                            setTitle("Browse");
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction()
                                    .add(R.id.flContainer, new BrowseFragment(), "browse")
                                    .commit();
                        }
                        if(fragmentManager.findFragmentByTag("nearbyUsers") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("nearbyUsers")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("profile") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("profile")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("search") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("search")).commit();
                        }
                        break;

                    case R.id.bnNearbyUsers:
                        if(fragmentManager.findFragmentByTag("nearbyUsers") != null) {
                            //set action bar title
                            setTitle("Nearby Users");
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .show(fragmentManager.findFragmentByTag("nearbyUsers"))
                                    .commit();
                        } else {
                            //set action bar title
                            setTitle("Nearby Users");
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction()
                                    .add(R.id.flContainer, new NearbyUsersFragment(), "nearbyUsers")
                                    .commit();
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("browse") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("browse")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("profile") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("profile")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("search") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("search")).commit();
                        }
                        break;

                    case R.id.bnProfile:
                    default:
                        if(fragmentManager.findFragmentByTag("profile") != null) {
                            //set action bar title
                            setTitle("Account");
                            //if the fragment exists, show it.
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .show(fragmentManager.findFragmentByTag("profile"))
                                    .commit();
                        } else {
                            //set action bar title
                            setTitle("Account");
                            //if the fragment does not exist, add it to fragment manager.
                            fragmentManager.beginTransaction()
                                    .add(R.id.flContainer, new ProfileFragment(), "profile")
                                    .commit();
                        }
                        //if the other fragments are visible, hide them.
                        if(fragmentManager.findFragmentByTag("browse") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("browse")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("nearbyUsers") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("nearbyUsers")).commit();
                        }
                        if(fragmentManager.findFragmentByTag("search") != null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("search")).commit();
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

    @Override
    public void onStart() {
        super.onStart();

        // Set the connection parameters - get user authorization
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        //connect to spotify
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.i(TAG, "Connected! Yay!");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public static SpotifyAppRemote getmSpotifyAppRemote(){
        return mSpotifyAppRemote;
    }

    @Override
    public void onStop() {
        Log.i(TAG, "stopping the music");
        super.onStop();
        //check if app is running in background and only pause music if its not
        //use the getActivity().isFinishing method
//        if (isDestroyed()){
//            mSpotifyAppRemote.getPlayerApi().getPlayerState()
//                    .setResultCallback(playerState -> {
//                        mSpotifyAppRemote.getPlayerApi().pause();
//                    })
//                    .setErrorCallback(throwable -> {
//                        Log.e(TAG, throwable.getMessage(), throwable);
//                    });
//        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(playerState -> {
                    mSpotifyAppRemote.getPlayerApi().pause();
                })
                .setErrorCallback(throwable -> {
                    Log.e(TAG, throwable.getMessage(), throwable);
                });
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        super.onDestroy();
    }
}