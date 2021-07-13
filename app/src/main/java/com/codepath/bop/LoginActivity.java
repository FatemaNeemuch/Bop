package com.codepath.bop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    //class constants
    public static final String TAG = "LoginActivity";

    //instance variables
    private EditText etUsername;
    private EditText etPassword;
    private EditText etFullName;
    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //if user is already logged in, stay logged in (persistence)
        if(ParseUser.getCurrentUser() != null){
            goToMainActivity();
        }

        //reference to views
        etUsername = findViewById(R.id.etUsernameWrite);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullNameWrite);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

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
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the ParseUser
                ParseUser user = new ParseUser();
                // Set core properties
                user.setUsername(etUsername.getText().toString());
                user.setPassword(etPassword.getText().toString());
                user.put("fullName", etFullName.getText().toString());
                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        //if error when creating new account, inform user
                        if (e != null) {
                            Log.e(TAG, "issue with sign up", e);
                            return;
                        } else {
                            //if new account created, call gotoMainActivity
                            goToMainActivity();
                            Toast.makeText(LoginActivity.this, getString(R.string.signed_up), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        //login in background so it doesn't interfere with other processes
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //if username or password are incorrect or any other error, inform user
                if (e != null){
                    Log.e(TAG, "issue with login", e);
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                //if valid username and password, call gotoMainActivity
                goToMainActivity();
                Toast.makeText(LoginActivity.this, getString(R.string.logged_in), Toast.LENGTH_SHORT).show();
            }
        });
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
