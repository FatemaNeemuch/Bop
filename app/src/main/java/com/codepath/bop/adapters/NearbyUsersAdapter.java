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
import com.codepath.bop.fragments.BrowseFragment;
import com.codepath.bop.fragments.NearbyUsersFragment;
import com.codepath.bop.models.Song;
import com.codepath.bop.models.User;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.List;

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
            ivCover = itemView.findViewById(R.id.ivCover);
            tvUsernameNU = itemView.findViewById(R.id.tvUsernameNU);
            tvSongTitleNU = itemView.findViewById(R.id.tvSongTitleNU);
            tvArtistNameNU = itemView.findViewById(R.id.tvArtistNameNU);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivPlayButtonNU = itemView.findViewById(R.id.ivPlayButtonNU);
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
            //get current user's song object
//            Song currentUserSong = (Song) ParseUser.getCurrentUser().get(User.KEY_CURRENT_SONG);
            Log.i(TAG, ParseUser.getCurrentUser().getUsername());
            //set song title
            tvSongTitleNU.setText(pUserSong.getKEY_TITLE());
            //set artist
            tvArtistNameNU.setText(pUserSong.getKEY_ARTIST());
            //set song cover
            Glide.with(context).load(pUserSong.getKEY_COVER_URL()).into(ivCover);
            //set play button based on whether the song is playing in recycler view
//            if (sameSong(pUserSong, currentUserSong)){
//                Glide.with(context).load(R.drawable.ic_baseline_pause_24).into(ivPlayButtonNU);
//            }else{
//                Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButtonNU);
//            }
            Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButtonNU);
            //play song here as well
            ivPlayButtonNU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pause song if button is clicked when song is playing
                    if (playing){
                        mSpotifyAppRemote.getPlayerApi().pause();
                        //change icon back to play button
                        Glide.with(context).load(R.drawable.ic_baseline_play_arrow_24).into(ivPlayButtonNU);
                        //update current song playing
//                        ParseUser.getCurrentUser().put(User.KEY_CURRENT_SONG, null); //not sure this line works
                        //update variable
                        playing = false;
                    }else{
                        //update current song playing
                        ParseUser.getCurrentUser().put(User.KEY_CURRENT_SONG, pUser.get(User.KEY_CURRENT_SONG));
                        ParseUser.getCurrentUser().saveInBackground();
                        //play song if button is clicked when the song is not playing
                        //update variable
                        playing = true;
                        //play song from spotify
                        mSpotifyAppRemote = NearbyUsersFragment.getmSpotifyAppRemote();
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

        private boolean sameSong(Song pUserSong, Song currentUserSong){
            if (pUserSong.getObjectId().equals(currentUserSong.getObjectId())){
                Log.i(TAG, "pUserSong object ID: " + pUserSong.getObjectId());
                Log.i(TAG, "currentUserSong object ID: " + currentUserSong.getObjectId());
                return true;
            }
            return false;
        }
    }
}
