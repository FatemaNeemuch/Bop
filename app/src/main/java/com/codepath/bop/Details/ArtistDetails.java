package com.codepath.bop.Details;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codepath.bop.R;

public class ArtistDetails extends AppCompatActivity {

    //class constants
    public static final String TAG = "Artists Details";

    //instance variables


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);
    }
}