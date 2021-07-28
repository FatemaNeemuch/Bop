package com.codepath.bop.Details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.adapters.SongAdapter;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Playlist;
import com.codepath.bop.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetails extends AppCompatActivity {

    //class constants
    public static final String TAG = "Playlist Details";

    //instance variables
    private RecyclerView rvPlaylistSongs;
    private List<Song> playlistSongs;
    private SongAdapter adapter;
    private ImageButton ibBackPD;
    private TextView tvPlaylistNameDetails;
    private ImageButton ibPlayButtonPD;
    private Playlist playlist;
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean playing;
    private static String mAccessToken;
    public static List<Song> songsListForCurrentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_details);

        //references to the views
        rvPlaylistSongs = findViewById(R.id.rvPlaylistSongs);
        ibBackPD = findViewById(R.id.ibBackPD);
        tvPlaylistNameDetails = findViewById(R.id.tvPlaylistNameDetails);
        ibPlayButtonPD = findViewById(R.id.ibPlayButtonPD);

        boolean premium = SpotifyDataManager.getProduct().equals("premium");

        playlistSongs = new ArrayList<>();
        adapter = new SongAdapter(playlistSongs, this, premium);

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPlaylistSongs.setLayoutManager(linearLayoutManager);
        rvPlaylistSongs.setAdapter(adapter);

        //unwrap the playlist passed by the intent, using the simple name as key
//        playlist = getIntent().getParcelableExtra(Playlist.class.getSimpleName());
        playlist = getIntent().getExtras().getParcelable(Playlist.class.getSimpleName());

        //set playlist name
        tvPlaylistNameDetails.setText(playlist.getName());

        songsListForCurrentSong = new ArrayList<>();

        //go back to the profile fragment
        ibBackPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
        });

        // set play button
        Glide.with(this).load(R.drawable.ic_baseline_play_arrow_24).circleCrop().into(ibPlayButtonPD);

        playing = false;

        //play the playlist
        ibPlayButtonPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause song if button is clicked when song is playing
                if (playing){
                    mSpotifyAppRemote.getPlayerApi().pause();
                    //change icon back to play button
                    Glide.with(PlaylistDetails.this).load(R.drawable.ic_baseline_play_arrow_24).circleCrop().into(ibPlayButtonPD);
                    //update variable
                    playing = false;
                }else{
                    //update variable
                    playing = true;
                    Log.i(TAG, playlist.getName());
                    //play song from spotify
                    mSpotifyAppRemote = MainActivity.getmSpotifyAppRemote();
                    mSpotifyAppRemote.getPlayerApi().play(playlist.getPlaylistURI());
                    mSpotifyAppRemote.getPlayerApi().setShuffle(true);
                    // Subscribe to PlayerState
                    mSpotifyAppRemote.getPlayerApi()
                            .subscribeToPlayerState()
                            .setEventCallback(playerState -> {
                                final Track track = playerState.track;
                                if (track != null) {
                                    Log.i(TAG, track.name + " by " + track.artist.name);
                                }
                                String artistName = track.artists.get(0).name;;
                                if (track.artists.size() > 1){
                                    for (int i = 1; i < track.artists.size(); i++){
                                        artistName = artistName + ", " + track.artists.get(i).name;
                                    }
                                }
                                boolean songSaved = false;
                                Log.i(TAG, "songsListForCurrentSong size: " + songsListForCurrentSong.size());
                                for (int i = 0; i < songsListForCurrentSong.size(); i++){
                                    Song songFromPlaylist = songsListForCurrentSong.get(i);
                                    if (track.name.equals(songFromPlaylist.getTitle())){
                                        songFromPlaylist.setCurrentSong(songFromPlaylist);
                                        songSaved = true;
                                    }
                                }
                                if (!songSaved){
                                    String spotifyLogoURL = "https://www.macworld.com/wp-content/uploads/2021/03/spotify-logo-100779042-orig-3.jpg?quality=50&strip=all&w=1024";
                                    Song song = new Song(track.uri, track.name, track.album.name, artistName, "1/1/2011", spotifyLogoURL, "album", true);
                                    //save this song as the current song
                                    song.setCurrentSong(song);
                                }
                            });
                    //change icon to pause button
                    Glide.with(PlaylistDetails.this).load(R.drawable.ic_baseline_pause_24).circleCrop().into(ibPlayButtonPD);
                }
            }
        });

        //get access token
        mAccessToken = MainActivity.getmAccessToken();

        //get playlist songs from SpotifyDataManager
        SpotifyDataManager.getTracks("https://api.spotify.com/v1/playlists/" + playlist.getPlaylistID() + "/tracks", playlistSongs, adapter, mAccessToken, true);
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
//        else if (item.getItemId() == R.id.addSong){
//            SpotifyDataManager.addSong("https://api.spotify.com/v1/playlists/" + playlist.getPlaylistID() + "/tracks",
//                    MainActivity.getmAccessToken(), new Song());
//        }
        return super.onOptionsItemSelected(item);
    }
}