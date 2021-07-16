package com.codepath.bop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.DataManager;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.adapters.SongAdapter;
import com.codepath.bop.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class SearchFragment extends Fragment {

    //class constants
    public static final String TAG = "Search Fragment";
    private static final String CLIENT_ID = "8d28149b161f40d1b429b265bcf79e4b";
    private static final String REDIRECT_URI = "com.codepath.bop://callback";

    //instance variables
    private static SpotifyAppRemote mSpotifyAppRemote;
    private List<Song> songs;
    private RecyclerView rvSongs;
    private SongAdapter adapter;
    private static String mAccessToken;

    public SearchFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        //update the presence of a menu
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //reference to views
        rvSongs = view.findViewById(R.id.rvSongs);

        //Initialize the list of tweets and adapter
        songs = new ArrayList<>();
        adapter = new SongAdapter(songs, getContext());

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSongs.setLayoutManager(linearLayoutManager);
        rvSongs.setAdapter(adapter);

        //get access token
        mAccessToken = MainActivity.getmAccessToken();
        //get top hits from DataManager
        DataManager.getTopHits(getString(R.string.topHitsURL), songs, adapter, mAccessToken);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search_fragment, menu);
        //find view
        MenuItem searchItem = menu.findItem(R.id.maSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //call a query on the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //create url for search query
                HttpUrl.Builder urlBuilder = HttpUrl.parse(getString(R.string.searchURL)).newBuilder();
                urlBuilder.addQueryParameter("q", query);
                urlBuilder.addQueryParameter("type", "track,album,artist");
                String url = urlBuilder.build().toString();

                //get search results from DataManager
                DataManager.SearchResults(url);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout button
        if (item.getItemId() == R.id.logout){
            onStop();
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            //go back to login page
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the connection parameters - get user authorization
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        //connect to spotify
        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.i(TAG, "Connected! Yay!");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public static SpotifyAppRemote getmSpotifyAppRemote(){
        return mSpotifyAppRemote;
    }

    @Override
    public void onStop() {
        Log.i(TAG, "stopping the music");
        super.onStop();
        //check if app is running in background and only pause music if its not
        //use the getActivity().isFinishing method
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(playerState -> {
                    mSpotifyAppRemote.getPlayerApi().pause();
                })
                .setErrorCallback(throwable -> {
                    Log.e(TAG, throwable.getMessage(), throwable);
                });
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}