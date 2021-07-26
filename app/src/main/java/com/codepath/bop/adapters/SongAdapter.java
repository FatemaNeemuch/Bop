package com.codepath.bop.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.bop.R;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.fragments.BrowseFragment;
import com.codepath.bop.models.Song;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    //class constants
    public static final String TAG = "Song Adapter";

    //instance variables
    private List<Song> songs;
    private Context context;
    private boolean premium;
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean playing;

    public SongAdapter(List<Song> songs, Context context, boolean premium) {
        this.songs = songs;
        this.context = context;
        this.premium = premium;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        //get song
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //instance variables
        private TextView tvSongTitle;
        private TextView tvArtistName;
        private ImageView ivCover;
        private ImageView ivPlayButton;
        private boolean isDoubleClicked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //reference views
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivCover = itemView.findViewById(R.id.ivCover);
            ivPlayButton = itemView.findViewById(R.id.ivPlayButton);
            //initialize variable
            playing = false;
        }

        public void bind(Song song) {
            //set song title
            tvSongTitle.setText(song.getTitle());
            //set artist
            tvArtistName.setText(song.getArtist());
            //set song cover
            Glide.with(context).load(song.getCoverURL()).into(ivCover);
            if (premium){
                //set play button based on whether the song is playing
                if (song.getisCurrentSong()){
                    Glide.with(context).load(R.drawable.ic_baseline_pause_24).into(ivPlayButton);
                }else{
                    Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButton);
                }
                //play song here as well
                ivPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //pause song if button is clicked when song is playing
                        if (playing){
                            mSpotifyAppRemote.getPlayerApi().pause();
                            //change icon back to play button
                            Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButton);
                            //update current song playing
                            song.setCurrentSong(null);
                            //update variable
                            playing = false;
                        }else{
                            //update current song playing
                            song.setCurrentSong(song);
                            //play song if button is clicked when the song is not playing
                            //update variable
                            playing = true;
                            //play song from spotify
                            mSpotifyAppRemote = MainActivity.getmSpotifyAppRemote();
                            mSpotifyAppRemote.getPlayerApi().play(song.getSongURI());
                            // Subscribe to PlayerState
                            mSpotifyAppRemote.getPlayerApi()
                                    .subscribeToPlayerState()
                                    .setEventCallback(playerState -> {
                                        final Track track = playerState.track;
                                        if (track != null) {
                                            Log.i(TAG, track.name + " by " + track.artist.name);
                                        }
                                    });
                            //change icon to pause button
                            Glide.with(context).load(R.drawable.ic_baseline_pause_24).into(ivPlayButton);
                        }
                    }
                });
            }

            //Double click post to like:
            isDoubleClicked=false;

            Handler handler=new Handler();
            Runnable r=new Runnable(){
                @Override
                public void run(){
                    //Actions when Single Clicked
                    isDoubleClicked = false;
                }
            };

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDoubleClicked){
                        //actions when double clicked
                        //add song to favs playlist
                        Toast.makeText(context, "Double clicked if", Toast.LENGTH_SHORT).show();
                        //optional: delete songs from playlist
                        isDoubleClicked = false;
                        //remove callbacks for Handlers
                        handler.removeCallbacks(r);
                    }else{
                        //Toast.makeText(context, "Double clicked else", Toast.LENGTH_SHORT).show();
                        //single click I think
                        isDoubleClicked=true;
                        handler.postDelayed(r,500);
                    }
                }
            });
        }
    }
}
