package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// using ViewModel combining with LiveData to retain the instance during the configuration change
// and livedata for updating immediately the result.
public class SoundCloudMusicModel extends AndroidViewModel {
    private MutableLiveData<List<SoundCloudMusicElement>> soundCloudMusicElementList;
    private int cur;
    private int lastUpdateIndex;
    public SoundCloudMusicModel(@NonNull Application application){
        super(application);
        soundCloudMusicElementList = new MutableLiveData<>();
        soundCloudMusicElementList.setValue(new ArrayList<>());
        cur = -1;
        lastUpdateIndex = -1;
    }
    public void addElement(SoundCloudMusicElement element){
        List<SoundCloudMusicElement> MusicElementList = soundCloudMusicElementList.getValue();
        MusicElementList.add(element);
        soundCloudMusicElementList.setValue(MusicElementList);

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
        return Objects.requireNonNull(soundCloudMusicElementList.getValue()).size();
    }
    public String getCurrentTitle(){
        return Objects.requireNonNull(soundCloudMusicElementList.getValue()).get(cur).getTitle();
    }
    public String getNextTitle(){
        return Objects.requireNonNull(soundCloudMusicElementList.getValue()).get(cur+1).getTitle();
    }
    public SoundCloudMusicElement getCurrentElement(){
        return Objects.requireNonNull(soundCloudMusicElementList.getValue()).get(cur);
    }
    public SoundCloudMusicElement getSoundcloudElement(int i){
        return Objects.requireNonNull(soundCloudMusicElementList.getValue()).get(i);
    }
    public int getCur(){
        return cur;
    }
}
