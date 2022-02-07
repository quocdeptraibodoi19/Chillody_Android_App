package com.example.chillody.Model;

import java.util.ArrayList;
import java.util.List;

public class SoundCloudMusicModel {
    private List<SoundCloudMusicElement> soundCloudMusicElementList;
    private int cur;
    private int lastUpdateIndex;
    public SoundCloudMusicModel(){
        soundCloudMusicElementList = new ArrayList<>();
        cur = -1;
        lastUpdateIndex = -1;
    }
    public void addElement(SoundCloudMusicElement element){
        soundCloudMusicElementList.add(element);
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
