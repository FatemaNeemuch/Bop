package com.codepath.bop;

import java.io.File;

public class Song {
    //song object

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

    public Song() {}

    public Song fromAPI(){
        Song song = new Song();
        //set all the fields here
        return song;
    }

    public void saveSong(){
        //save all the fields to the parse database here
    }

}
