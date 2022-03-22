package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FavoriteUnsplashImgViewModel extends AndroidViewModel {
    private final UnsplashImageRepository repository;
    public FavoriteUnsplashImgViewModel(@NonNull Application application) {
        super(application);
        repository = new UnsplashImageRepository(application);

    }
    public void updateFavoriteUnsplashImg(UnsplashImgElement element){
        repository.updateFavoriteUnsplashImg(element);
    }
    public LiveData<List<UnsplashImgElement>> getFavoriteElementList(){
        return repository.getFavoriteList();
    }
}
