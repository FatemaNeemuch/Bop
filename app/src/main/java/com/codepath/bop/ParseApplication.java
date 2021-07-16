package com.codepath.bop;

import android.app.Application;

import com.codepath.bop.models.Playlist;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Register your parse models
//        ParseObject.registerSubclass(Song.class);
//        ParseObject.registerSubclass(Playlist.class);
//        ParseObject.registerSubclass(User.class);
        //set applicationID, and server based on the values in the back4app settings
        //client key is not needed unless explicitly configured
        //any network interceptors must be added with the Configuration builder given in syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("alha81HSaKUTRt7ErtFTp6JUJFQlTSIongrGEmY8")
                .clientKey("znmXwpBjy873hd6BbpT2ZkH3HAlQkCnwwjZaeNSe")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
