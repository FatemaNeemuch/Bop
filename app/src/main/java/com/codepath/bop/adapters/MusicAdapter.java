package com.codepath.bop.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.codepath.bop.details.AlbumDetails;
import com.codepath.bop.details.ArtistDetails;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Album;
import com.codepath.bop.models.Artist;
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
        //figure out what object the item is
        return musicList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        //inflate correct view based on object type
        switch (viewType) {
            case Music.TYPE_SONG:
                itemView = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
                return new SongViewHolder(itemView);
            case Music.TYPE_ARTIST:
                itemView = LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false);
                return new ArtistViewHolder(itemView);
            default: // TYPE_ALBUM
                itemView = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
                return new AlbumViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //go to correct viewholder's bind method based on object type
        switch (getItemViewType(position)) {
            case Music.TYPE_SONG:
                ((SongViewHolder) holder).bindView(position);
                break;
            case Music.TYPE_ALBUM:
                ((AlbumViewHolder) holder).bindView(position);
                break;
            case Music.TYPE_ARTIST:
                ((ArtistViewHolder) holder).bindView(position);
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
            //initialize variables
            premium = SpotifyDataManager.getProduct().equals("premium");
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
                ivPlayButton.setBackgroundResource(R.drawable.circle_background);
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
                    //single click actions
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
                        Toast.makeText(context, song.getTitle() + " " + context.getString(R.string.added_song) + " " + ParseUser.getCurrentUser().getUsername() + context.getString(R.string.default_favs), Toast.LENGTH_SHORT).show();
                        isDoubleClicked = false;
                        //remove callbacks for Handlers
                        handler.removeCallbacks(r);
                    }else{
                        isDoubleClicked=true;
                        handler.postDelayed(r,500);
                    }
                }
            });
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //instance variables
        private ImageView ivAlbumCover;
        private TextView tvAlbumTitle;
        private TextView tvAlbumArtistName;
        private ImageView ivGoToDetails;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            //reference views
            ivAlbumCover = itemView.findViewById(R.id.ivAlbumCover);
            tvAlbumTitle = itemView.findViewById(R.id.tvAlbumTitle);
            tvAlbumArtistName = itemView.findViewById(R.id.tvAlbumArtistName);
            ivGoToDetails = itemView.findViewById(R.id.ivGoToDetails);
            //set listener
            itemView.setOnClickListener(this);
        }

        void bindView(int position) {
            Album album = (Album) musicList.get(position);
            //set album name
            tvAlbumTitle.setText(album.getAlbumName());
            //set album artist
            tvAlbumArtistName.setText(album.getArtist());
            //set album cover
            Glide.with(context).load(album.getCoverURL()).transform(new RoundedCornersTransformation(30, 5)).into(ivAlbumCover);
            //show details arrow
            Glide.with(context).load(R.drawable.outline_arrow_right_24).into(ivGoToDetails);

            //go to details page button
            ivGoToDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        Bundle bundle1 = new Bundle();
                        bundle1.putParcelable(Album.class.getSimpleName(), album);
                        // create intent for the new activity
                        Intent intent = new Intent(context, AlbumDetails.class);
                        //send in playlist object
                        intent.putExtras(bundle1);
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the artist at the position, this won't work if the class is static
                Album album = (Album) musicList.get(position);
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable(Album.class.getSimpleName(), album);
                // create intent for the new activity
                Intent intent = new Intent(context, AlbumDetails.class);
                //send in playlist object
                intent.putExtras(bundle1);
                // show the activity
                context.startActivity(intent);
            }
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //instance variables
        private ImageView ivArtistImage;
        private TextView tvArtistNameItem;
        private ImageView ivGoToDetailsArtist;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            //reference to views
            ivArtistImage = itemView.findViewById(R.id.ivArtistImage);
            tvArtistNameItem = itemView.findViewById(R.id.tvArtistNameItem);
            ivGoToDetailsArtist = itemView.findViewById(R.id.ivGoToDetailsArtist);
            //set listener
            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {
            Artist artist = (Artist) musicList.get(position);
            //set the Artist name
            tvArtistNameItem.setText(artist.getArtistName());
            //set the artist image
            if (artist.getArtistImageURL().equals("")){
                Glide.with(context).load(R.drawable.generic_profile_pic).circleCrop().into(ivArtistImage);
            }else{
                Glide.with(context).load(artist.getArtistImageURL()).circleCrop().into(ivArtistImage);
            }
            //show details arrow
            Glide.with(context).load(R.drawable.outline_chevron_right_24).into(ivGoToDetailsArtist);
            //go to details page button
            ivGoToDetailsArtist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        Bundle bundle1 = new Bundle();
                        bundle1.putParcelable(Artist.class.getSimpleName(), artist);
                        // create intent for the new activity
                        Intent intent = new Intent(context, ArtistDetails.class);
                        //send in playlist object
                        intent.putExtras(bundle1);
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the artist at the position, this won't work if the class is static
                Artist artist = (Artist) musicList.get(position);
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable(Artist.class.getSimpleName(), artist);
                // create intent for the new activity
                Intent intent = new Intent(context, ArtistDetails.class);
                //send in playlist object
                intent.putExtras(bundle1);
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
