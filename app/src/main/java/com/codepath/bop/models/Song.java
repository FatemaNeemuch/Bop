package com.codepath.bop.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Song {

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

    public static Song fromAPI(JSONObject jsonObject) throws JSONException {
        Song song = new Song();
            song.albumType = jsonObject.getJSONObject("album").getString("album_type");
            song.album = jsonObject.getJSONObject("album").getString("name");
            song.title = jsonObject.getString("name");
            song.artist = jsonObject.getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
            if (jsonObject.getJSONObject("album").getJSONArray("artists").length() > 1){
                String artistName = "";
                for (int i = 1; i < jsonObject.getJSONObject("album").getJSONArray("artists").length(); i++){
                    artistName = artistName + ", " + jsonObject.getJSONObject("album").getJSONArray("artists").getJSONObject(i).getString("name");
                }
                song.artist = song.artist + artistName;
            }
            song.coverURL = jsonObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
            song.releaseDate = jsonObject.getJSONObject("album").getString("release_date");
            song.songURI = jsonObject.getString("uri");
        return song;
    }

    public void saveSong(){
        //save all the fields to the parse database here
    }

    public String getSongURI() {
        return songURI;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public String getAlbumType() {
        return albumType;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setCurrentSong(Song song){
        //update the status of last song played
        if (User.getCurrentSong() != null){
            User.getCurrentSong().isCurrentSong = false;
        }
        //set song as current song
        User.setCurrentSong(song);
        //update song status
        isCurrentSong = true;
    }

    public boolean isCurrentSong() {
        return isCurrentSong;
    }
}
