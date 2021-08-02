package com.codepath.bop.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.adapters.NearbyUsersAdapter;
import com.codepath.bop.adapters.NearbyUsersFreeAdapter;
import com.codepath.bop.managers.ParseDatabaseManager;
import com.codepath.bop.managers.SpotifyDataManager;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

public class NearbyUsersFragment extends Fragment {

    //class constants
    public static final String TAG = "Nearby Users Fragment";
    private final int TEN_SECONDS = 1000 * 10;

    //instance variables
    private static SpotifyAppRemote mSpotifyAppRemote;
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
            //Initialize the adapter
            adapter = new NearbyUsersAdapter(nearbyUsers, getContext());

            //setup the adapter
            rvNearbyUsers.setAdapter(adapter);

//            ParseDatabaseManager.queryNearbyUsers(nearbyUsers, adapter);
        }else{
            //Initialize the adapter
            freeAdapter = new NearbyUsersFreeAdapter(nearbyUsers, getContext());

            //setup the adapter
            rvNearbyUsers.setAdapter(freeAdapter);

//            ParseDatabaseManager.queryNearbyUsersFree(nearbyUsers, freeAdapter);
        }
    }

    public void scheduleUpdateNearbyUsers() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (premium){
                    nearbyUsers.clear();
                    adapter.notifyDataSetChanged();
                    ParseDatabaseManager.queryNearbyUsers(nearbyUsers, adapter);
                    Toast.makeText(getContext(), "nearby users premium", Toast.LENGTH_SHORT).show();
                }else{
                    nearbyUsers.clear();
                    freeAdapter.notifyDataSetChanged();
                    ParseDatabaseManager.queryNearbyUsersFree(nearbyUsers, freeAdapter);
                    Toast.makeText(getContext(), "nearby users free", Toast.LENGTH_SHORT).show();
                }
                ParseDatabaseManager.queryClearParseSongs();
                handler.postDelayed(this, TEN_SECONDS);
            }
        };
        handler.postDelayed(runnable, TEN_SECONDS);
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
        if (premium){
            nearbyUsers.clear();
            adapter.notifyDataSetChanged();
            ParseDatabaseManager.queryNearbyUsers(nearbyUsers, adapter);
        }else{
            nearbyUsers.clear();
            freeAdapter.notifyDataSetChanged();
            ParseDatabaseManager.queryNearbyUsersFree(nearbyUsers, freeAdapter);
        }
        //constantly update nearby users
        scheduleUpdateNearbyUsers();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}