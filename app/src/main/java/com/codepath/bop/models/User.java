package com.codepath.bop.models;

import android.util.Log;
import android.widget.Toast;

import com.codepath.bop.activities.MainActivity;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseObject {

    //clean up this model class

    //class constants
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CURRENT_SONG = "currentSong";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PROFILE_PIC = "profilePic";


    //instance variables
    private String username;
    private String password;
    private ParseGeoPoint location;
    private static Song currentSong;
    private String fullName;
    private ParseFile profilePic;

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
            currentUser.put(KEY_CURRENT_SONG, song);
            currentUser.saveInBackground();
            Log.i("User", "currentSong saved");
        }
    }

    private ParseFile getProfilePic(){
        return profilePic;
    }

    private void setProfilePic(ParseFile pic){
        profilePic = pic;
        //check save ParseFile code written here
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            currentUser.put(KEY_PROFILE_PIC, pic);
            currentUser.saveInBackground();
            Log.i("User", "profilePic saved");
        }
    }

    //create getter and setter methods for the keys
}
