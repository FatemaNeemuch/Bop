package com.codepath.bop;

public class User {

    //instance variables
    private String username;
    private String password;
    private String location; //GeoPoint or Int?
    private Song currentSong;
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
}
