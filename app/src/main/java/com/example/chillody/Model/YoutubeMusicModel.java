package com.example.chillody.Model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

public class YoutubeMusicModel extends AndroidViewModel {
    private final List<YoutubeMusicElement> youtubeMusicElementList;
    private int lastUpdateIndex;
    // This element is used to mark if the MusicLayout successfully update the UI in the complete way.
    private boolean isSuccesfulUpdateUI = false;
    public YoutubeMusicModel(@NonNull Application application) {
        super(application);
        youtubeMusicElementList = new ArrayList<>();
        lastUpdateIndex = 0;
    }
    public void AddMusicElement(YoutubeMusicElement element){
        youtubeMusicElementList.add(element);
    }
    // yes for successfully update the UI, and vice versa
    public boolean isSuccesfulUpdateUI(){
        return isSuccesfulUpdateUI;
    }
    // true for not update UI and false for having updated UI
    public void setSuccesfulUpdateUI(boolean mark){
        isSuccesfulUpdateUI = mark;
    }
    public YoutubeMusicElement getMusicElement(int index){
        return youtubeMusicElementList.get(index);
    }
    public boolean isLastSongInList(int curIndex){
        Log.d("QuocBug", "isLastSongInList: "+String.valueOf(curIndex)+" - "+String.valueOf(youtubeMusicElementList.size()-1));
        return curIndex == youtubeMusicElementList.size() - 1;
    }

    public int getLastUpdateIndex(){
        return lastUpdateIndex;
    }
    public void setLastUpdateIndex(int index){
        lastUpdateIndex = index;
    }
    // this method is for the sake of debugging
    public int getLengthYoutubeList(){
        return youtubeMusicElementList.size();
    }
}
