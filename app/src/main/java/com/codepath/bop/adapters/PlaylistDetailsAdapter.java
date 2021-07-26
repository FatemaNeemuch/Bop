package com.codepath.bop.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.models.Song;

import java.util.List;

public class PlaylistDetailsAdapter extends RecyclerView.Adapter<PlaylistDetailsAdapter.ViewHolder>{

    private List<Song> playlistSongs;
    private Context context;

    public PlaylistDetailsAdapter(List<Song> playlistSongs, Context context) {
        this.playlistSongs = playlistSongs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDetailsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
