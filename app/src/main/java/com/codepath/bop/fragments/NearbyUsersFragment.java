package com.codepath.bop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.adapters.NearbyUsersAdapter;
import com.codepath.bop.adapters.NearbyUsersFreeAdapter;
import com.codepath.bop.managers.ParseDatabaseManager;
import com.codepath.bop.managers.SpotifyDataManager;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NearbyUsersFragment extends Fragment {

    //class constants
    public static final String TAG = "Nearby Users Fragment";
    private final int DELAY = 1000 * 60;

    //instance variables
    private List<ParseUser> nearbyUsers;
    private RecyclerView rvNearbyUsers;
    private NearbyUsersAdapter adapter;
    private NearbyUsersFreeAdapter freeAdapter;
    private boolean premium;
    private Handler handler;
    private Runnable runnable;

    public NearbyUsersFragment() {
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
        return inflater.inflate(R.layout.fragment_nearby_users, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //reference to views
        rvNearbyUsers = view.findViewById(R.id.rvNearbyUsers);

        //Initialize the list of songs
        nearbyUsers = new ArrayList<>();
        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNearbyUsers.setLayoutManager(linearLayoutManager);

        //check if account is premium
        premium = SpotifyDataManager.getProduct().equals("premium");

        if (premium){
            //Initialize and set up the adapter
            adapter = new NearbyUsersAdapter(nearbyUsers, getContext());
            rvNearbyUsers.setAdapter(adapter);
        }else{
            //Initialize and set up the free adapter
            freeAdapter = new NearbyUsersFreeAdapter(nearbyUsers, getContext());
            rvNearbyUsers.setAdapter(freeAdapter);
        }
    }

    public void scheduleUpdateNearbyUsers() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                nearbyUsers.clear();
                if (premium){
                    adapter.notifyDataSetChanged();
                    //update nearby users for premium
                    ParseDatabaseManager.queryAllNearbyUsers(nearbyUsers, adapter, null, true);
                }else{
                    freeAdapter.notifyDataSetChanged();
                    //update nearby users for free
                    ParseDatabaseManager.queryAllNearbyUsers(nearbyUsers, null, freeAdapter, false);
                }
                //clear all songs from Parse that aren't a user's current song
                ParseDatabaseManager.queryClearParseSongs();
                //perform these calls after the delay
                handler.postDelayed(this, DELAY);
            }
        };
        handler.postDelayed(runnable, DELAY);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_nearby_users, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //logout button
        if (item.getItemId() == R.id.Rlogout){
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
        nearbyUsers.clear();
        if (premium){
            //show nearby users for premium
            adapter.notifyDataSetChanged();
            ParseDatabaseManager.queryAllNearbyUsers(nearbyUsers, adapter, null, true);
        }else{
            //show nearby users for free
            freeAdapter.notifyDataSetChanged();
            ParseDatabaseManager.queryAllNearbyUsers(nearbyUsers, null, freeAdapter, false);
        }
        //constantly update nearby users
        scheduleUpdateNearbyUsers();
    }

    @Override
    public void onStop() {
        super.onStop();
        //stop updating nearby users
        handler.removeCallbacks(runnable);
    }
}