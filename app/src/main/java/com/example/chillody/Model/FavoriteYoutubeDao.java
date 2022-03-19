package com.example.chillody.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteYoutubeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMusicDao(FavoriteYoutubeElement element);

    @Delete
    void deleteMusicDao(FavoriteYoutubeElement element);

    @Query("UPDATE FAVORITE_MUSIC_TABLE SET MusicUrl = :Url WHERE MusicID = :songID")
    void updateDownloadUrl(String songID, String Url);

    @Query("SELECT * FROM Favorite_Music_Table WHERE MusicType = :Type ")
    LiveData<List<FavoriteYoutubeElement>> getFAVElementsInType(String Type);

    @Query("DELETE FROM Favorite_Music_Table")
    void deleteAllMusicDao();
}
