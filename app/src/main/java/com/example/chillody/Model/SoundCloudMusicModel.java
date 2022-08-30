package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Because our project depends on the API available on the Internet. Therefore, this model is
// for the backup based on SoundCloud framework.
// Our main project depends on the Youtube framework
// using ViewModel combining with LiveData to retain the instance during the configuration change
public class SoundCloudMusicModel extends AndroidViewModel {
    private final List<SoundCloudMusicElement> soundCloudMusicElementList;
    private final List<String> Playlist;
    private int curPlaylist;
    private int cur;
    private int lastUpdateIndex;
    public SoundCloudMusicModel(@NonNull Application application){
        super(application);
        soundCloudMusicElementList = new ArrayList<>();
        Playlist = new ArrayList<>();
        curPlaylist = -1;
        cur = -1;
        lastUpdateIndex = -1;
    }
    public void addElement(SoundCloudMusicElement element){
        soundCloudMusicElementList.add(element);

    }
    public int getLengthPlaylist(){
        return Playlist.size();
    }
    public void AddPlaylist (String url){
        Playlist.add(url);
    }
    public String getCurrentPlaylist(){
        return Playlist.get(curPlaylist);
    }
    public void setCurPlaylist(int curPlaylist){
        this.curPlaylist = curPlaylist;
    }
    public int getCurPlaylist(){
        return curPlaylist;
    }
    public void setLastUpdateIndex(int index){
        lastUpdateIndex = index;
    }
    public int getLastUpdateIndex(){
        return lastUpdateIndex;
    }
    public void NextOne(){
        cur++;
    }
    public void PreviousOne(){
        if(cur !=0) cur--;
    }
    public int getLength(){
        return soundCloudMusicElementList.size();
    }
    public String getCurrentTitle(){
        return soundCloudMusicElementList.get(cur).getTitle();
    }
    public String getNextTitle(){
        return soundCloudMusicElementList.get(cur+1).getTitle();
    }
    public SoundCloudMusicElement getCurrentElement(){
        return soundCloudMusicElementList.get(cur);
    }
    public SoundCloudMusicElement getSoundcloudElement(int i){
        return soundCloudMusicElementList.get(i);
    }
    public int getCur(){
        return cur;
    }
}
