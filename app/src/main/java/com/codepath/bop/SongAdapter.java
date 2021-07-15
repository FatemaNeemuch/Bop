package com.codepath.bop;

import android.content.Context;
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
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    //class constants
    public static final String TAG = "Song Adapter";

    //instance variables
    List<Song> songs;
    Context context;
    boolean playing;
    boolean resume;
    SpotifyAppRemote mSpotifyAppRemote;

    public SongAdapter(List<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
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
        //get post
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //instance variables
        private TextView tvSongTitle;
        private TextView tvArtistName;
        private ImageView ivCover;
        private ImageView ivPlayButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivCover = itemView.findViewById(R.id.ivCover);
            ivPlayButton = itemView.findViewById(R.id.ivPlayButton);
        }

        public void bind(Song song) {
            tvSongTitle.setText(song.getTitle());
            tvArtistName.setText(song.getArtist());
            Glide.with(context).load(song.getCoverURL()).into(ivCover);
            Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButton);
            Log.i(TAG, Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButton).toString());
            playing = false;
            resume = false;
            //play song here as well
            ivPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playing){
                        mSpotifyAppRemote.getPlayerApi().pause();
                        Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButton);
                        playing = false;
                    }else{
                        playing = true;
                        resume = true;
                        mSpotifyAppRemote = MainActivity.getmSpotifyAppRemote();
//                        if (resume){
//                            mSpotifyAppRemote.getPlayerApi().resume();
//                            resume = false;
//                        }else{
//                            mSpotifyAppRemote.getPlayerApi().play(song.getSongURI());
//                        }
                        mSpotifyAppRemote.getPlayerApi().play(song.getSongURI());
//                        // Subscribe to PlayerState
//                        mSpotifyAppRemote.getPlayerApi()
//                                .subscribeToPlayerState()
//                                .setEventCallback(playerState -> {
//                                    final Track track = playerState.track;
//                                    if (track != null) {
//                                        Log.i(TAG, track.name + " by " + track.artist.name);
//                                    }
//                                });
                        Glide.with(context).load(R.drawable.ic_baseline_pause_24).into(ivPlayButton);
                    }
                    Toast.makeText(context, "play song", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
