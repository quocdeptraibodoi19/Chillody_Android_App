package com.example.chillody.Model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneralSongRepository {
    private final YoutubeMusicDao youtubeMusicDao;
    private final LiveData<List<YoutubeMusicElement>> chillingList,cafeList,ghibliList;
    public GeneralSongRepository(Application application){
        RoomDatabase roomDatabase = RoomDatabase.getInstance(application);
        youtubeMusicDao = roomDatabase.youtubeMusicDao();
        chillingList = youtubeMusicDao.getMusicElementList("Chilling");
        cafeList = youtubeMusicDao.getMusicElementList("Cafe");
        ghibliList = youtubeMusicDao.getMusicElementList("Ghibli");
    }
    public LiveData<List<YoutubeMusicElement>> getChillingList(){
        return chillingList;
    }
    public LiveData<List<YoutubeMusicElement>> getCafeList(){
        return cafeList;
    }
    public LiveData<List<YoutubeMusicElement>> getGhibliList(){
        return ghibliList;
    }
    public void insertNewSong(YoutubeMusicElement element){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                youtubeMusicDao.insertMusicDao(element);
            }
        });
        service.shutdown();
    }
    public void deleteSong(YoutubeMusicElement element){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                youtubeMusicDao.deleteMusicElement(element);
            }
        });
        service.shutdown();
    }
    public void deleteSongMusicType(String type){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                youtubeMusicDao.deleteMusicType(type);
            }
        });
        service.shutdown();
    }
}
