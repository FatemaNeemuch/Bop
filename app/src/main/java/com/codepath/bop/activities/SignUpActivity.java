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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.bop.R;
import com.codepath.bop.dialog.ConfirmSpotifyDialogFragment;
import com.codepath.bop.dialog.CreateNewPlaylistDialogFragment;
import com.codepath.bop.managers.SpotifyDataManager;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    //class constants
    public static final String TAG = "SignUp Dialog Fragment";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
                fullName = etFullNameSignUp.getText().toString();
                username = etUsernameSignUp.getText().toString();
                password = etPasswordSignUp.getText().toString();
                confirmPassword = etConfirmPasswordSignUp.getText().toString();
                if (samePassword() && notEmpty()){
                    showConfirmSpotifyDialog();
                }else{
                    etPasswordSignUp.setText("");
                    etConfirmPasswordSignUp.setText("");
                    Toast.makeText(SignUpActivity.this, getString(R.string.mismatch_passwords), Toast.LENGTH_SHORT).show();
                }
            }
        });
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