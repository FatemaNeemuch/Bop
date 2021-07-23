package com.codepath.bop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.bop.R;
import com.codepath.bop.dialog.SignUpDialogFragment;
import com.codepath.bop.managers.SpotifyDataManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class LoginActivity extends AppCompatActivity {

    //class constants
    public static final String TAG = "LoginActivity";
    private static final int REQUEST_LOCATION = 1;
    private static final String CLIENT_ID = "8d28149b161f40d1b429b265bcf79e4b";
    private static final String REDIRECT_URI = "com.codepath.bop://callback";
    private static final int REQUEST_CODE = 873;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private,streaming,playlist-read-private,user-top-read";


    //instance variables
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private LocationManager locationManager;
    private static String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //if user is already logged in, stay logged in (persistence)
        if(ParseUser.getCurrentUser() != null){
            saveCurrentUserLocation();
            goToMainActivity();
        }

        //reference to views
        etUsername = findViewById(R.id.etUsernameWrite);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        //login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                //method checks if valid username and password entered
                loginUser(username, password);
            }
        });

        //sign up button
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    private void showSignUpDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SignUpDialogFragment signUpDialogFragment = SignUpDialogFragment.newInstance("Sign Up Modal Overlay");
        signUpDialogFragment.show(fm, "fragment_sign_up");
    }

    private void loginUser(String username, String password) {
        //login in background so it doesn't interfere with other processes
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //if username or password are incorrect or any other error, inform user
                if (e != null){
                    Log.e(TAG, "issue with login", e);
                    etUsername.setText("");
                    etPassword.setText("");
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "done user: " + user.getUsername() + ", currentUser: " + ParseUser.getCurrentUser().getUsername());
                //save the user location to Parse
                boolean saved = saveCurrentUserLocation();
                //only go to main activity if location successfully saved
                if (saved){
                    //if valid username and password, call gotoMainActivity
                    goToMainActivity();
                    Toast.makeText(LoginActivity.this, getString(R.string.logged_in), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean saveCurrentUserLocation() {
        // requesting permission to get user's location
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Need Access to Location", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else {
            // getting last know user's location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // checking if the location is null
            if(location != null){
                // if it isn't, save it to Back4App Dashboard
                ParseGeoPoint currentUserLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                Toast.makeText(this, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser != null) {
                    currentUser.put("location", currentUserLocation);
                    currentUser.saveInBackground();
                    return true;
                } else {
                    Log.i(TAG, "current user is null");
                }
            }
            else {
                //notify me and the user if location is null
                Log.i(TAG, "location is null");
                Toast.makeText(this, "location is null", Toast.LENGTH_SHORT).show();
            }
        }
        //return false if location not saved for any reason
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_LOCATION:
                saveCurrentUserLocation();
                break;
        }
    }

    public static ParseGeoPoint getCurrentUserLocation(){

        // finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            Log.i(TAG, "current user is null");
        }
        // otherwise, return the current user location
        return currentUser.getParseGeoPoint("location");

    }

    //method that makes an intent to go to MainActivity
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //finish intent so that going to previous screen after logging in closes
        // the app instead of going back to log in screen
        finish();
    }
}
