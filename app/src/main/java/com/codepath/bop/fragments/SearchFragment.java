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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.managers.DataManager;
import com.codepath.bop.EndlessRecyclerViewScrollListener;
import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.adapters.SongAdapter;
import com.codepath.bop.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

//not being used as of now
public class SearchFragment extends Fragment {

    //class constants
    public static final String TAG = "Search Fragment";

    //instance variables
    private static SpotifyAppRemote mSpotifyAppRemote;
    private List<Song> songs;
    private RecyclerView rvSongs;
    private SongAdapter adapter;
    private static String mAccessToken;
    private String staticQuery;
    private EndlessRecyclerViewScrollListener scrollListener;
    FragmentManager fragmentManager;

    public SearchFragment(){

    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "lauched search fragment");
        //update the presence of a menu
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_browse, container, false);
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

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

//                //create url for search query
//                HttpUrl.Builder urlBuilder = HttpUrl.parse(getString(R.string.searchURL)).newBuilder();
//                urlBuilder.addQueryParameter("q", staticQuery);
//                urlBuilder.addQueryParameter("type", "track,album,artist");
//                urlBuilder.addQueryParameter("limit", String.valueOf(50));
//                urlBuilder.addQueryParameter("offset", String.valueOf(20));
//                String url = urlBuilder.build().toString();
//                DataManager.SearchResults(url);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvSongs.addOnScrollListener(scrollListener);
//
//        //get access token
//        mAccessToken = MainActivity.getmAccessToken();
//        //get top hits from DataManager
//        DataManager.getTopHits(getString(R.string.topHitsURL), songs, adapter, mAccessToken);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search_fragment, menu);
        //find view
        MenuItem searchItem = menu.findItem(R.id.maSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        fragmentManager = getChildFragmentManager();
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //launch the Search Fragment here
//                //move all query code over to new fragment
//                if(fragmentManager.findFragmentByTag("search") != null) {
//                    //if the fragment exists, show it.
//                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("search")).commit();
//                } else {
//                    //if the fragment does not exist, add it to fragment manager.
//                    fragmentManager.beginTransaction().add(R.id.flContainer, new SearchFragment(), "search").commit();
//                }
//            }
//        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
//                songs.clear();
//                adapter.notifyDataSetChanged();
//                getActivity().finish();
//                DataManager.getTopHits(getString(R.string.topHitsURL), songs, adapter, mAccessToken);
                songs.clear();
                adapter.notifyDataSetChanged();
                if (fragmentManager.findFragmentByTag("search").isVisible()){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("search"));
                }
                return false;
            }
        });
        //call a query on the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                staticQuery = query;

                //create url for search query
                HttpUrl.Builder urlBuilder = HttpUrl.parse(getString(R.string.searchURL)).newBuilder();
                urlBuilder.addQueryParameter("q", query);
                urlBuilder.addQueryParameter("type", "track,album,artist");
                urlBuilder.addQueryParameter("limit", String.valueOf(50));
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
}
