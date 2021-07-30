package com.codepath.bop.Details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.bop.Music;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.adapters.MusicAdapter;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Album;
import com.codepath.bop.models.Playlist;
import com.codepath.bop.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetails extends AppCompatActivity {

    //class constants
    public static final String TAG = "Album Details";

    //instance variables
    private RecyclerView rvAlbumSongs;
    private List<? extends Music> albumSongs;
    private ImageButton ibBackAD;
    private TextView tvAlbumNameDetails;
    private ImageView ibPlayButtonAD;
    private Album album;
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean playing;
    private static String mAccessToken;
    public static List<Song> songsListForCurrentSong;
    private MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        //references to the views
        rvAlbumSongs = findViewById(R.id.rvAlbumSongs);
        ibBackAD = findViewById(R.id.ibBackAD);
        tvAlbumNameDetails = findViewById(R.id.tvAlbumNameDetails);
        ibPlayButtonAD = findViewById(R.id.ibPlayButtonAD);

        albumSongs = new ArrayList<>();
        musicAdapter = new MusicAdapter(albumSongs, this);

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManagerAlbums = new LinearLayoutManager(this);
        rvAlbumSongs.setLayoutManager(linearLayoutManagerAlbums);
        rvAlbumSongs.setAdapter(musicAdapter);

        //unwrap the album passed by the intent, using the simple name as key
        album = getIntent().getExtras().getParcelable(Album.class.getSimpleName());

        //set album name
        tvAlbumNameDetails.setText(album.getAlbumName());

        songsListForCurrentSong = new ArrayList<>();

        //go back to the profile fragment
        ibBackAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set play button
        Glide.with(this).load(R.drawable.ic_baseline_play_arrow_24).circleCrop().into(ibPlayButtonAD);

        playing = false;

        //play the playlist
        ibPlayButtonAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause song if button is clicked when song is playing
                if (playing){
                    mSpotifyAppRemote.getPlayerApi().pause();
                    //change icon back to play button
                    Glide.with(AlbumDetails.this).load(R.drawable.ic_baseline_play_arrow_24).circleCrop().into(ibPlayButtonAD);
                    //update variable
                    playing = false;
                }else{
                    //update variable
                    playing = true;
                    Log.i(TAG, album.getAlbumName());
                    //play song from spotify
                    mSpotifyAppRemote = MainActivity.getmSpotifyAppRemote();
                    mSpotifyAppRemote.getPlayerApi().play(album.getAlbumURI());
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
                                    Song songFromAlbum = (Song) songsListForCurrentSong.get(i);
                                    if (track.uri.equals(songFromAlbum.getSongURI()) && !songSaved){
                                        songFromAlbum.setCurrentSong(songFromAlbum);
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
                    Glide.with(AlbumDetails.this).load(R.drawable.ic_baseline_pause_24).circleCrop().into(ibPlayButtonAD);
                }
            }
        });

        //get access token
        mAccessToken = MainActivity.getmAccessToken();

        //get playlist songs from SpotifyDataManager
        SpotifyDataManager.getTracksfromAlbum("https://api.spotify.com/v1/albums/" + album.getAlbumID() + "/tracks", (List<Song>) albumSongs, musicAdapter, mAccessToken, true, album);
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