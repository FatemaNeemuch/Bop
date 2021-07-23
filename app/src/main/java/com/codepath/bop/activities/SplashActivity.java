package com.codepath.bop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.bop.managers.SpotifyDataManager;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SplashActivity extends AppCompatActivity {

    //class constants
    public static final String TAG = "SplashActivity";
    private static final String CLIENT_ID = "8d28149b161f40d1b429b265bcf79e4b";
    private static final String REDIRECT_URI = "com.codepath.bop://callback";
    private static final int REQUEST_CODE = 873;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private,streaming,playlist-read-private,user-top-read";

    //instance variables
    private static String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticateSpotify();
    }

    public void gotoLoginActivity(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        // Start home activity
        startActivity(intent);
        // close splash activity
        finish();
    }

    private void authenticateSpotify() {
        Log.i(TAG, "authenticate Spotify");
        //build request with correct scopes
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        Log.i(TAG, "open Login Activity");
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.i(TAG, "onActivity Result");
        Log.i(TAG, "requestCode: " + requestCode);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.i(TAG, "got token");
                    //need token for any call
                    mAccessToken = response.getAccessToken();
                    Log.i(TAG, "called get User Profile");
                    //get User profile information
                    SpotifyDataManager.getUserProfile("https://api.spotify.com/v1/me", mAccessToken);
                    gotoLoginActivity();
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

    public static String getmAccessToken() {
        return mAccessToken;
    }

}

