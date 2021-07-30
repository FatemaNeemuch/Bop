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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.Music;
import com.codepath.bop.adapters.MusicAdapter;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.models.Song;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class BrowseFragment extends Fragment {

    //class constants
    public static final String TAG = "Browse Fragment";

    //instance variables
    private List<Song> songs;
    private RecyclerView rvSongs;
    private static String mAccessToken;
    private boolean premium;
    private MusicAdapter musicAdapter;
    private List<Music> musicSearchResults;

    public BrowseFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //update the presence of a menu
        setHasOptionsMenu(true);
        //set title
//        getActivity().setTitle("Search");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_browse, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //reference to views
        rvSongs = view.findViewById(R.id.rvSongs);

        //check if account is premium
        premium = SpotifyDataManager.getProduct().equals("premium");

        //Initialize the list of songs and adapter
        songs = new ArrayList<>();
        musicAdapter = new MusicAdapter(songs, getContext());

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSongs.setLayoutManager(linearLayoutManager);
        rvSongs.setAdapter(musicAdapter);

        //get access token
        mAccessToken = MainActivity.getmAccessToken();

        //get top hits from SpotifyDataManager
        SpotifyDataManager.getTracks(getString(R.string.topHitsURL), songs, musicAdapter, mAccessToken, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search_fragment, menu);
        //find view
        MenuItem searchItem = menu.findItem(R.id.maSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                songs.clear();
                musicAdapter.notifyDataSetChanged();
                SpotifyDataManager.getTracks(getString(R.string.topHitsURL), songs, musicAdapter, mAccessToken, false);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //figure out how to clear adpater here
                Toast.makeText(getContext(), "onSearchClick SearchView", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onSearchClick SearchView");
            }
        });
        //call a query on the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //create url for search query
                HttpUrl.Builder urlBuilder = HttpUrl.parse(getString(R.string.searchURL)).newBuilder();
                urlBuilder.addQueryParameter("q", query);
//                urlBuilder.addQueryParameter("type", "track,artist,album,playlist");
                urlBuilder.addQueryParameter("type", "track,artist,album");
                urlBuilder.addQueryParameter("limit", String.valueOf(50));
                String url = urlBuilder.build().toString();

                musicSearchResults = new ArrayList<>();

                //get search results from DataManager
                SpotifyDataManager.SearchResults(url, musicAdapter, musicSearchResults);
                searchView.clearFocus();
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
}