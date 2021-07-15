package com.codepath.bop.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.codepath.bop.R;
import com.codepath.bop.activities.LoginActivity;
import com.codepath.bop.activities.MainActivity;
import com.codepath.bop.adapters.SongAdapter;
import com.codepath.bop.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

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

public class SearchFragment extends Fragment {

    //class constants
    public static final String TAG = "Search Fragment";
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

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
//        authenticateSpotify();
//        Log.i(TAG, "after authenticate");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        // Setup any handles to view objects here
        //reference to views
        rvSongs = view.findViewById(R.id.rvSongs);

        //Initialize the list of tweets and adapter
        songs = new ArrayList<>();
        adapter = new SongAdapter(songs, getContext());

        //Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSongs.setLayoutManager(linearLayoutManager);
        rvSongs.setAdapter(adapter);

//        mAccessToken = MainActivity.getmAccessToken();
//        mSpotifyAppRemote = MainActivity.getmSpotifyAppRemote();

        resume = false;

        mAccessToken = MainActivity.getmAccessToken();
        getTopHits("https://api.spotify.com/v1/playlists/37i9dQZF1DXcBWIGoYBM5M/tracks");
    }

//    private void authenticateSpotify() {
//        Log.i(TAG, "authenticateSpoify");
//        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
//        builder.setScopes(new String[]{SCOPES});
//        AuthenticationRequest request = builder.build();
//        Log.i(TAG, "launching login activity");
//        AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        //super.onActivityResult(requestCode, resultCode, intent);
//
//        Log.i(TAG, "onActivityResult");
//
//        // Check if result comes from the correct activity
//        if (requestCode == REQUEST_CODE) {
//            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
//
//            switch (response.getType()) {
//                // Response was successful and contains auth token
//                case TOKEN:
//                    //need token for any call
//                    Log.i(TAG, "token fetched");
//                    mAccessToken = response.getAccessToken();
//                    //request to get data everytime - url is whatever data you want
//                    getTopHits("https://api.spotify.com/v1/playlists/37i9dQZF1DXcBWIGoYBM5M/tracks");
//                    break;
//
//                // Auth flow returned an error
//                case ERROR:
//                    Log.i(TAG, "error when getting response");
//                    break;
//
//                // Most likely auth flow was cancelled
//                default:
//                    Log.i(TAG, "auth flow was cancelled");
//                    // Handle other cases
//            }
//        }
//    }

    private void getTopHits(String url) {

//        //create your own url as required code
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
//        //urlBuilder.addQueryParameter("q", query);
//        urlBuilder.addQueryParameter("type", "track");
//        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObjectHits = new JSONObject(response.body().string());
                    songs.addAll(Song.fromTopHits(jsonObjectHits.getJSONArray("items")));
                    //Log.i(TAG, "onResponse" + jsonObject.getJSONObject("albums")
                    // .getJSONArray("items").getJSONObject(0).getString("name"));
                    Log.i(TAG, "onResponse " + jsonObjectHits.toString());
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    Log.i(TAG, "TopHits Failed to parse data: " + e);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_search_fragment, menu);
        MenuItem searchItem = menu.findItem(R.id.maSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit");
                //create your own url as required code
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
                urlBuilder.addQueryParameter("q", query);
                urlBuilder.addQueryParameter("type", "track,album,artist");
                String url = urlBuilder.build().toString();

                final Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + mAccessToken)
                        .build();

                mCall = mOkHttpClient.newCall(request);

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
                            songs.clear();
                            songs.addAll(Song.fromSearchArray(jsonObject.getJSONObject("tracks").getJSONArray("items")));
                            //jsonObject.getJSONArray("items");
                            //Log.i(TAG, "onResponse" + jsonObject.getJSONObject("albums").getJSONArray("items").getJSONObject(0).getString("name"));
                            Log.i(TAG, "onResponse" + jsonObject.toString());
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Update UI
                                    adapter.notifyDataSetChanged();
                                }
                            });
//                            getActivity().runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
                        } catch (JSONException e) {
                            Log.i(TAG, "Search Failed to parse data: " + e);
                        }
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

    @Override
    public void onStart() {
        Log.i(TAG, "starting");
        super.onStart();

        // Set the connection parameters - get user authorization
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        //connect to spotify
        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.i(TAG, "Connected! Yay!");

                        // Now you can start interacting with App Remote
//                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public static SpotifyAppRemote getmSpotifyAppRemote(){
        return mSpotifyAppRemote;
    }

    @Override
    public void onStop() {
        Log.i(TAG, "stopping the music");
        super.onStop();
        mSpotifyAppRemote.getPlayerApi().getPlayerState()
                .setResultCallback(playerState -> {
                    mSpotifyAppRemote.getPlayerApi().pause();
                })
                .setErrorCallback(throwable -> {
                    Log.e(TAG, throwable.getMessage(), throwable);
                });
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}