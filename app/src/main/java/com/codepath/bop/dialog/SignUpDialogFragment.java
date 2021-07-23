package com.codepath.bop.dialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpDialogFragment extends DialogFragment {

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

    public SignUpDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SignUpDialogFragment newInstance(String title) {
        SignUpDialogFragment frag = new SignUpDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        return inflater.inflate(R.layout.fragment_sign_up, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etFullNameSignUp = view.findViewById(R.id.etFullNameSignUp);
        etUsernameSignUp = view.findViewById(R.id.etUsernameSignUp);
        etPasswordSignUp = view.findViewById(R.id.etPasswordSignUp);
        etConfirmPasswordSignUp = view.findViewById(R.id.etConfirmPasswordSignUp);
        btnSignUpModal = view.findViewById(R.id.btnSignUpModal);
        btnCancelSignUp = view.findViewById(R.id.btnCancelSignUp);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etFullNameSignUp.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnCancelSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
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
                    Toast.makeText(getContext(), getString(R.string.mismatch_passwords), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveNewUser() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "Need Access to Location", Toast.LENGTH_SHORT).show();
            //requesting permission to get users location
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else{
            //getting the user's last known location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //check if location is null
            if (location != null){
                ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                Toast.makeText(getContext(), "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                // Create the ParseUser
                ParseUser user = new ParseUser();
                // Set core properties
                user.setUsername(etUsernameSignUp.getText().toString());
                user.setPassword(etPasswordSignUp.getText().toString());
                //set additional properties
                user.put("fullName", etFullNameSignUp.getText().toString());
                user.put("location", userLocation);
                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        //if error when creating new account, inform user
                        if (e != null) {
                            Log.e(TAG, "issue with sign up", e);
                            Toast.makeText(getContext(), getString(R.string.invalid_sign_up), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            //if new account created, call gotoMainActivity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            //finish intent so that going to previous screen after logging in closes
                            // the app instead of going back to log in screen
                            getActivity().finish();
                            Toast.makeText(getContext(), getString(R.string.signed_up), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                //notify me and the user if location is null
                Log.i(TAG, "location is null");
                Toast.makeText(getContext(), "location is null", Toast.LENGTH_SHORT).show();
                //go back to the login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
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
