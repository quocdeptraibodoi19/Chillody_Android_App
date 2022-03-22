package com.example.chillody.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UnsplashImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insertUnsplashImgDao(UnsplashImgElement element);

    @Query("SELECT * FROM unsplash_image_table WHERE Type = :type")
     LiveData<List<UnsplashImgElement>> getUnsplashImgListDao(String type);

    @Query("SELECT * FROM unsplash_image_table WHERE isFavorite = 1")
     LiveData<List<UnsplashImgElement>> getFavoriteList();

    // the reason why I use the regularUrl here is that regularUrl is set to be the primaryKey of the table
    @Query("UPDATE unsplash_image_table SET isFavorite = :isFavorite WHERE RegularUrl = :regularUrl")
    void updateFavoriteUnsplashImgDao(String regularUrl, int isFavorite);
}
