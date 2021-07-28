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

import com.codepath.bop.Music;
import com.codepath.bop.activities.SplashActivity;
import com.codepath.bop.adapters.MusicAdapter;
import com.codepath.bop.managers.SpotifyDataManager;
import com.codepath.bop.EndlessRecyclerViewScrollListener;
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

public class BrowseFragment extends Fragment {

    //class constants
    public static final String TAG = "Browse Fragment";
    private static final String CLIENT_ID = "8d28149b161f40d1b429b265bcf79e4b";
    private static final String REDIRECT_URI = "com.codepath.bop://callback";

    //instance variables
    private static SpotifyAppRemote mSpotifyAppRemote;
//    private List<Song> songs;
    private RecyclerView rvSongs;
//    private SongAdapter adapter;
    private static String mAccessToken;
    private boolean premium;
    private List<Song> musicItems;
    private MusicAdapter musicAdapter;
    private List<? extends Music> musicSearchResults;
    private String staticQuery;
    private EndlessRecyclerViewScrollListener scrollListener;
//    private final FragmentManager fragmentManager = getChildFragmentManager();
//    private FragmentManager fragmentManager;

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
//        songs = new ArrayList<>();
        musicItems = new ArrayList<>();
//        adapter = new SongAdapter(songs, getContext(), premium);
        musicAdapter = new MusicAdapter(musicItems, getContext());

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSongs.setLayoutManager(linearLayoutManager);
//        rvSongs.setAdapter(adapter);
        rvSongs.setAdapter(musicAdapter);

        //get access token
        mAccessToken = MainActivity.getmAccessToken();

        //get top hits from SpotifyDataManager
        SpotifyDataManager.getTracks(getString(R.string.topHitsURL), (List<Song>) musicItems, musicAdapter, mAccessToken, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search_fragment, menu);
        //find view
        MenuItem searchItem = menu.findItem(R.id.maSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        if (isAdded()){
//            fragmentManager = getChildFragmentManager();
//        }
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //launch the Search Fragment here
//                //move all query code over to new fragment
//                Log.i(TAG, isAdded() + "");
////                if (isVisible()){
//                Log.i(TAG, "browse fragment: " + getParentFragmentManager().findFragmentByTag("browse"));
//                    getParentFragmentManager().beginTransaction().hide(getParentFragmentManager().findFragmentByTag("browse"));
////                }
//                if(isAdded()){
//                    if(fragmentManager.findFragmentByTag("search") != null) {
//                        //if the fragment exists, show it.
//                        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("search")).addToBackStack(null).commit();
//                    } else {
//                        //if the fragment does not exist, add it to fragment manager.
//                        fragmentManager.beginTransaction().add(R.id.flContainerSF, new SearchFragment(), "search").addToBackStack(null).commit();
//                    }
//                }
//            }
//        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                musicItems.clear();
                musicAdapter.notifyDataSetChanged();
                SpotifyDataManager.getTracks(getString(R.string.topHitsURL), (List<Song>) musicItems, musicAdapter, mAccessToken, false);
//                songs.clear();
//                adapter.notifyDataSetChanged();
//                if (isAdded()){
//                    Log.i(TAG, "search fragment: " + fragmentManager.findFragmentByTag("search"));
//                    if (fragmentManager.findFragmentByTag("search").isAdded() && fragmentManager.findFragmentByTag("search").isVisible()){
//                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("search"));
//                    }
//                }
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
//                urlBuilder.addQueryParameter("type", "track,artist,album,playlist");
                urlBuilder.addQueryParameter("type", "track,album");
                urlBuilder.addQueryParameter("limit", String.valueOf(50));
                String url = urlBuilder.build().toString();

                //get search results from DataManager
                SpotifyDataManager.SearchResults(url, musicAdapter);
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