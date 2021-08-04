package com.codepath.bop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.models.Song;
import com.codepath.bop.models.User;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUsersAdapter.ViewHolder>{

    //class constants
    public static final String TAG = "Nearby Users Adapter";

    //instance variables
    private List<ParseUser> nearbyUsers;
    private Context context;
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean playing;

    public NearbyUsersAdapter(List<ParseUser> nearbyUsers, Context context) {
        this.nearbyUsers = nearbyUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_nearby_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyUsersAdapter.ViewHolder holder, int position) {
        //get user
        ParseUser pUser = nearbyUsers.get(position);
        holder.bind(pUser);
    }

    @Override
    public int getItemCount() {
        return nearbyUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //instance variables
        ImageView ivCover;
        TextView tvUsernameNU;
        TextView tvSongTitleNU;
        TextView tvArtistNameNU;
        TextView tvDistance;
        ImageView ivPlayButtonNU;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //reference to views
            ivCover = itemView.findViewById(R.id.ivCover);
            tvUsernameNU = itemView.findViewById(R.id.tvUsernameNU);
            tvSongTitleNU = itemView.findViewById(R.id.tvSongTitleNU);
            tvArtistNameNU = itemView.findViewById(R.id.tvArtistNameNU);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivPlayButtonNU = itemView.findViewById(R.id.ivPlayButtonNU);
            //initialize variables
            playing = false;
        }

        public void bind(ParseUser pUser) {
            //set username
            tvUsernameNU.setText(pUser.getUsername() + " is listening to...");
            //set distance
            double distance = LoginActivity.getCurrentUserLocation().distanceInMilesTo(pUser.getParseGeoPoint("location"));
            tvDistance.setText(Math.round(distance * 100.0) / 100.0 + " m");
            //get song object
            Song pUserSong = (Song) pUser.get(User.KEY_CURRENT_SONG);
            //set song title
            tvSongTitleNU.setText(pUserSong.getKEY_TITLE());
            //set artist
            tvArtistNameNU.setText(pUserSong.getKEY_ARTIST());
            //set song cover
            Glide.with(context).load(pUserSong.getKEY_COVER_URL()).transform(new RoundedCornersTransformation(30, 5)).into(ivCover);
            //set play button
            Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButtonNU);
            //play song
            ivPlayButtonNU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pause song if button is clicked when song is playing
                    if (playing){
                        mSpotifyAppRemote.getPlayerApi().pause();
                        //change icon back to play button
                        Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButtonNU);
                        //update variable
                        playing = false;
                    }else{
                        //update current song playing
                        Song currentSong = (Song) ParseUser.getCurrentUser().get(User.KEY_CURRENT_SONG);
                        if (currentSong != null){
                            currentSong.setIsCurrentSong(currentSong, false);
                        }
                        //save new current song to parse
                        ParseUser.getCurrentUser().put(User.KEY_CURRENT_SONG, pUser.get(User.KEY_CURRENT_SONG));
                        ParseUser.getCurrentUser().saveInBackground();
                        //play song if button is clicked when the song is not playing
                        //update variable
                        playing = true;
                        //play song from spotify
                        mSpotifyAppRemote = MainActivity.getmSpotifyAppRemote();
                        mSpotifyAppRemote.getPlayerApi().play(pUserSong.getKEY_SONG_URI());
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
                        Glide.with(context).load(R.drawable.ic_baseline_pause_24).into(ivPlayButtonNU);
                    }
                }
            });
        }
    }
}
