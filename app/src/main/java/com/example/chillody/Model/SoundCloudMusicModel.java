package com.example.chillody.Model;

import java.util.ArrayList;
import java.util.List;

public class SoundCloudMusicModel {
    private List<SoundCloudMusicElement> soundCloudMusicElementList;
    private int cur;
    public SoundCloudMusicModel(){
        soundCloudMusicElementList = new ArrayList<>();
        cur = 0;
    }
    public void addElement(SoundCloudMusicElement element){
        soundCloudMusicElementList.add(element);
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
}
