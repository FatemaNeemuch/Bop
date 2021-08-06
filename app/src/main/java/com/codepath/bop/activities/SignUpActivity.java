package com.codepath.bop.activities;

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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.codepath.bop.R;
import com.codepath.bop.dialog.ConfirmSpotifyDialogFragment;
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
    private static String fullName;
    private static String username;
    private static String password;
    private String confirmPassword;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //set action bar title
        setTitle("Sign Up");

        // Get field from view
        etFullNameSignUp = findViewById(R.id.etFullNameSignUp);
        etUsernameSignUp = findViewById(R.id.etUsernameSignUp);
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp);
        etConfirmPasswordSignUp = findViewById(R.id.etConfirmPasswordSignUp);
        btnSignUpModal = findViewById(R.id.btnSignUpModal);
        btnCancelSignUp = findViewById(R.id.btnCancelSignUp);

        //cancel button to go back to login activity
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
                fullName = etFullNameSignUp.getText().toString();
                username = etUsernameSignUp.getText().toString();
                password = etPasswordSignUp.getText().toString();
                confirmPassword = etConfirmPasswordSignUp.getText().toString();
                //check all fields have been entered properly
                if (samePassword() && notEmpty()){
                    //get user location
                    askForLocation();
                }else{
                    //clear password boxes if they don't match
                    etPasswordSignUp.setText("");
                    etConfirmPasswordSignUp.setText("");
                    Toast.makeText(SignUpActivity.this, getString(R.string.mismatch_passwords), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void askForLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Need Access to Location", Toast.LENGTH_SHORT).show();
            //requesting permission to get users location
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else{
            //getting the user's last known location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //check if location is null
            if (location != null){
                //ask user if the currently logged in spotify account is correct for them
                showConfirmSpotifyDialog();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_LOCATION:
                askForLocation();
                break;
        }
    }

    private void showConfirmSpotifyDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ConfirmSpotifyDialogFragment confirmSpotifyDialogFragment = ConfirmSpotifyDialogFragment.newInstance("Confirm Spotify Account");
        confirmSpotifyDialogFragment.show(fm, "fragment_confirm_account");
    }

    public static String getUsername(){
        return username;
    }

    public static String getPassword(){
        return password;
    }

    public static String getFullName(){
        return fullName;
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