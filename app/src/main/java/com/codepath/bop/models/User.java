package com.codepath.bop.models;

import android.util.Log;
import android.widget.Toast;

import com.codepath.bop.activities.MainActivity;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseObject {

    //might not need this model

    //class constants
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CURRENT_SONG = "currentSong";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_LOCATION = "location";


    //instance variables
    private String username;
    private String password;
    private ParseGeoPoint location;
    private static Song currentSong;
    private String fullName;

    public User() {}

    public User fromDatabase(){
        User user = new User();
        //set all the fields here
        return user;
    }

    //probs not needed
    public String getUsername() {
        return username;
    }

    //probs not needed
    public String getPassword() {
        return password;
    }

    public ParseGeoPoint getLocation() {
        return location;
    }

    //probs not needed
    public String getFullName() {
        return fullName;
    }

    public static Song getCurrentSong() {
        return currentSong;
    }

    public static void setCurrentSong(Song song) {
        currentSong = song;
        Song.saveSong(song);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            currentUser.put("currentSong", song);
            currentUser.saveInBackground();
            //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
            Log.i("User", "currentSong saved");
        }
    }

    //create getter and setter methods for the keys
}
