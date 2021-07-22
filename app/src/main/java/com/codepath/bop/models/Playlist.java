package com.codepath.bop.models;

import com.codepath.bop.activities.MainActivity;
import com.google.gson.JsonObject;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//probs not needed
@ParseClassName("Playlist")
public class Playlist extends ParseObject {

    //class constants
    public static final String KEY_NAME = "name";
//    public static final String KEY_CREATOR = "creator";
    public static final String KEY_PLAYLIST_URI = "playlistURI";

    //instance variables
    private String name;
    private String coverURL;
    private List<Song> songs;
    private String playlistURI;
//    private ParseUser creator;

    public Playlist() {
    }

    public static Playlist fromAPI(JSONObject jsonObject) throws JSONException {
        Playlist playlist = new Playlist();
        playlist.name = jsonObject.getString("name");
        if (jsonObject.getJSONArray("images").length() >= 1){
            playlist.coverURL = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        }else{
            playlist.coverURL = "";
        }
//        playlist.songs.addAll() - figure out this property; can just call a preexisting request
        playlist.playlistURI = jsonObject.getString("uri");
        return playlist;
    }

    public void savePlaylist(){
        //save all the fields to the parse database here
    }

    public String getPlaylistURI() {
        return playlistURI;
    }

    public String getName() {
        return name;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public List<Song> getSongs() {
        return songs;
    }

    //    public ParseUser getCreator() {
//        return creator;
//    }

    //create getter and setter methods for the keys
}
