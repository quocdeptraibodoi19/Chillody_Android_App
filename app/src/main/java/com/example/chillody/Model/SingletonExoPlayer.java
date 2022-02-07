package com.example.chillody.Model;

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;

public class SingletonExoPlayer {
    private final ExoPlayer exoPlayer;
    private static SingletonExoPlayer singletonExoPlayer = null;
    private SingletonExoPlayer(Context context){
        exoPlayer = new ExoPlayer.Builder(context).build();
    }
    public static SingletonExoPlayer getInstance(Context context){
        if(singletonExoPlayer == null)
            singletonExoPlayer = new SingletonExoPlayer(context);
        return singletonExoPlayer;
    }
    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
