package com.codepath.bop.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("Song")
public class Song extends ParseObject {

    //class constants
    public static final String TAG = "Song Model";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_SONG_URI = "songURI";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_RELEASE_DATE = "releaseDate";
    public static final String KEY_COVER_URL = "coverURL";
    public static final String KEY_ALBUM_TYPE = "albumType";
    public static final String KEY_PLAYLIST = "playlist";
    public static final String KEY_IS_CURRENT_SONG = "isCurrentSong";

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

    public static void saveSong(Song song){
        song.setKeyAlbumType(song.albumType);
        song.setKeyAlbum(song.album);
        song.setKeyTitle(song.title);
        song.setKeyArtist(song.artist);
        song.setKeyCoverUrl(song.coverURL);
        song.setKeyReleaseDate(song.releaseDate);
        song.setKeySongUri(song.songURI);
        song.setKeyPlaylist(song.playlist);
        song.setKeyIsCurrentSong(song.isCurrentSong);
        song.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "error with saving", e);
                }
                Log.i(TAG, "song successfully saved");
            }
        });
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

    public boolean getisCurrentSong() {
        return isCurrentSong;
    }

    //parse getter and setter methods

    public String getKEY_TITLE() {
        return getString(KEY_TITLE);
    }

    public void setKeyTitle(String title){
        put(KEY_TITLE, title);
    }

    public String getKEY_ALBUM() {
        return getString(KEY_ALBUM);
    }

    public void setKeyAlbum(String album){
        put(KEY_ALBUM, album);
    }

    public String getKEY_SONG_URI() {
        return getString(KEY_SONG_URI);
    }

    public void setKeySongUri(String uri){
        put(KEY_SONG_URI, uri);
    }

    public String getKEY_ARTIST() {
        return getString(KEY_ARTIST);
    }

    public void setKeyArtist(String artist){
        put(KEY_ARTIST, artist);
    }

    public String getKEY_RELEASE_DATE() {
        return getString(KEY_RELEASE_DATE);
    }

    public void setKeyReleaseDate(String releaseDate){
        put(KEY_RELEASE_DATE, releaseDate);
    }

    public String getKEY_COVER_URL() {
        return getString(KEY_COVER_URL);
    }

    public void setKeyCoverUrl(String url){
        put(KEY_COVER_URL, url);
    }

    public String getKEY_ALBUM_TYPE() {
        return getString(KEY_ALBUM_TYPE);
    }

    public void setKeyAlbumType(String type){
        put(KEY_ALBUM_TYPE, type);
    }

    public ParseObject getKEY_PLAYLIST() {
        return getParseObject(KEY_PLAYLIST);
    }

    public void setKeyPlaylist(ParseObject playlist){
        put(KEY_PLAYLIST, playlist);
    }

    public String getKEY_IS_CURRENT_SONG() {
        return getString(KEY_IS_CURRENT_SONG);
    }

    public void setKeyIsCurrentSong(boolean status){
        put(KEY_IS_CURRENT_SONG, status);
    }
}
