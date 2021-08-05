package com.codepath.bop.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseObject {

    //class constants
    public static final String KEY_CURRENT_SONG = "currentSong";
    public static final String KEY_PROFILE_PIC_FILE = "profilePicFile";


    //instance variables
    private static Song currentSong;

    public User() {}

    public static Song getCurrentSong() {
        return currentSong;
    }

    public static void setCurrentSong(Song song) {
        currentSong = song;
        Song.saveSong(song);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            currentUser.put(KEY_CURRENT_SONG, song);
            currentUser.saveInBackground();
            Log.i("User", "currentSong saved");
        }
    }
}
