package com.codepath.bop.models;

public class User {

    //instance variables
    private String username;
    private String password;
    private String location; //GeoPoint or Int?
    private static Song currentSong;
    private String fullName;

    public User() {}

    public User fromAPI(){
        User user = new User();
        //set all the fields here
        return user;
    }

    public void saveSong(){
        //save all the fields to the parse database here
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public String getFullName() {
        return fullName;
    }

    public static Song getCurrentSong() {
        return currentSong;
    }

    public static void setCurrentSong(Song song) {
        currentSong = song;
    }
}
