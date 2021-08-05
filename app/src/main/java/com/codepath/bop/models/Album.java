package com.codepath.bop.models;

import android.os.Parcelable;

import com.codepath.bop.Music;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel(analyze = {Album.class})
public class Album implements Music, Parcelable {

    //class constants
    public static final String TAG = "Album";

    //instance variables
    @SerializedName("album_artist")
    private String artist;
    @SerializedName("album_id")
    private String albumID;
    @SerializedName("album_cover_url")
    private String coverURL;
    @SerializedName("album_name")
    private String albumName;
    @SerializedName("album_release_date")
    private String releaseDate;
    @SerializedName("album_num_tracks")
    private String numTracks;
    @SerializedName("album_uri")
    private String albumURI;

    public Album(){};

    //implement Parcelable
    protected Album(android.os.Parcel in) {
        artist = in.readString();
        albumID = in.readString();
        coverURL = in.readString();
        albumName = in.readString();
        releaseDate = in.readString();
        numTracks = in.readString();
        albumURI = in.readString();
    }

    //implement Parcelable
    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(artist);
        dest.writeString(albumID);
        dest.writeString(coverURL);
        dest.writeString(albumName);
        dest.writeString(releaseDate);
        dest.writeString(numTracks);
        dest.writeString(albumURI);
    }

    //implement Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //implement Parcelable
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(android.os.Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    //implement Music
    @Override
    public int getType() {
        return Music.TYPE_ALBUM;
    }

    public static Album fromAPI(JSONObject jsonObject) throws JSONException {
        //create new album object from API jsonObject
        Album album = new Album();
        album.albumName = jsonObject.getString("name");
        album.artist = jsonObject.getJSONArray("artists").getJSONObject(0).getString("name");
        //get all artist names
        if (jsonObject.getJSONArray("artists").length() > 1){
            String artistName = "";
            for (int i = 1; i < jsonObject.getJSONArray("artists").length(); i++){
                artistName = artistName + ", " + jsonObject.getJSONArray("artists").getJSONObject(i).getString("name");
            }
            album.artist = album.artist + artistName;
        }
        album.albumID = jsonObject.getString("id");
        album.coverURL = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        album.releaseDate = jsonObject.getString("release_date");
        album.numTracks = jsonObject.getString("total_tracks");
        album.albumURI = jsonObject.getString("uri");
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumID() {
        return albumID;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumReleaseDate() {
        return releaseDate;
    }

    public String getAlbumURI() {
        return albumURI;
    }
}
