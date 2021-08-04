package com.codepath.bop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.codepath.bop.R;
import com.codepath.bop.dialog.ConfirmSpotifyDialogFragment;

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
                    //ask user if the currently logged in spotify account is correct for them
                    showConfirmSpotifyDialog();
                }else{
                    //clear password boxes if they don't match
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