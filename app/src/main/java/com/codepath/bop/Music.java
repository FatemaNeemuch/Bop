package com.codepath.bop;

public interface Music {
    int TYPE_SONG = 101;
    int TYPE_ALBUM = 102;
    int TYPE_ARTIST = 103;
    int TYPE_PLAYLIST = 104;

    int getType();
}
