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
import com.codepath.bop.Music;
import com.codepath.bop.R;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Album;
import com.codepath.bop.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MusicAdapter extends RecyclerView.Adapter {

    //class constants
    public static final String TAG = "Music Adapter";

    //instance variables
    private List<Music> musicList;
    private Context context;

    public MusicAdapter(List<? extends Music> musicList, Context context) {
        setMusicList(musicList);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return musicList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case Music.TYPE_SONG:
                itemView = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
                return new SongViewHolder(itemView);
            default: // TYPE_ALBUM
                itemView = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
                return new AlbumViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case Music.TYPE_SONG:
                ((SongViewHolder) holder).bindView(position);
                break;
            case Music.TYPE_ALBUM:
                ((AlbumViewHolder) holder).bindView(position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void setMusicList(List<? extends Music> musicListItems) {
        if (musicList == null){
            musicList = new ArrayList<>();
        }
        musicList.clear();
        musicList.addAll(musicListItems);
        notifyDataSetChanged();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder{

        //instance variables
        private TextView tvSongTitle;
        private TextView tvArtistName;
        private ImageView ivCover;
        private ImageView ivPlayButton;
        private boolean isDoubleClicked;
        private boolean premium;
        private SpotifyAppRemote mSpotifyAppRemote;
        private boolean playing;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            //reference views
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            ivCover = itemView.findViewById(R.id.ivCover);
            ivPlayButton = itemView.findViewById(R.id.ivPlayButton);
            premium = SpotifyDataManager.getProduct().equals("premium");
            //initialize variable
            playing = false;
        }

        void bindView(int position) {
            Song song = (Song) musicList.get(position);
            //set song title
            tvSongTitle.setText(song.getTitle());
            //set artist
            tvArtistName.setText(song.getArtist());
            //set song cover
            Glide.with(context).load(song.getCoverURL()).transform(new RoundedCornersTransformation(30, 5)).into(ivCover);
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
                        SpotifyDataManager.addSong("https://api.spotify.com/v1/playlists/" + ParseUser.getCurrentUser().get("defaultPlaylistID") + "/tracks",
                                MainActivity.getmAccessToken(), (Song) musicList.get(getAdapterPosition()));
                        Toast.makeText(context, context.getString(R.string.added_song) + " " + ParseUser.getCurrentUser().getUsername() + context.getString(R.string.default_favs), Toast.LENGTH_SHORT).show();
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

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bindView(int position) {
            Album album = (Album) musicList.get(position);
            // bind data to the views
            // textView.setText()...
        }
    }
}
