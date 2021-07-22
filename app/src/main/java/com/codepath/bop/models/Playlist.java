package com.codepath.bop.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

//probs not needed
@ParseClassName("Playlist")
public class Playlist extends ParseObject {

    //class constants
    public static final String KEY_NAME = "name";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_PLAYLIST_URI = "playlistURI";

    //instance variables
    private String playlistURI;
    private String name;
    private ParseUser creator;

    public Playlist() {
        playlistURI = "92usd93n9wf823n";
        name = "Favorites";
        creator = ParseUser.getCurrentUser();
    }

    public Playlist fromAPI(){
        Playlist playlist = new Playlist();
        //set all the fields here
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

    public ParseUser getCreator() {
        return creator;
    }

    //create getter and setter methods for the keys
}
