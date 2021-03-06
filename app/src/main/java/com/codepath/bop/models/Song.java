package com.codepath.bop.models;

import android.os.Parcelable;
import android.util.Log;

import com.codepath.bop.Music;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@ParseClassName("Song")
@Parcel(analyze = Song.class)
public class Song extends ParseObject implements Parcelable, Music {

    //class constants
    public static final String TAG = "Song Model";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_SONG_URI = "songURI";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_RELEASE_DATE = "releaseDate";
    public static final String KEY_COVER_URL = "coverURL";
    public static final String KEY_ALBUM_TYPE = "albumType";
    public static final String KEY_IS_CURRENT_SONG = "isCurrentSong";

    //instance variables
    @SerializedName("song_song_uri")
    String songURI;
    @SerializedName("song_song_title")
    String title;
    @SerializedName("song_song_album")
    String album;
    @SerializedName("song_song_artist")
    String artist;
    @SerializedName("song_song_release_date")
    String releaseDate;
    @SerializedName("song_song_cover_url")
    String coverURL;
    @SerializedName("song_song_album_type")
    String albumType;
    @SerializedName("song_song_is_current_song")
    boolean isCurrentSong;

    public Song() {}

    //implement Parcelable
    public Song(String songURI, String title, String album, String artist, String releaseDate, String coverURL, String albumType, boolean isCurrentSong){
        this.songURI = songURI;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.coverURL = coverURL;
        this.albumType = albumType;
        this.isCurrentSong = isCurrentSong;
    }

    //implement Parcelable
    protected Song(android.os.Parcel in) {
        songURI = in.readString();
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        releaseDate = in.readString();
        coverURL = in.readString();
        albumType = in.readString();
        isCurrentSong = in.readByte() != 0;
    }

    //implement Parcelable
    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(songURI);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(releaseDate);
        dest.writeString(coverURL);
        dest.writeString(albumType);
        dest.writeByte((byte) (isCurrentSong ? 1 : 0));
    }

    //implement Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //implement Parcelable
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(android.os.Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public static Song fromAPI(JSONObject jsonObject) throws JSONException {
        //create new song object from API JsonObject
        Song song = new Song();
            song.albumType = jsonObject.getJSONObject("album").getString("album_type");
            song.album = jsonObject.getJSONObject("album").getString("name");
            song.title = jsonObject.getString("name");
            song.artist = jsonObject.getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
            //get all the artist names
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
            song.isCurrentSong = false;
        return song;
    }

    public static Song fromAlbumAPI(JSONObject jsonObject, Album album) throws JSONException {
        //create new song object from API jsonObject for an album
        Song song = new Song();
        song.albumType = "album";
        song.album = album.getAlbumName();
        song.title = jsonObject.getString("name");
        song.artist = jsonObject.getJSONArray("artists").getJSONObject(0).getString("name");
        //get all the artist names
        if (jsonObject.getJSONArray("artists").length() > 1){
            String artistName = "";
            for (int i = 1; i < jsonObject.getJSONArray("artists").length(); i++){
                artistName = artistName + ", " + jsonObject.getJSONArray("artists").getJSONObject(i).getString("name");
            }
            song.artist = song.artist + artistName;
        }
        song.coverURL = album.getCoverURL();
        song.releaseDate = album.getAlbumReleaseDate();
        song.songURI = jsonObject.getString("uri");
        song.isCurrentSong = false;
        return song;
    }

    //implement Music
    @Override
    public int getType() {
        return Music.TYPE_SONG;
    }

    //save song to Parse database
    public static void saveSong(Song song){
        song.setKeyAlbumType(song.albumType);
        song.setKeyAlbum(song.album);
        song.setKeyTitle(song.title);
        song.setKeyArtist(song.artist);
        song.setKeyCoverUrl(song.coverURL);
        song.setKeyReleaseDate(song.releaseDate);
        song.setKeySongUri(song.songURI);
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

    public void setSongURI(Song song, String uri) {
        song.songURI = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCurrentSong(Song song){
        //update the status of last song played
        if (User.getCurrentSong() != null){
            User.getCurrentSong().isCurrentSong = false;
        }
        if (song != null){
            //update song status
            song.isCurrentSong = true;
            //set song as current song
            User.setCurrentSong(song);
        }else{
            ParseUser.getCurrentUser().remove(User.KEY_CURRENT_SONG);
            ParseUser.getCurrentUser().saveInBackground();
        }
    }

    public boolean getisCurrentSong() {
        return isCurrentSong;
    }

    public void setIsCurrentSong(Song song, boolean status){
        song.isCurrentSong = status;
    }

    //parse getter and setter methods

    public String getKEY_TITLE() {
        return getString(KEY_TITLE);
    }

    public void setKeyTitle(String title){
        put(KEY_TITLE, title);
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

    public void setKeyReleaseDate(String releaseDate){
        put(KEY_RELEASE_DATE, releaseDate);
    }

    public String getKEY_COVER_URL() {
        return getString(KEY_COVER_URL);
    }

    public void setKeyCoverUrl(String url){
        put(KEY_COVER_URL, url);
    }

    public void setKeyAlbumType(String type){
        put(KEY_ALBUM_TYPE, type);
    }

    public void setKeyIsCurrentSong(boolean status){
        put(KEY_IS_CURRENT_SONG, status);
    }
}
