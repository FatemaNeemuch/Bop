package com.codepath.bop.managers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.adapters.NearbyUsersAdapter;
import com.codepath.bop.adapters.NearbyUsersFreeAdapter;
import com.codepath.bop.models.Song;
import com.codepath.bop.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
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
    private static List<ParseUser> allUsers;

    public static void queryAllNearbyUsers(List<ParseUser> nearbyUsers, NearbyUsersAdapter adapter, NearbyUsersFreeAdapter freeAdapter, boolean premium){
        staticNearbyUsers = nearbyUsers;
        staticAdapter = adapter;
        staticFreeAdapter = freeAdapter;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(User.KEY_CURRENT_SONG);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                if (e == null) {
                    allUsers = new ArrayList<>();
                    allUsers.addAll(nearUsers);
                    // avoiding null pointer
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    // set the closestUser to the one that isn't the current user
                    for(int i = 0; i < nearUsers.size(); i++) {
                        if (!nearUsers.get(i).getObjectId().equals(currentUser.getObjectId()) && nearUsers.get(i).get(User.KEY_CURRENT_SONG) != null) {
                            staticNearbyUsers.add(nearUsers.get(i));
                        }
                    }
                    sortOnDistance(staticNearbyUsers);
                    if (premium){
                        //update the views on the main thread in a static method
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Update UI
                                staticAdapter.notifyDataSetChanged();
                            }
                        });
                    }else{
                        //update the views on the main thread in a static method
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Update UI
                                staticFreeAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "error getting nearby users", e);
                    return;
                }
            }
        });
        ParseQuery.clearAllCachedResults();
    }

    public static void queryClearParseSongs() {
        ParseQuery<Song> query = ParseQuery.getQuery(Song.class);
        query.findInBackground(new FindCallback<Song>() {
            @Override
            public void done(List<Song> songs, ParseException e) {
                if (e == null) {
                    ArrayList<String> userSongIDs = new ArrayList<>();
                    for (int i = 0; i < allUsers.size(); i++){
                        Song userSong = (Song) allUsers.get(i).get(User.KEY_CURRENT_SONG);
                        if (userSong != null){
                            userSongIDs.add(userSong.getObjectId());
                        }
                    }
                    // set the closestUser to the one that isn't the current user
                    for(Song song: songs) {
                        if (!userSongIDs.contains(song.getObjectId())){
                            song.deleteInBackground();
                        }
                    }
                } else {
                    Log.e(TAG, "error getting songs to clear", e);
                    return;
                }
            }
        });
        ParseQuery.clearAllCachedResults();
    }

    //insertion sort
    public static void sortOnDistance(List<ParseUser> nearbyUsers){
        ParseGeoPoint currentUserLocation = LoginActivity.getCurrentUserLocation();
        for (int i = 1; i < nearbyUsers.size(); i++){
            double tempDistance = currentUserLocation.distanceInMilesTo(nearbyUsers.get(i).getParseGeoPoint("location"));
            ParseUser tempUser = nearbyUsers.get(i);
            int j = i;
            //if temp distance is less than the distance of user in front of it, swap users
            while (j > 0 && tempDistance < currentUserLocation.distanceInMilesTo(nearbyUsers.get(j - 1).getParseGeoPoint("location"))){
                //swap users
                nearbyUsers.set(j, nearbyUsers.get(j - 1));
                nearbyUsers.set(j -1, tempUser);
                j--;
            }
        }
    }
}