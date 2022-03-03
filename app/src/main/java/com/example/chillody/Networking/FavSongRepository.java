package com.example.chillody.Networking;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.chillody.Model.FavoriteYoutubeDao;
import com.example.chillody.Model.FavoriteYoutubeElement;
import com.example.chillody.Model.RoomDatabase;
import com.example.chillody.Model.YoutubeMusicElement;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavSongRepository {
    private final FavoriteYoutubeDao favoriteYoutubeDao;
    private final LiveData<List<FavoriteYoutubeElement>> chillingSongs;
    private final LiveData<List<FavoriteYoutubeElement>> cafeSongs;
    private final LiveData<List<FavoriteYoutubeElement>> ghibliSongs;
    public FavSongRepository(Application application){
        RoomDatabase roomDatabase = RoomDatabase.getInstance(application);
        favoriteYoutubeDao = roomDatabase.favoriteYoutubeDao();
        chillingSongs = favoriteYoutubeDao.getFAVElementsInType("ChillingLove");
        cafeSongs = favoriteYoutubeDao.getFAVElementsInType("CafeLove");
        ghibliSongs = favoriteYoutubeDao.getFAVElementsInType("GhibliLove");
    }
    //The reason why this method is not executed in the query thread is that
    // the livedata automatically query on the thread other than the UI thread.
    // Any other query methods must be executed in the worker thread explicitly.
    //By default, queries (@Query) must be executed on a thread other than the main thread.
    // For operations such as inserting or deleting,
    // Room takes care of thread management for you if you use the appropriate annotations.
    public LiveData<List<FavoriteYoutubeElement>> GetAllChillingFAVSongs(){
        return chillingSongs;
    };
    public LiveData<List<FavoriteYoutubeElement>> GetAllCafeFAVSongs(){
        return cafeSongs;
    };
    public LiveData<List<FavoriteYoutubeElement>> GetAllGhibliFAVSongs(){
        return  ghibliSongs;
    }
    public void InsertSongElement(FavoriteYoutubeElement element){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                favoriteYoutubeDao.insertMusicDao(element);

            }
        });
        executorService.shutdown();
    }
    public void DeleteSongElement(FavoriteYoutubeElement element){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                favoriteYoutubeDao.deleteMusicDao(element);
            }
        });
        executorService.shutdown();
    }
    public void DeleteAllSongs(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                favoriteYoutubeDao.deleteAllMusicDao();
            }
        });
        executorService.shutdown();
    }

}
