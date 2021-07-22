package com.codepath.bop.managers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.adapters.ProfileAdapter;
import com.codepath.bop.adapters.SongAdapter;
import com.codepath.bop.models.Playlist;
import com.codepath.bop.models.Song;
import com.codepath.bop.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyDataManager {

    //class constants
    public static final String TAG = "Spotify Data Manager";

    //instance variables
    private static List<Song> staticSongs;
    private static SongAdapter staticAdapter;
    private static String staticMAccessToken;
    private static String userID;
    private static String profilePic;
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static Call mCall;

//    public static void confirmUserSpotify(String url, String userEmail, String mAccessToken){
//
//        staticMAccessToken = mAccessToken;
//
//        //build request
//        final Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization", "Bearer " + staticMAccessToken)
//                .build();
//
//        //make call
//        mCall = mOkHttpClient.newCall(request);
//
//        //asynch call enqueued
//        mCall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i(TAG, "onFailure" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    final JSONObject jsonObjectUser = new JSONObject(response.body().string());
//                    if (jsonObjectUser.getString("email").equals(userEmail)){
//                        //if valid username and password, call gotoMainActivity
//                        LoginActivity.goToMainActivity();
//                    }
//                } catch (JSONException e) {
//                    Log.i(TAG, "Confirm Spotify Failed to parse data: " + e);
//                }
//            }
//        });
//    }

    public static void getUserProfile(String url, String mAccessToken) {
        //build request
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        //make call
        mCall = mOkHttpClient.newCall(request);

        //asynch call enqueued
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    //Log.i(TAG, "onResponse: " + response.body().string());
                    final JSONObject jsonObjectUser = new JSONObject(response.body().string());
                    Log.i(TAG, "got jsonObject");
                    //get UserID
                    userID = jsonObjectUser.getString("id");
                    profilePic = "";
                    if (jsonObjectUser.getJSONArray("images").length() >= 1){
                        profilePic = jsonObjectUser.getJSONArray("images").getJSONObject(0).getString("url");
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "UserProfile Failed to parse data: " + e);
                }
            }
        });
    }

    public static String getProfilePicURl(){
        return profilePic;
    }

    public static void getTopHits(String url, List<Song> songs, SongAdapter adapter, String mAccessToken) {

        //instantiate instance variables
        staticSongs = songs;
        staticAdapter = adapter;
        staticMAccessToken = mAccessToken;

        //build request
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + staticMAccessToken)
                .build();

        //make call
        mCall = mOkHttpClient.newCall(request);

        //asynch call enqueued
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectHits = new JSONObject(response.body().string());
                    //parse data to get song objects and add them to staticSongs list
                    staticSongs.addAll(fromTopHits(jsonObjectHits.getJSONArray("items")));
                    Log.i(TAG, "onResponse " + jsonObjectHits.toString());
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            staticAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    Log.i(TAG, "TopHits Failed to parse data: " + e);
                }
            }
        });
    }

    public static void SearchResults(String url){

        //build request
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + staticMAccessToken)
                .build();

        //make call
        mCall = mOkHttpClient.newCall(request);

        //asynch call enqueued
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    // Remove all songs from the adapter
                    staticSongs.clear();
                    //parse data to get song objects and add them to staticSongs list
                    staticSongs.addAll(fromSearchArray(jsonObject.getJSONObject("tracks").getJSONArray("items")));
                    Log.i(TAG, "onResponse" + jsonObject.toString());
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            staticAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    Log.i(TAG, "Search Failed to parse data: " + e);
                }
            }
        });

    }

    public static void getPlaylists(String url, String mAccessToken, List<Playlist> playlists, ProfileAdapter profileAdapter) {
        //build request
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        //make call
        mCall = mOkHttpClient.newCall(request);

        //asynch call enqueued
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectPlaylists = new JSONObject(response.body().string());
                    Log.i(TAG, "items length: " + jsonObjectPlaylists.getJSONArray("items").length());
                    //parse through object to get playlist owned by the current user
                    playlists.addAll(fromUserPlaylists(jsonObjectPlaylists.getJSONArray("items")));
                    Log.i(TAG, "Playlists length: " + playlists.size());
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            profileAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "getPlaylist Failed to parse data: " + e);
                }
            }
        });
    }

    public static List<Playlist> fromUserPlaylists(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items
        List<Playlist> playlists = new ArrayList<>();
        Log.i(TAG, "json Array length: " + jsonArray.length());
        //make playlist objects and add them to playlist list
        for (int i = 0; i < jsonArray.length(); i++) {
            Log.i(TAG, "inside for loop");
            //only get the playlist if the current user is a/the owner
            if (jsonArray.getJSONObject(i).getJSONObject("owner").getString("id").equals(userID)){
                playlists.add(Playlist.fromAPI(jsonArray.getJSONObject(i)));
                Log.i(TAG, "playlist added");
            }
        }
        Log.i(TAG, "filtered playlist length: " + playlists.size());
        return playlists;
    }

    public static List<Song> fromTopHits(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items"
        List<Song> songs = new ArrayList<>();
        //make song objects and add them to songs list
        for (int i = 0; i < jsonArray.length(); i++) {
            songs.add(Song.fromAPI(jsonArray.getJSONObject(i).getJSONObject("track")));
        }
        return songs;
    }

    public static List<Song> fromSearchArray(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items"
        List<Song> songs = new ArrayList<>();
        //make song objects and add them to songs list
        for (int i = 0; i < jsonArray.length(); i++) {
            songs.add(Song.fromAPI(jsonArray.getJSONObject(i)));
        }
        return songs;
    }
}
