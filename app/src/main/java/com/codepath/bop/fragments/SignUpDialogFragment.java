package com.codepath.bop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpDialogFragment extends DialogFragment {

    //class constants
    public static final String TAG = "SignUp Dialog Fragment";
    //instance variables
    private EditText etFullNameSignUp;
    private EditText etUsernameSignUp;
    private EditText etPasswordSignUp;
    private EditText etConfirmPasswordSignUp;
    private Button btnSignUpModal;
    private ImageButton btnCancelSignUp;

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
                Log.i(TAG, "password: " + etPasswordSignUp.getText().toString());
                Log.i(TAG, "password: " + etConfirmPasswordSignUp.getText().toString());
                if (etPasswordSignUp.getText().toString().equals(etConfirmPasswordSignUp.getText().toString())){
                    // Create the ParseUser
                    ParseUser user = new ParseUser();
                    // Set core properties
                    user.setUsername(etUsernameSignUp.getText().toString());
                    user.setPassword(etPasswordSignUp.getText().toString());
                    user.put(getString(R.string.fullNameParse), etFullNameSignUp.getText().toString());
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
                    etPasswordSignUp.setText("");
                    etConfirmPasswordSignUp.setText("");
                    Toast.makeText(getContext(), getString(R.string.mismatch_passwords), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
