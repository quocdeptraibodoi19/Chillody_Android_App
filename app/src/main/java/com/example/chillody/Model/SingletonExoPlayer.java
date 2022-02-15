package com.example.chillody.Model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;

public class SingletonExoPlayer {
    // There are currently 3 types of ExoPlayer corresponding to our layouts: chilling, cafe and ghibli
    private String type="";
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
    public void EndMusic(){
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
            Log.d("QuocBug", "EndMusic: "+ String.valueOf(exoPlayer.getMediaItemCount()));
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
