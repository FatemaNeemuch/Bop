package com.codepath.bop.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.codepath.bop.Details.PlaylistDetails;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.SplashActivity;
import com.codepath.bop.adapters.ProfileAdapter;
import com.codepath.bop.adapters.SongAdapter;
import com.codepath.bop.dialog.CreateNewPlaylistDialogFragment;
import com.codepath.bop.fragments.ProfileFragment;
import com.codepath.bop.models.Playlist;
import com.codepath.bop.models.Song;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private static String userURI;
    private static String displayName;
    private static String email;
    private static String product;
    private static String defaultID;
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static Call mCall;

    public static void getUserProfile(String url, String mAccessToken, Context context) {
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
                Log.e(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectUser = new JSONObject(response.body().string());
                    //get UserID
                    userID = jsonObjectUser.getString("id");
                    //get user URI
                    userURI = jsonObjectUser.getString("uri");
                    //get profile pic url if there is a profile pic
                    profilePic = ".";
                    if (jsonObjectUser.getJSONArray("images").length() >= 1){
                        profilePic = jsonObjectUser.getJSONArray("images").getJSONObject(0).getString("url");
                    }
                    Log.i(TAG, "profile pic from JSON Object " + profilePic);
                    //get username
                    displayName = jsonObjectUser.getString("display_name");
                    //get user email
                    email = jsonObjectUser.getString("email");
                    //get whether the account is premium or free
                    product = jsonObjectUser.getString("product");
                    //launch an intent to go to login activity after request is successful
                    Log.i(TAG, "product " + product);
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    //finish SplashActivity so user can swipe backwards to it
                    ((SplashActivity) context).finish();
                } catch (JSONException e) {
                    Log.e(TAG, "UserProfile Failed to parse data: " + e);
                }
            }
        });
    }

    public static String getUserID(){
        return userID;
    }

    public static String getProduct(){
        return product;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getUserURI(){
        return userURI;
    }

    public static String getProfilePicURl(){
        return profilePic;
    }

    public static void getTracks(String url, List<Song> songs, SongAdapter adapter, String mAccessToken, boolean isPlaylistDetails) {

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
                Log.e(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectHits = new JSONObject(response.body().string());
                    Log.i(TAG, "onResponse: " + jsonObjectHits.getJSONArray("items"));
                    //parse data to get song objects and add them to staticSongs list
                    staticSongs.addAll(fromTopHits(jsonObjectHits.getJSONArray("items")));
                    if (isPlaylistDetails){
                        PlaylistDetails.songsListForCurrentSong.addAll(staticSongs);
                    }
                    Log.i(TAG, "onResponse getTopHits");
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
                    Log.e(TAG, "TopHits Failed to parse data: " + e);
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
                Log.e(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    // Remove all songs from the adapter
                    staticSongs.clear();
                    //parse data to get song objects and add them to staticSongs list
                    staticSongs.addAll(fromSearchArray(jsonObject.getJSONObject("tracks").getJSONArray("items")));
                    Log.i(TAG, "Search Query onResponse" + jsonObject.toString());
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "adpater updated");
                            //Update UI
                            staticAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Search Failed to parse data: " + e);
                }
            }
        });

    }

    public static void getPlaylists(String url, String mAccessToken, List<Playlist> playlists, ProfileAdapter profileAdapter, boolean fromCreatePlaylist, CreateNewPlaylistDialogFragment createNewPlaylistDialogFragment) {
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
                Log.e(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectPlaylists = new JSONObject(response.body().string());
                    Log.i(TAG, "items length: " + jsonObjectPlaylists.getJSONArray("items").length());
                    if (fromCreatePlaylist){
                        playlists.clear();
                        createNewPlaylistDialogFragment.dismiss();
                    }
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

    public static void createDefaultPlaylist(String url, String mAccessToken, String username) {
        String json = "{\"name\":\"" + username + "'s Favs\"}";
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        //build request
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        //make the call
        mCall = mOkHttpClient.newCall(request);

        //asynch call enqueued
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectFavs = new JSONObject(response.body().string());
                    defaultID = jsonObjectFavs.getString("id");
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("defaultPlaylistID", defaultID);
                    currentUser.saveInBackground();
                    Log.i(TAG, "successfully added default playlist");
                } catch (JSONException e) {
                    Log.e(TAG, "postPlaylist Failed to parse data: " + e);
                }
            }
        });
    }

    public static String getDefaultID(){
        return defaultID;
    }

    public static void createNewPlaylist(String url, String mAccessToken, String playlistName, Context context, CreateNewPlaylistDialogFragment createNewPlaylistDialogFragment) {
        String json = "{\"name\":\"" + playlistName + "\"}";
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        //build request
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        //make the call
        mCall = mOkHttpClient.newCall(request);

        //asynch call enqueued
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectNewPlaylist = new JSONObject(response.body().string());
                    ProfileFragment.getPlaylists(true, createNewPlaylistDialogFragment);
                    Log.i(TAG, "successfully added new playlist");
                } catch (JSONException e) {
                    Log.e(TAG, "postPlaylist Failed to parse data: " + e);
                }
            }
        });
    }

    public static void addSong(String baseurl, String mAccessToken, Song song) {
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json"), "");

        //create url for search query
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseurl).newBuilder();
        urlBuilder.addQueryParameter("uris", song.getSongURI());
        String url = urlBuilder.build().toString();

        //build request
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        //make the call
        mCall = mOkHttpClient.newCall(request);

        //asynch call enqueued
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectSong = new JSONObject(response.body().string());
                    Log.i(TAG, "successfully added " + song.getTitle() + " to default playlist");
                } catch (JSONException e) {
                    Log.e(TAG, "postPlaylist Failed to parse data: " + e);
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
            //only get the playlist if the current user is a/the owner
            if (jsonArray.getJSONObject(i).getJSONObject("owner").getString("id").equals(userID)){
                playlists.add(Playlist.fromAPI(jsonArray.getJSONObject(i)));
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
