package com.codepath.bop.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.bop.details.PlaylistDetails;
import com.codepath.bop.R;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.models.Playlist;

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
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //instance variables
        private ImageView ivPlaylistCover;
        private TextView tvPlaylistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //reference views
            ivPlaylistCover = (ImageView)itemView.findViewById(R.id.ivPlaylistCover);
            tvPlaylistName = (TextView)itemView.findViewById(R.id.tvPlaylistName);
            //set listener
            itemView.setOnClickListener(this);
        }

        public void bind(Playlist playlist) {
            //set playlist name
            tvPlaylistName.setText(playlist.getName());
            //set playlist cover
            if (playlist.getCoverURL().equals("")){
                //if no cover from spotify, use generic image
                Glide.with(context).load(R.drawable.sample_record_image).into(ivPlaylistCover);
            }else{
                //show cover from spotify if available
                Glide.with(context).load(playlist.getCoverURL()).centerCrop().into(ivPlaylistCover);
            }
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the playlist at the position, this won't work if the class is static
                Playlist playlist = playlists.get(position);
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable(Playlist.class.getSimpleName(), playlist);
                // create intent for the new activity
                Intent intent = new Intent(context, PlaylistDetails.class);
                //send in playlist object
                intent.putExtras(bundle1);
                // show the activity
                context.startActivity(intent);
                //show animation when transitioning
                ((MainActivity) context).overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            }
        }
    }
}
