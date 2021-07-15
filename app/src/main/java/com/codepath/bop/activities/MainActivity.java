package com.codepath.bop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import android.widget.SearchView;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.bop.R;
import com.codepath.bop.fragments.NearbyUsersFragment;
import com.codepath.bop.fragments.PlaylistFragment;
import com.codepath.bop.fragments.ProfileFragment;
import com.codepath.bop.fragments.SearchFragment;
import com.codepath.bop.models.Song;
import com.codepath.bop.adapters.SongAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //class constants
    public static final String TAG = "Main Activity";
    private static final String CLIENT_ID = "8d28149b161f40d1b429b265bcf79e4b";
    private static final String REDIRECT_URI = "com.codepath.bop://callback";
    public static final String BASE_URL = "https://api.spotify.com/v1";
    private static final int REQUEST_CODE = 873;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private, streaming";

    //instance variables
    private static SpotifyAppRemote mSpotifyAppRemote;
    private Boolean resume;
    private List<Song> songs;
    private RecyclerView rvSongs;
    private SongAdapter adapter;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static String mAccessToken;
    private Call mCall;
    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //reference to views
//        rvSongs = findViewById(R.id.rvSongs);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//        //Initialize the list of tweets and adapter
//        songs = new ArrayList<>();
//        adapter = new SongAdapter(songs, MainActivity.this);
//
//        //Recycler view setup: layout manager and the adapter
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        rvSongs.setLayoutManager(linearLayoutManager);
//        rvSongs.setAdapter(adapter);
//
//        resume = false;

        authenticateSpotify();

//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment;
//                //dictates which fragment to launch depending on which menu item clicked
//                switch (item.getItemId()) {
//                    case R.id.bnSearch:
//                        Log.i(TAG, "going to search fragment");
//                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
//                        fragment = new SearchFragment();
//                        break;
//                    case R.id.bnPlaylists:
//                        Log.i(TAG, "going to playlist fragment");
//                        Toast.makeText(MainActivity.this, "Playlist", Toast.LENGTH_SHORT).show();
//                        fragment = new PlaylistFragment();
//                        break;
//                    case R.id.bnNearbyUsers:
//                        Log.i(TAG, "going to nearby users fragment");
//                        Toast.makeText(MainActivity.this, "NearbyUsers", Toast.LENGTH_SHORT).show();
//                        fragment = new NearbyUsersFragment();
//                        break;
//                    case R.id.bnProfile:
//                    default:
//                        Log.i(TAG, "going to profile fragment");
//                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
//                        fragment = new ProfileFragment();
//                        break;
//                }
//                //shows the fragment clicked
//                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
//                return true;
//            }
//        });
//
//        // Set default selection
//        bottomNavigationView.setSelectedItemId(R.id.bnSearch);
    }

    private void authenticateSpotify() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.i(TAG, "onActivityResult");

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    //need token for any call
                    Log.i(TAG, "token fetched");
                    mAccessToken = response.getAccessToken();
                    Log.i(TAG, "bottom navigation");
                    bottomNavigationView();
                    //request to get data everytime - url is whatever data you want
//                    getTopHits("https://api.spotify.com/v1/playlists/37i9dQZF1DXcBWIGoYBM5M/tracks");
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.i(TAG, "error when getting response");
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.i(TAG, "auth flow was cancelled");
                    // Handle other cases
            }
        }
    }

    public void bottomNavigationView(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                //dictates which fragment to launch depending on which menu item clicked
                switch (item.getItemId()) {
                    case R.id.bnSearch:
                        Log.i(TAG, "going to search fragment");
                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        fragment = new SearchFragment();
                        break;
                    case R.id.bnPlaylists:
                        Log.i(TAG, "going to playlist fragment");
                        Toast.makeText(MainActivity.this, "Playlist", Toast.LENGTH_SHORT).show();
                        fragment = new PlaylistFragment();
                        break;
                    case R.id.bnNearbyUsers:
                        Log.i(TAG, "going to nearby users fragment");
                        Toast.makeText(MainActivity.this, "NearbyUsers", Toast.LENGTH_SHORT).show();
                        fragment = new NearbyUsersFragment();
                        break;
                    case R.id.bnProfile:
                    default:
                        Log.i(TAG, "going to profile fragment");
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        fragment = new ProfileFragment();
                        break;
                }
                //shows the fragment clicked
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.bnSearch);
    }

//    private void getTopHits(String url) {
//
////        //create your own url as required code
////        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
////        //urlBuilder.addQueryParameter("q", query);
////        urlBuilder.addQueryParameter("type", "track");
////        String url = urlBuilder.build().toString();
//
//        final Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization", "Bearer " + mAccessToken)
//                .build();
//
//        mCall = mOkHttpClient.newCall(request);
//
//        mCall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i(TAG, "onFailure" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    final JSONObject jsonObjectHits = new JSONObject(response.body().string());
//                    songs.addAll(Song.fromTopHits(jsonObjectHits.getJSONArray("items")));
//                    //Log.i(TAG, "onResponse" + jsonObject.getJSONObject("albums")
//                    // .getJSONArray("items").getJSONObject(0).getString("name"));
//                    Log.i(TAG, "onResponse " + jsonObjectHits.toString());
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                } catch (JSONException e) {
//                    Log.i(TAG, "TopHits Failed to parse data: " + e);
//                }
//            }
//        });
//    }

    public static String getmAccessToken(){
        return mAccessToken;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_search_fragment, menu);
//        MenuItem searchItem = menu.findItem(R.id.maSearch);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Log.i(TAG, "onQueryTextSubmit");
//                //create your own url as required code
//                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
//                urlBuilder.addQueryParameter("q", query);
//                urlBuilder.addQueryParameter("type", "track,album,artist");
//                String url = urlBuilder.build().toString();
//
//                final Request request = new Request.Builder()
//                        .url(url)
//                        .addHeader("Authorization", "Bearer " + mAccessToken)
//                        .build();
//
//                mCall = mOkHttpClient.newCall(request);
//
//                mCall.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.i(TAG, "onFailure" + e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        try {
//                            final JSONObject jsonObject = new JSONObject(response.body().string());
//                            // Remove all songs from the adapter
//                            songs.clear();
//                            songs.addAll(Song.fromSearchArray(jsonObject.getJSONObject("tracks").getJSONArray("items")));
//                            //jsonObject.getJSONArray("items");
//                            //Log.i(TAG, "onResponse" + jsonObject.getJSONObject("albums").getJSONArray("items").getJSONObject(0).getString("name"));
//                            Log.i(TAG, "onResponse" + jsonObject.toString());
//                            runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
//                        } catch (JSONException e) {
//                            Log.i(TAG, "Search Failed to parse data: " + e);
//                        }
//                    }
//                });
//
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.logout){
//            onStop();
//            ParseUser.logOut();
//            ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
//            //go back to login page
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//        return true;
//    }

//    @Override
//    protected void onStart() {
//        Log.i(TAG, "starting");
//        super.onStart();
//
//        // Set the connection parameters - get user authorization
//        ConnectionParams connectionParams =
//                new ConnectionParams.Builder(CLIENT_ID)
//                        .setRedirectUri(REDIRECT_URI)
//                        .showAuthView(true)
//                        .build();
//
//        //connect to spotify
//        SpotifyAppRemote.connect(this, connectionParams,
//                new Connector.ConnectionListener() {
//
//                    @Override
//                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
//                        mSpotifyAppRemote = spotifyAppRemote;
//                        Log.i(TAG, "Connected! Yay!");
//
//                        // Now you can start interacting with App Remote
////                        connected();
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        Log.e(TAG, throwable.getMessage(), throwable);
//
//                        // Something went wrong when attempting to connect! Handle errors here
//                    }
//                });
//    }

//    private void connected() {
//        Log.i(TAG, "playing a playlist");
////        // Play a playlist
////        if (resume){
////            mSpotifyAppRemote.getPlayerApi().resume();
////        }else{
////            mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
////            resume = true;
////        }
////        mSpotifyAppRemote.getPlayerApi().setShuffle(true);
////
////        // Subscribe to PlayerState
////        mSpotifyAppRemote.getPlayerApi()
////                .subscribeToPlayerState()
////                .setEventCallback(playerState -> {
////                    final Track track = playerState.track;
////                    if (track != null) {
////                        Log.i(TAG, track.name + " by " + track.artist.name);
////                    }
////                });
//    }

//    public static SpotifyAppRemote getmSpotifyAppRemote(){
//        return mSpotifyAppRemote;
//    }


    //method not needed
//    @Override
//    protected void onPause() {
//        Log.i(TAG, "pausing the music");
//        super.onPause();
//        mSpotifyAppRemote.getPlayerApi().getPlayerState()
//                .setResultCallback(playerState -> {
//                    mSpotifyAppRemote.getPlayerApi().pause();
//                })
//                .setErrorCallback(throwable -> {
//                    Log.e(TAG, throwable.getMessage(), throwable);
//                });
//    }

//    @Override
//    protected void onStop() {
//        Log.i(TAG, "stopping the music");
//        super.onStop();
//        mSpotifyAppRemote.getPlayerApi().getPlayerState()
//                .setResultCallback(playerState -> {
//                    mSpotifyAppRemote.getPlayerApi().pause();
//                })
//                .setErrorCallback(throwable -> {
//                    Log.e(TAG, throwable.getMessage(), throwable);
//                });
//    }

//    @Override
//    protected void onDestroy() {
//        Log.i(TAG, "onDestroy");
//        super.onDestroy();
//        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
//    }
}