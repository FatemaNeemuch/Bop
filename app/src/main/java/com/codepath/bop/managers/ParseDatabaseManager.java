package com.codepath.bop.managers;

import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.adapters.NearbyUsersAdapter;
import com.codepath.bop.adapters.NearbyUsersFreeAdapter;
import com.codepath.bop.adapters.SongAdapter;
import com.codepath.bop.models.Song;
import com.codepath.bop.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ParseDatabaseManager {

    //class constants
    public static final String TAG = "Parse Database Manager";

    //instance variables
    private static List<ParseUser> staticNearbyUsers;
    private static NearbyUsersAdapter staticAdapter;
    private static NearbyUsersFreeAdapter staticFreeAdapter;

    public static void queryNearbyUsers(List<ParseUser> nearbyUsers, NearbyUsersAdapter adapter){
        staticNearbyUsers = nearbyUsers;
        staticAdapter = adapter;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(User.KEY_CURRENT_SONG);
        query.whereNear("location", LoginActivity.getCurrentUserLocation());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                if (e == null) {
                    // avoiding null pointer
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    // set the closestUser to the one that isn't the current user
                    for(int i = 0; i < nearUsers.size(); i++) {
                        if (!nearUsers.get(i).getObjectId().equals(currentUser.getObjectId()) && nearUsers.get(i).get(User.KEY_CURRENT_SONG) != null) {
                            staticNearbyUsers.add(nearUsers.get(i));
                        }else{
//                            ParseUser queryCurrentUser = nearUsers.get(i);
//                            Song song = (Song) queryCurrentUser.get(User.KEY_CURRENT_SONG);
//                            if(song != null){
//                                Log.i(TAG, song.getKEY_TITLE());
//                            }
                            //other option:
//                            staticNearbyUsers.add(0, nearUsers.get(i));
                        }
                    }
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            staticAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.e(TAG, "error getting nearby users", e);
                    return;
                }
            }
        });
        ParseQuery.clearAllCachedResults();
    }

    public static void queryNearbyUsersFree(List<ParseUser> nearbyUsers, NearbyUsersFreeAdapter freeAdapter) {
        staticNearbyUsers = nearbyUsers;
        staticFreeAdapter = freeAdapter;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(User.KEY_CURRENT_SONG);
        query.whereNear("location", LoginActivity.getCurrentUserLocation());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                if (e == null) {
                    // avoiding null pointer
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    // set the closestUser to the one that isn't the current user
                    for(int i = 0; i < nearUsers.size(); i++) {
                        if (!nearUsers.get(i).getObjectId().equals(currentUser.getObjectId()) && nearUsers.get(i).get(User.KEY_CURRENT_SONG) != null) {
                            staticNearbyUsers.add(nearUsers.get(i));
                        }
                    }
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            staticFreeAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.e(TAG, "error getting nearby users", e);
                    return;
                }
            }
        });
        ParseQuery.clearAllCachedResults();
    }
}