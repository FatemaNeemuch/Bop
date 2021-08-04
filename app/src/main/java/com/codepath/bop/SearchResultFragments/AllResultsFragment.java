package com.codepath.bop.SearchResultFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.Music;
import com.codepath.bop.R;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.adapters.MusicAdapter;
import com.codepath.bop.fragments.SearchFragment;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.models.Song;

import java.util.ArrayList;
import java.util.List;

public class AllResultsFragment extends Fragment {

    //class constants
    public static final String TAG = "All Results";

    //instance variables
    private List<Music> musicSearchResults;
    private MusicAdapter musicAdapter;
    private RecyclerView rvSearchResults;
    private static String mAccessToken;
    private boolean premium;

    public AllResultsFragment() {}

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //reference to views
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        //check if account is premium
        premium = SpotifyDataManager.getProduct().equals("premium");

        //Initialize the list of songs and adapter
        musicSearchResults = new ArrayList<>();
        musicAdapter = new MusicAdapter(musicSearchResults, getContext());

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSearchResults.setLayoutManager(linearLayoutManager);
        rvSearchResults.setAdapter(musicAdapter);

        //get access token
        mAccessToken = MainActivity.getmAccessToken();

        SpotifyDataManager.SearchResults(SearchFragment.getURL(), musicAdapter, musicSearchResults, premium);
    }
}
