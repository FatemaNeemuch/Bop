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

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class NearbyUsersFreeAdapter extends RecyclerView.Adapter<NearbyUsersFreeAdapter.ViewHolder>{

    //class constants
    public static final String TAG = "NearbyUsersFreeAdapter";

    //instance variables
    private List<ParseUser> nearbyUsers;
    private Context context;

    public NearbyUsersFreeAdapter(List<ParseUser> nearbyUsers, Context context) {
        this.nearbyUsers = nearbyUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_nearby_user_free, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyUsersFreeAdapter.ViewHolder holder, int position) {
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
        ImageView FivCover;
        TextView FtvUsernameNU;
        TextView FtvSongTitleNU;
        TextView FtvArtistNameNU;
        TextView FtvDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            FivCover = itemView.findViewById(R.id.FivCover);
            FtvUsernameNU = itemView.findViewById(R.id.FtvUsernameNU);
            FtvSongTitleNU = itemView.findViewById(R.id.FtvSongTitleNU);
            FtvArtistNameNU = itemView.findViewById(R.id.FtvArtistNameNU);
            FtvDistance = itemView.findViewById(R.id.FtvDistance);
        }

        public void bind(ParseUser pUser) {
            //set username
            FtvUsernameNU.setText(pUser.getUsername() + " is listening to...");
            //set distance
            double distance = LoginActivity.getCurrentUserLocation().distanceInMilesTo(pUser.getParseGeoPoint("location"));
            FtvDistance.setText(Math.round(distance * 100.0) / 100.0 + " m");
            //get song object
            Song pUserSong = (Song) pUser.get(User.KEY_CURRENT_SONG);
            Log.i(TAG, ParseUser.getCurrentUser().getUsername());
            //set song title
            FtvSongTitleNU.setText(pUserSong.getKEY_TITLE());
            //set artist
            FtvArtistNameNU.setText(pUserSong.getKEY_ARTIST());
            //set song cover
            Glide.with(context).load(pUserSong.getKEY_COVER_URL()).transform(new RoundedCornersTransformation(30, 5)).into(FivCover);
        }
    }
}
