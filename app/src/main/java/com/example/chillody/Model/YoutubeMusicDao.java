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
    // ignore here mean that if the conflict happen, the system will ignore it and consider everything as norm
    // if we turn it into abort, it means that if the conflict appears, the system will abort the inserting process ( it means that it will crash
    // the program)
    // Therefore, for the REPLACE works, we have to specify the ID such that when room insert the new one, it will look up at the previous IDs to check that whether the new ID already exists before.
    //Room, will not check and compare if you have the quote already in the DB. What it will do is look if the primary key already exists in the DB if it does, Room will replace all old data with the new one.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMusicDao(YoutubeMusicElement element);

    @Query("SELECT * FROM General_Song_Table WHERE MusicType = :type")
    LiveData<List<YoutubeMusicElement>> getMusicElementList(String type);

    @Query("UPDATE General_Song_Table SET IsFavorite = 1 WHERE MusicID = :songID ")
    void UpdateLikeMusicElement(String songID);
    @Query("UPDATE General_Song_Table SET IsFavorite = 0 WHERE MusicID = :songID")
    void UpdateDisLikeMusicElement(String songID);
    @Delete
    void deleteMusicElement(YoutubeMusicElement element);

    @Query("DELETE FROM General_Song_Table WHERE MusicType = :type")
    void deleteMusicType(String type);
}
