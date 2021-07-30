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
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.fragments.BrowseFragment;
import com.codepath.bop.fragments.NearbyUsersFragment;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Song;
import com.codepath.bop.models.User;
import com.parse.ParseObject;
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
        private boolean isDoubleClicked;

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
            Glide.with(context).load(pUserSong.getKEY_COVER_URL()).transform(new RoundedCornersTransformation(30, 5)).into(ivCover);
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

//            //Double click post to like:
//            isDoubleClicked=false;
//
//            Handler handler=new Handler();
//            Runnable r=new Runnable(){
//                @Override
//                public void run(){
//                    //Actions when Single Clicked
//                    Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
//                    isDoubleClicked = false;
//                }
//            };
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isDoubleClicked){
//                        //actions when double clicked
//                        //add song to favs playlist
//                        Song song = (Song) nearbyUsers.get(getAdapterPosition()).get(User.KEY_CURRENT_SONG);
//                        Log.i(TAG, "song objects for user: " + ((Song) nearbyUsers.get(getAdapterPosition()).get(User.KEY_CURRENT_SONG)).getTitle());
//                        SpotifyDataManager.addSong("https://api.spotify.com/v1/playlists/" + ParseUser.getCurrentUser().get("defaultPlaylistID") + "/tracks",
//                                MainActivity.getmAccessToken(), song);
//                        Toast.makeText(context, song.getTitle() + " " + context.getString(R.string.added_song) + " " + ParseUser.getCurrentUser().getUsername() + context.getString(R.string.default_favs), Toast.LENGTH_SHORT).show();
//                        isDoubleClicked = false;
//                        //remove callbacks for Handlers
//                        handler.removeCallbacks(r);
//                    }else{
//                        isDoubleClicked=true;
//                        handler.postDelayed(r,500);
//                    }
//                }
//            });

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
