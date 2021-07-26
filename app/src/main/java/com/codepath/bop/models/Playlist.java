package com.codepath.bop.models;

import android.os.Parcelable;

import com.codepath.bop.activities.MainActivity;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

@ParseClassName("Playlist")
@Parcel(analyze = {Playlist.class})
public class Playlist extends ParseObject implements Parcelable {

    //class constants
    public static final String KEY_NAME = "name";
//    public static final String KEY_CREATOR = "creator";
    public static final String KEY_PLAYLIST_URI = "playlistURI";

    //instance variables
    @SerializedName("playlist_name")
    String name;
    @SerializedName("playlist_cover")
    String coverURL;
    List<Song> songs;
    @SerializedName("playlist_URI")
    String playlistURI;
//    ParseUser creator;

    public Playlist() {
    }

    public Playlist(String name, String coverURL, String playlistURI){
        this.name = name;
        this.coverURL = coverURL;
        this.playlistURI = playlistURI;
    }

    protected Playlist(android.os.Parcel in) {
        name = in.readString();
        coverURL = in.readString();
        playlistURI = in.readString();
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(coverURL);
        dest.writeString(playlistURI);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(android.os.Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public static Playlist fromAPI(JSONObject jsonObject) throws JSONException {
        Playlist playlist = new Playlist();
        playlist.name = jsonObject.getString("name");
        if (jsonObject.getJSONArray("images").length() >= 1){
            playlist.coverURL = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        }else{
            playlist.coverURL = "";
        }
//        playlist.songs.addAll() - figure out this property; can just call a preexisting request
        playlist.playlistURI = jsonObject.getString("uri");
        return playlist;
    }

    public void savePlaylist(){
        //save all the fields to the parse database here
    }

    public String getPlaylistURI() {
        return playlistURI;
    }

    public String getName() {
        return name;
    }

    public String getCoverURL() {
        return coverURL;
    }

//    public List<Song> getSongs() {
//        return songs;
//    }

    //    public ParseUser getCreator() {
//        return creator;
//    }

    //create getter and setter methods for the keys
}
