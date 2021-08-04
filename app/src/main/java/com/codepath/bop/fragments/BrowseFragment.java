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
import androidx.fragment.app.FragmentManager;
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
        inflater.inflate(R.menu.menu_browse_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout button
        if (item.getItemId() == R.id.Blogout){
            onStop();
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
            //go back to login page
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.startSearch){
            final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if(fragmentManager.findFragmentByTag("search") != null) {
                //set title
                getActivity().setTitle("Search");
                //if the fragment exists, show it.
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(fragmentManager.findFragmentByTag("search"))
                        .addToBackStack(null)
                        .commit();
            } else {
                //set title
                getActivity().setTitle("Search");
                if (premium){
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.flContainer, new SearchFragment(), "search").addToBackStack(null).commit();
                }else{
                    //if the fragment does not exist, add it to fragment manager.
                    fragmentManager.beginTransaction().add(R.id.flContainer, new SearchFreeFragment(), "search").addToBackStack(null).commit();
                }
            }
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("browse")).commit();
        }
        return true;
    }
}