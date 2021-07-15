package com.codepath.bop;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class Song {

    //instance variables
    private String songURI;
    private String title;
    private String album;
    private String artist;
    private String releaseDate;
    private String coverURL;
    private String albumType;
    private Playlist playlist;
    private boolean isCurrentSong;
    private String mAccessToken;

    public Song() {}

    public static Song fromAPI(JSONObject jsonObject) throws JSONException {
        Song song = new Song();
            song.albumType = jsonObject.getJSONObject("album").getString("album_type");
            song.album = jsonObject.getJSONObject("album").getString("name");
            song.title = jsonObject.getString("name");
            song.artist = jsonObject.getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
            if (jsonObject.getJSONObject("album").getJSONArray("artists").length() > 1){
                String artistName = "";
                for (int i = 1; i < jsonObject.getJSONObject("album").getJSONArray("artists").length(); i++){
                    artistName = artistName + ", " + jsonObject.getJSONObject("album").getJSONArray("artists").getJSONObject(i).getString("name");
                }
                song.artist = song.artist + artistName;
            }
            song.coverURL = jsonObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
            song.releaseDate = jsonObject.getJSONObject("album").getString("release_date");
            song.songURI = jsonObject.getString("uri");
        return song;
    }

//    public static Song SearchAPI(JSONObject jsonObject) throws JSONException {
//        Song song = new Song();
//        song.albumType = jsonObject.getString("album_type");
//        song.album = jsonObject.getJSONObject("album").getString("name");
//        song.title = jsonObject.getString("name");
//        song.artist = jsonObject.getJSONArray("artists").getJSONObject(0).getString("name");
//        if (jsonObject.getJSONArray("artists").length() > 1){
//            String artistName = "";
//            for (int i = 1; i < jsonObject.getJSONArray("artists").length(); i++){
//                artistName = artistName + ", " + jsonObject.getJSONArray("artists").getJSONObject(i).getString("name");
//            }
//            song.artist = song.artist + artistName;
//        }
//        song.coverURL = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
//        song.releaseDate = jsonObject.getString("release_date");
//        song.songURI = jsonObject.getString("uri");
//        return song;
//    }

    public static List<Song> fromTopHits(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items"
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            songs.add(fromAPI(jsonArray.getJSONObject(i).getJSONObject("track")));
        }
        return songs;
    }

    public static List<Song> fromSearchArray(JSONArray jsonArray) throws JSONException {
        //jsonArray is "items"
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            songs.add(fromAPI(jsonArray.getJSONObject(i)));
        }
        return songs;
    }

    public void saveSong(){
        //save all the fields to the parse database here
    }

    public String getSongURI() {
        return songURI;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public String getAlbumType() {
        return albumType;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public boolean isCurrentSong() {
        return isCurrentSong;
    }
}
