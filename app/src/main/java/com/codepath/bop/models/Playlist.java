package com.codepath.bop.models;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

@Parcel(analyze = {Playlist.class})
public class Playlist implements Parcelable {

    //class constants
    public static final String KEY_NAME = "name";
    public static final String KEY_PLAYLIST_URI = "playlistURI";

    //instance variables
    @SerializedName("playlist_name")
    String name;
    @SerializedName("playlist_cover")
    String coverURL;
    @SerializedName("playlist_id")
    String playlistID;
    @SerializedName("playlist_songs")
    List<Song> songs;
    @SerializedName("playlist_URI")
    String playlistURI;

    public Playlist() {}

    //implement Parcelable
    public Playlist(String name, String coverURL, String playlistURI, String playlistID){
        this.name = name;
        this.coverURL = coverURL;
        this.playlistURI = playlistURI;
        this.playlistID = playlistID;

    }

    //implement Parcelable
    protected Playlist(android.os.Parcel in) {
        name = in.readString();
        coverURL = in.readString();
        playlistURI = in.readString();
        playlistID = in.readString();
    }

    //implement Parcelable
    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(coverURL);
        dest.writeString(playlistURI);
        dest.writeString(playlistID);
    }

    //implement Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //implement Parcelable
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
        //create new Playlist object from API jsonObject
        Playlist playlist = new Playlist();
        playlist.name = jsonObject.getString("name");
        //check if playlist has a cover image
        if (jsonObject.getJSONArray("images").length() >= 1){
            playlist.coverURL = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        }else{
            //create empty string if no cover image
            playlist.coverURL = "";
        }
        playlist.playlistURI = jsonObject.getString("uri");
        playlist.playlistID = jsonObject.getString("id");
        return playlist;
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

    public String getPlaylistID(){
        return playlistID;
    }
}
