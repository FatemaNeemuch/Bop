package com.codepath.bop.models;

public class Playlist {

    //instance variables
    private String playlistURI;
    private String name;
    private User creator;

    public Playlist() {}

    public Playlist fromAPI(){
        Playlist playlist = new Playlist();
        //set all the fields here
        return playlist;
    }

    public void saveSong(){
        //save all the fields to the parse database here
    }
}
