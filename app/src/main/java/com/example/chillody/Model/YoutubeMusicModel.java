package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

public class YoutubeMusicModel extends AndroidViewModel {
    private final List<YoutubeMusicElement> youtubeMusicElementList;
    private int curIndex;
    private int lastUpdateIndex;
    public YoutubeMusicModel(@NonNull Application application) {
        super(application);
        youtubeMusicElementList = new ArrayList<>();
        curIndex = -1;
        lastUpdateIndex = 0;
    }
    public void AddMusicElement(YoutubeMusicElement element){
        youtubeMusicElementList.add(element);
    }
    public YoutubeMusicElement getCurrentMusicElement(){
        return youtubeMusicElementList.get(curIndex);
    }
    public YoutubeMusicElement getMusicElement(int index){
        return youtubeMusicElementList.get(index);
    }
    public boolean isLastSongInList(){
        return curIndex == youtubeMusicElementList.size() - 1;
    }
    public void AddNext(){
        curIndex++;
    }
    public void AddPrevious(){
        curIndex--;
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
