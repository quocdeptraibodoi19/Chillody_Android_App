package com.example.chillody.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FavoriteYoutubeViewModel extends AndroidViewModel {
    private FavSongRepository repository;
    public FavoriteYoutubeViewModel(@NonNull Application application) {
        super(application);
        repository = new FavSongRepository(application);
    }
    public LiveData<List<FavoriteYoutubeElement>> getFavChillingSongs(){
        return repository.GetAllChillingFAVSongs();
    }
    public LiveData<List<FavoriteYoutubeElement>> getFavGhibliSongs(){
        return repository.GetAllGhibliFAVSongs();
    }
    public LiveData<List<FavoriteYoutubeElement>> getFavCafeSongs(){
        return repository.GetAllCafeFAVSongs();
    }
    public void InsertFavoriteSongs(FavoriteYoutubeElement element){
        repository.InsertSongElement(element);
    }
    public void DeleteSongElements(FavoriteYoutubeElement element){
        repository.DeleteSongElement(element);
    }
    public void DeleteAllSongs(){
        repository.DeleteAllSongs();
    }
}
