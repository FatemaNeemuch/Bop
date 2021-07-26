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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.activities.SignUpActivity;
import com.codepath.bop.activities.SplashActivity;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.User;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ConfirmSpotifyDialogFragment extends DialogFragment {
    //class constants
    public static final String TAG = "Confirm Spotify Dialog";
    private static final int REQUEST_LOCATION = 1;

    //instance variable
    private LocationManager locationManager;
    private TextView tvQuestion;
    private ImageView ivProfilePicCA;
    private TextView tvDisplayName;
    private Button btnYes;
    private Button btnNo;

    public ConfirmSpotifyDialogFragment(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ConfirmSpotifyDialogFragment newInstance(String title) {
        ConfirmSpotifyDialogFragment frag = new ConfirmSpotifyDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        return inflater.inflate(R.layout.fragment_confirm_account, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        tvQuestion = view.findViewById(R.id.tvQuestion);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        ivProfilePicCA = view.findViewById(R.id.ivProfilePicCA);
        btnYes = view.findViewById(R.id.btnYes);
        btnNo = view.findViewById(R.id.btnNo);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Playlist Name");
        getDialog().setTitle(title);

        //set display name
        tvDisplayName.setText(SpotifyDataManager.getDisplayName());
        //set profile picture
        if (SpotifyDataManager.getProfilePicURl().equals(".")){
            //use generic profile pic saved on parse if no Spotify profile picture
            Glide.with(getContext())
                    .load(R.drawable.generic_profile_pic)
                    .circleCrop()
                    .into(ivProfilePicCA);
        }else{
            //use Spotify profile picture if available
            Glide.with(getContext())
                    .load(SpotifyDataManager.getProfilePicURl())
                    .circleCrop()
                    .into(ivProfilePicCA);
        }
        //if correct account
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewUser();
            }
        });

        //if not correct account
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
                user.setUsername(SignUpActivity.getUsername());
                user.setPassword(SignUpActivity.getPassword());
                //set additional properties
                user.put("fullName", SignUpActivity.getFullName());
                user.put("location", userLocation);
                user.put("userURI", SpotifyDataManager.getUserURI());
                user.put("userID", SpotifyDataManager.getUserID());
                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        //if error when creating new account, inform user
                        if (e != null) {
                            Log.e(TAG, "issue with sign up", e);
                            Toast.makeText(getContext(), getString(R.string.invalid_sign_up), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //create url for posting a playlist
                        String url = "https://api.spotify.com/v1/users/" + SpotifyDataManager.getUserID() + "/playlists";
                        //make call to create default playlist at sign up
                        SpotifyDataManager.createDefaultPlaylist(url, SplashActivity.getmAccessToken(), SignUpActivity.getUsername());
                        //if new account created, call gotoMainActivity
                        gotoMainActivity();
                        Toast.makeText(getContext(), getString(R.string.signed_up), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                //notify me and the user if location is null
                Log.i(TAG, "location is null");
                Toast.makeText(getContext(), "location is null", Toast.LENGTH_SHORT).show();
                //dismiss the dialog
                dismiss();
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

    private void gotoMainActivity(){
        dismiss();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        //finish intent so that going to previous screen after signing in closes
        // the app instead of going back to sign in screen
        getActivity().finish();
    }
}
