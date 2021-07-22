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
import com.codepath.bop.models.Playlist;
import com.codepath.bop.models.User;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    //class constants
    public static final String TAG = "Profile Adapter";
    //instance variables
    Context context;
    List<Playlist> playlists;

    public ProfileAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        //get playlist
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "Playlist size is " + playlists.size());
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //instance variables
        private ImageView ivPlaylistCover;
        private TextView tvPlaylistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaylistCover = (ImageView)itemView.findViewById(R.id.ivPlaylistCover);
            tvPlaylistName = (TextView)itemView.findViewById(R.id.tvPlaylistName);
        }

        public void bind(Playlist playlist) {
            Log.i(TAG, "Playlist Object: " + playlist.getName());
            //get playlist name and cover from Spotify API
            tvPlaylistName.setText(playlist.getName());
            Glide.with(context).load(R.drawable.sample_record_image).centerCrop().into(ivPlaylistCover);
        }
    }
}
