package com.codepath.bop.models;

import android.os.Parcelable;

import com.codepath.bop.Music;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel(analyze = {Artist.class})
public class Artist implements Music, Parcelable{

    //class constants
    public static final String TAG = "Artist";

    //instance variables
    @SerializedName("artist_name")
    private String artistName;
    @SerializedName("artist_image_url")
    private String artistImageURL;
    @SerializedName("artist_id")
    private String artistID;
    @SerializedName("artist_uri")
    private String artistURI;

    public Artist() {}

    //implement Parcelable
    protected Artist(android.os.Parcel in) {
        artistName = in.readString();
        artistImageURL = in.readString();
        artistID = in.readString();
        artistURI = in.readString();
    }

    //implement Parcelable
    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(artistImageURL);
        dest.writeString(artistID);
        dest.writeString(artistURI);
    }

    //implement Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //implement Parcelable
    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(android.os.Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    //implement Music
    @Override
    public int getType() {
        return Music.TYPE_ARTIST;
    }

    public static Artist fromAPI(JSONObject jsonObject) throws JSONException {
        //create new Artist object from API jsonObject
        Artist artist = new Artist();
        artist.artistName = jsonObject.getString("name");
        artist.artistID = jsonObject.getString("id");
        //check if Artist has a profile pic
        if (jsonObject.getJSONArray("images").length() != 0){
            artist.artistImageURL = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
        }else{
            //create empty string if no profile pic available
            artist.artistImageURL = "";
        }
        artist.artistURI = jsonObject.getString("uri");
        return artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistImageURL() {
        return artistImageURL;
    }

    public String getArtistID() {
        return artistID;
    }
}
