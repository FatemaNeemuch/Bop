package com.codepath.bop.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

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

    public User fromAPI(){
        User user = new User();
        //set all the fields here
        return user;
    }

    public void saveUser(){
        //save all the fields to the parse database here
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
    }

    //create getter and setter methods for the keys
}
