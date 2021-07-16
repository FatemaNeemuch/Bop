package com.codepath.bop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.bop.R;
import com.codepath.bop.fragments.NearbyUsersFragment;
import com.codepath.bop.fragments.PlaylistFragment;
import com.codepath.bop.fragments.ProfileFragment;
import com.codepath.bop.fragments.SearchFragment;
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
    final FragmentManager fragmentManager = getSupportFragmentManager();


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
                Fragment fragment;
                //dictates which fragment to launch depending on which menu item clicked
                switch (item.getItemId()) {
                    case R.id.bnSearch:
                        fragment = new SearchFragment();
                        break;
                    case R.id.bnPlaylists:
                        fragment = new PlaylistFragment();
                        break;
                    case R.id.bnNearbyUsers:
                        fragment = new NearbyUsersFragment();
                        break;
                    case R.id.bnProfile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                //shows the fragment clicked
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
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