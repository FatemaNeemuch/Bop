package com.codepath.bop;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("alha81HSaKUTRt7ErtFTp6JUJFQlTSIongrGEmY8")
                .clientKey("znmXwpBjy873hd6BbpT2ZkH3HAlQkCnwwjZaeNSe")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
