package com.codepath.bop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.adapters.ProfileAdapter;
import com.codepath.bop.dialog.CreateNewPlaylistDialogFragment;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Playlist;
import com.codepath.bop.models.User;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class ProfileFragment extends Fragment {

    //class constants
    public static final String TAG = "Profile Fragment";

    //instance variables
    private RecyclerView rvPlaylists;
    private static ProfileAdapter adapter;
    private static List<Playlist> playlists;
    private TextView tvUsernameProfile;
    private ImageView ivProfilePic;
    private static String mAccessToken;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //update the presence of a menu
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //reference to views
        rvPlaylists = view.findViewById(R.id.rvPlaylists);
        tvUsernameProfile = view.findViewById(R.id.tvUsernameProfile);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);

        //instantiate list and adapter
        playlists = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), playlists);

        //Recycler view setup: layout manager and the adapter
        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        rvPlaylists.setLayoutManager(layout);
        rvPlaylists.setAdapter(adapter);

        //set username and profile pic
        tvUsernameProfile.setText("Hi " + ParseUser.getCurrentUser().getUsername() + "!");

        //set User profile pic
        if (SpotifyDataManager.getProfilePicURl().equals(".")){
            //use generic profile pic saved on parse if no Spotify profile picture
            Glide.with(getContext())
                    .load(ParseUser.getCurrentUser().getParseFile(User.KEY_PROFILE_PIC_FILE).getUrl())
                    .circleCrop()
                    .into(ivProfilePic);
        }else{
            //use Spotify profile picture if available
            Glide.with(getContext())
                    .load(SpotifyDataManager.getProfilePicURl())
                    .circleCrop()
                    .into(ivProfilePic);
        }

        //get access token
        mAccessToken = MainActivity.getmAccessToken();

        //method makes API call- created to avoid redundant code
        getPlaylists(false, null);
    }

    public static void getPlaylists(boolean fromCreatePlaylist, CreateNewPlaylistDialogFragment createNewPlaylistDialogFragment){
        //create url for user playlists query
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/me/playlists").newBuilder();
        urlBuilder.addQueryParameter("limit", String.valueOf(50));
        String playlistUrl = urlBuilder.build().toString();
        //get user's playlists from SpotifyDataManager
        SpotifyDataManager.getPlaylists(playlistUrl, mAccessToken, playlists, adapter, fromCreatePlaylist, createNewPlaylistDialogFragment);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout button
        if (item.getItemId() == R.id.Plogout){
            onStop();
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            //go back to login page
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.btnAddPlaylist){
            showCreateNewPlaylistDialog();
        }
        return true;
    }

    private void showCreateNewPlaylistDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CreateNewPlaylistDialogFragment createNewPlaylistDialogFragment = CreateNewPlaylistDialogFragment.newInstance("Create New Playlist");
        createNewPlaylistDialogFragment.show(fm, "fragment_create_new_playlist");
    }
}