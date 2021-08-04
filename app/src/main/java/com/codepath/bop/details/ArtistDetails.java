package com.codepath.bop.details;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.Music;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.adapters.MusicAdapter;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Album;
import com.codepath.bop.models.Artist;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ArtistDetails extends AppCompatActivity {

    //class constants
    public static final String TAG = "Artists Details";

    //instance variables
    private RecyclerView rvAlbums;
    private List<? extends Music> albums;
    private MusicAdapter musicAdapter;
    private ImageButton ibBackArtD;
    private TextView tvArtistNameArtD;
    private Artist artist;
    private static String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        //unwrap the artist passed by the intent, using the simple name as key
        artist = getIntent().getExtras().getParcelable(Artist.class.getSimpleName());

        //set action bar title
        setTitle(artist.getArtistName());

        //reference to views
        rvAlbums = findViewById(R.id.rvAlbums);
        ibBackArtD = findViewById(R.id.ibBackArtD);
        tvArtistNameArtD = findViewById(R.id.tvArtistNameArtD);

        //instantiate list and adapter
        albums = new ArrayList<>();
        musicAdapter = new MusicAdapter(albums, this);

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManagerAlbums = new LinearLayoutManager(this);
        rvAlbums.setLayoutManager(linearLayoutManagerAlbums);
        rvAlbums.setAdapter(musicAdapter);

        //set album name
        tvArtistNameArtD.setText(artist.getArtistName());

        //go back to the profile fragment
        ibBackArtD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get access token
        mAccessToken = MainActivity.getmAccessToken();

        //get artist's albums from SpotifyDataManager
        SpotifyDataManager.getArtistAlbums("https://api.spotify.com/v1/artists/" + artist.getArtistID() + "/albums", (List<Album>) albums, musicAdapter, mAccessToken);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.PDLogout){
            onStop();
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            //go back to login page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}