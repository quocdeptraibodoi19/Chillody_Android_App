package com.example.chillody.Model;

import android.app.Application;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;

public class SingletonExoPlayer {
    // There are currently 3 types of ExoPlayer corresponding to our layouts: chilling, cafe and ghibli
    // In addition to it, there may be 3 other types which are lovechill, lovecafe and loveghibli
    private String type="";
    private final ExoPlayer exoPlayer;
    private static SingletonExoPlayer singletonExoPlayer = null;
    private boolean isThreadProcessing = false;
    private boolean isErrorProcessed = false;
    private int UpdatingUIFlag = -1;
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
    public void setThreadProcessing(boolean isStart){
        isThreadProcessing = isStart;
    }
    public boolean isThreadProcessing() {
        return isThreadProcessing;
    }
    public void setUIUpdatingFlag(int updateIndex){
        this.UpdatingUIFlag = updateIndex;
    }
    public int getUIUpdatingFlag(){
        return this.UpdatingUIFlag;
    }
    public boolean IsErrorProcessed(){
        return this.isErrorProcessed;
    }
    public void setErrorProcessedFlag(boolean isErrorProcessed){
        this.isErrorProcessed = isErrorProcessed;
    }
}
