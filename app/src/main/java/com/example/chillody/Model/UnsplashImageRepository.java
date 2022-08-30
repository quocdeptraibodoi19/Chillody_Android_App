package com.example.chillody.Model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnsplashImageRepository {
    private final UnsplashImageDao unsplashImageDao;
    public UnsplashImageRepository(Application application){
        RoomDatabase roomDatabase = RoomDatabase.getInstance(application);
        unsplashImageDao = roomDatabase.unsplashImageDao();
    }
    public LiveData<List<UnsplashImgElement>> getUnsplashImgList(String type){
        return unsplashImageDao.getUnsplashImgListDao(type);
    }
    public void insertUnsplashImgElement(UnsplashImgElement element){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                unsplashImageDao.insertUnsplashImgDao(element);
            }
        });
        service.shutdown();
    }
    public LiveData<List<UnsplashImgElement>> getFavoriteList(){
        return unsplashImageDao.getFavoriteList();
    }
    public void updateFavoriteUnsplashImg(UnsplashImgElement element){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                unsplashImageDao.updateFavoriteUnsplashImgDao(element.getRegularURL(),element.getIsFavorite());
            }
        });
        service.shutdown();
    }
}
