package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GeneralYoutubeViewModel extends AndroidViewModel {
    private final GeneralSongRepository repository;
    public GeneralYoutubeViewModel(@NonNull Application application) {
        super(application);
        repository = new GeneralSongRepository(application);
    }
    public LiveData<List<YoutubeMusicElement>> getChillingSongs(){
        return repository.getChillingList();
    }
    public LiveData<List<YoutubeMusicElement>> getCafeSongs(){
        return repository.getCafeList();
    }
    public  LiveData<List<YoutubeMusicElement>> getGhibliSongs(){
        return repository.getGhibliList();
    }
    public void insertNewSong(YoutubeMusicElement element){
        repository.insertNewSong(element);
    }
    public void deleteSong(YoutubeMusicElement element){
        repository.deleteSong(element);
    }
    public void deleteSongMusicType(String type){
        repository.deleteSongMusicType(type);
    }
}
