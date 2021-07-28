package com.codepath.bop.models;

import com.codepath.bop.Music;

public class Album implements Music {

    @Override
    public int getType() {
        return Music.TYPE_ALBUM;
    }
}
