package com.example.chillody.Model;

import android.app.Application;
import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;

public class SingletonExoPlayer {
    private final ExoPlayer exoPlayer;
    private static SingletonExoPlayer singletonExoPlayer = null;
    private SingletonExoPlayer(Application application){
        exoPlayer = new ExoPlayer.Builder(application).build();
    }
    public static SingletonExoPlayer getInstance(Application application){
        if(singletonExoPlayer == null)
            singletonExoPlayer = new SingletonExoPlayer(application);
        return singletonExoPlayer;
    }
    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
