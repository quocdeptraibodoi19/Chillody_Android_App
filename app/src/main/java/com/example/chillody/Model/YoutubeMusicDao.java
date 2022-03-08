package com.example.chillody.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface YoutubeMusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMusicDao(YoutubeMusicElement element);

    @Query("SELECT * FROM General_Song_Table WHERE MusicType = :type")
    LiveData<List<YoutubeMusicElement>> getMusicElementList(String type);

    @Delete
    void deleteMusicElement(YoutubeMusicElement element);

    @Query("DELETE FROM General_Song_Table WHERE MusicType = :type")
    void deleteMusicType(String type);
}
