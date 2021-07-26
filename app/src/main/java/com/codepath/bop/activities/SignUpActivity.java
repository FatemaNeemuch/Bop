package com.codepath.bop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.bop.R;
import com.codepath.bop.managers.SpotifyDataManager;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    //class constants
    public static final String TAG = "SignUp Dialog Fragment";
    private static final int REQUEST_LOCATION = 1;

    //instance variables
    private EditText etFullNameSignUp;
    private EditText etUsernameSignUp;
    private EditText etPasswordSignUp;
    private EditText etConfirmPasswordSignUp;
    private Button btnSignUpModal;
    private ImageButton btnCancelSignUp;
    private LocationManager locationManager;
    private String password;
    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Get field from view
        etFullNameSignUp = findViewById(R.id.etFullNameSignUp);
        etUsernameSignUp = findViewById(R.id.etUsernameSignUp);
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp);
        etConfirmPasswordSignUp = findViewById(R.id.etConfirmPasswordSignUp);
        btnSignUpModal = findViewById(R.id.btnSignUpModal);
        btnCancelSignUp = findViewById(R.id.btnCancelSignUp);

        btnCancelSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUpModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = etPasswordSignUp.getText().toString();
                confirmPassword = etConfirmPasswordSignUp.getText().toString();
                if (samePassword() && notEmpty()){
                    saveNewUser();
                }else{
                    etPasswordSignUp.setText("");
                    etConfirmPasswordSignUp.setText("");
                    Toast.makeText(SignUpActivity.this, getString(R.string.mismatch_passwords), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveNewUser() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(SignUpActivity.this, "Need Access to Location", Toast.LENGTH_SHORT).show();
            //requesting permission to get users location
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else{
            //getting the user's last known location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //check if location is null
            if (location != null){
                ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                Toast.makeText(SignUpActivity.this, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                // Create the ParseUser
                ParseUser user = new ParseUser();
                // Set core properties
                String username = etUsernameSignUp.getText().toString();
                user.setUsername(username);
                user.setPassword(etPasswordSignUp.getText().toString());
                user.setEmail(SpotifyDataManager.getEmail());
                //set additional properties
                user.put("fullName", etFullNameSignUp.getText().toString());
                user.put("location", userLocation);
                user.put("userURI", SpotifyDataManager.getUserURI());
                user.put("userID", SpotifyDataManager.getUserID());
                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        //if error when creating new account, inform user
                        if (e != null) {
                            Log.e(TAG, "issue with sign up", e);
                            Toast.makeText(SignUpActivity.this, getString(R.string.invalid_sign_up), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //create url for posting a playlist
                        String url = "https://api.spotify.com/v1/users/" + SpotifyDataManager.getUserID() + "/playlists";
                        //make call to create default playlist at sign up
                        SpotifyDataManager.createDefaultPlaylist(url, SplashActivity.getmAccessToken(), username);
                        //if new account created, call gotoMainActivity
                        gotoMainActivity();
                        Toast.makeText(SignUpActivity.this, getString(R.string.signed_up), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //notify me and the user if location is null
                Log.i(TAG, "location is null");
                Toast.makeText(this, "location is null", Toast.LENGTH_SHORT).show();
                //go back to the login activity
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private ParseGeoPoint getCurrentUserLocation(){

        // finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            Log.i(TAG, "current user is null");
        }
        // otherwise, return the current user location
        return currentUser.getParseGeoPoint("location");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_LOCATION:
                saveNewUser();
                break;
        }
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //finish intent so that going to previous screen after logging in closes
        // the app instead of going back to log in screen
        finish();
    }

    private boolean samePassword(){
        if (password.equals(confirmPassword)){
            return true;
        }
        return false;
    }

    private boolean notEmpty(){
        if (password.isEmpty() && confirmPassword.isEmpty()){
            return false;
        }
        return true;
    }
}