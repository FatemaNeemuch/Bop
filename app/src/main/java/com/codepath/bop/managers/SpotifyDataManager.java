package com.codepath.bop.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.codepath.bop.Music;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.SplashActivity;
import com.codepath.bop.adapters.MusicAdapter;
import com.codepath.bop.adapters.ProfileAdapter;
import com.codepath.bop.details.AlbumDetails;
import com.codepath.bop.details.PlaylistDetails;
import com.codepath.bop.dialog.CreateNewPlaylistDialogFragment;
import com.codepath.bop.fragments.ProfileFragment;
import com.codepath.bop.models.Album;
import com.codepath.bop.models.Artist;
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
    private static MusicAdapter staticAdapter;
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
                    //get username
                    displayName = jsonObjectUser.getString("display_name");
                    //get user email
                    email = jsonObjectUser.getString("email");
                    //get whether the account is premium or free
                    product = jsonObjectUser.getString("product");
                    //launch an intent to go to login activity after request is successful
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

    public static void getTracks(String url, List<Song> songs, MusicAdapter adapter, String mAccessToken, boolean isPlaylistDetails) {

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
                    //parse data to get song objects and add them to staticSongs list
                    staticSongs.addAll(fromTopHits(jsonObjectHits.getJSONArray("items")));
                    //if the call was made from the Playlist Details class, populate this list
                    if (isPlaylistDetails){
                        PlaylistDetails.songsListForCurrentSong.addAll(staticSongs);
                    }
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            staticAdapter.setMusicList(staticSongs);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "getTracks Failed to parse data: " + e);
                }
            }
        });
    }

    public static void getTracksfromAlbum(String url, List<Song> songs, MusicAdapter adapter, String mAccessToken, boolean isAlbumDetails, Album album) {

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
                    //parse data to get song objects and add them to staticSongs list
                    staticSongs.addAll(fromAlbum(jsonObjectHits.getJSONArray("items"), album));
                    //if the call was made from the Album Details page, populate this list
                    if (isAlbumDetails){
                        AlbumDetails.songsListForCurrentSong.addAll(staticSongs);
                    }
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            staticAdapter.setMusicList(staticSongs);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "getTracksfromAlbum Failed to parse data: " + e);
                }
            }
        });
    }

    public static void getArtistAlbums(String url, List<Album> albums, MusicAdapter musicAdapter, String mAccessToken) {

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
                    final JSONObject jsonObjectHits = new JSONObject(response.body().string());
                    //parse data to get album objects and add them to albums list
                    albums.addAll(getAlbumsfromAPIArray(jsonObjectHits.getJSONArray("items")));
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            musicAdapter.setMusicList(albums);
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "ArtistAlbums Failed to parse data: " + e);
                }
            }
        });
    }

    public static void SearchResults(String url, MusicAdapter adapter, List<Music> musicSearchResults, boolean premium, boolean artists, boolean albums, boolean songs){

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
                    //get the list of album objects
                    List<Album> albumsfromarray = getAlbumsfromAPIArray(jsonObject.getJSONObject("albums").getJSONArray("items"));
                    //get the list of artist objects
                    List<Artist> artistsfromarray = fromSearchArrayArtists(jsonObject.getJSONObject("artists").getJSONArray("items"));
                    List<Song> songsfromarray;
                    if (premium){
                        //only get song results if premium account
                        songsfromarray = fromSearchArraySongs(jsonObject.getJSONObject("tracks").getJSONArray("items"));
                    }else{
                        songsfromarray = new ArrayList<>();
                    }
                    //parse data to get Music objects and add them to musicSearchResults list as needed
                    int i = 0;
                    while (i < songsfromarray.size() || i < albumsfromarray.size() || i < artistsfromarray.size()){
                        //only add the objects the calling class wants based on booleans
                        if (artists && i < artistsfromarray.size()){
                            musicSearchResults.add(artistsfromarray.get(i));
                        }
                        if (albums && i < albumsfromarray.size()){
                            musicSearchResults.add(albumsfromarray.get(i));
                        }
                        if (songs && i  < songsfromarray.size()){
                            musicSearchResults.add(songsfromarray.get(i));
                        }
                        i++;
                    }
                    //update the views on the main thread in a static method
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Update UI
                            adapter.setMusicList(musicSearchResults);
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
                    //if the call was made from Create New Playlist Modal
                    if (fromCreatePlaylist){
                        //clear list of playlists
                        playlists.clear();
                        //dismiss modal
                        createNewPlaylistDialogFragment.dismiss();
                    }
                    //parse through object to get playlist owned by the current user
                    playlists.addAll(fromUserPlaylists(jsonObjectPlaylists.getJSONArray("items")));
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
        //create response body with default playlist name
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
                    //get default playlist ID
                    defaultID = jsonObjectFavs.getString("id");
                    //save ID to current User's parse database
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("defaultPlaylistID", defaultID);
                    currentUser.saveInBackground();
                } catch (JSONException e) {
                    Log.e(TAG, "postPlaylist Failed to parse data: " + e);
                }
            }
        });
    }

    public static void createNewPlaylist(String url, String mAccessToken, String playlistName, Context context, CreateNewPlaylistDialogFragment createNewPlaylistDialogFragment) {
        //create response body with playlist name
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
                    //do nothing with objects but create to have exception show
                    final JSONObject jsonObjectNewPlaylist = new JSONObject(response.body().string());
                    //reload all the playlists to show the newly created playlist
                    ProfileFragment.getPlaylists(true, createNewPlaylistDialogFragment);
                } catch (JSONException e) {
                    Log.e(TAG, "postPlaylist Failed to parse data: " + e);
                }
            }
        });
    }

    public static void addSong(String baseurl, String mAccessToken, Song song) {
        //create empty response body
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
                    //do nothing with objects but create to have exception show
                    final JSONObject jsonObjectSong = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    Log.e(TAG, "postPlaylist Failed to parse data: " + e);
                }
            }
        });
    }

    public static List<Playlist> fromUserPlaylists(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items
        List<Playlist> playlists = new ArrayList<>();
        //make playlist objects and add them to playlist list
        for (int i = 0; i < jsonArray.length(); i++) {
            //only get the playlist if the current user is a/the owner
            if (jsonArray.getJSONObject(i).getJSONObject("owner").getString("id").equals(userID)){
                playlists.add(Playlist.fromAPI(jsonArray.getJSONObject(i)));
            }
        }
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

    public static List<Song> fromAlbum(JSONArray jsonArray, Album album) throws JSONException {
        //jsonArray is "items"
        List<Song> songs = new ArrayList<>();
        //make song objects and add them to songs list
        for (int i = 0; i < jsonArray.length(); i++) {
            songs.add(Song.fromAlbumAPI(jsonArray.getJSONObject(i), album));
        }
        return songs;
    }

    public static List<Song> fromSearchArraySongs(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items"
        List<Song> songs = new ArrayList<>();
        //make song objects and add them to songs list
        for (int i = 0; i < jsonArray.length(); i++) {
            songs.add(Song.fromAPI(jsonArray.getJSONObject(i)));
        }
        return songs;
    }

    public static List<Album> getAlbumsfromAPIArray(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items"
        List<Album> albums = new ArrayList<>();
        //make song objects and add them to songs list
        for (int i = 0; i < jsonArray.length(); i++) {
            albums.add(Album.fromAPI(jsonArray.getJSONObject(i)));
        }
        return albums;
    }

    public static List<Artist> fromSearchArrayArtists(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items"
        List<Artist> artists = new ArrayList<>();
        //make song objects and add them to songs list
        for (int i = 0; i < jsonArray.length(); i++) {
            artists.add(Artist.fromAPI(jsonArray.getJSONObject(i)));
        }
        return artists;
    }
}
