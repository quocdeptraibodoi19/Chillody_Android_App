package com.example.chillody.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "General_Song_Table")
public class YoutubeMusicElement {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "MusicID")
    private String musicID;
    @ColumnInfo(name = "DownloadUrl")
    private String DownloadedMusicUrl;
    @NonNull
    @ColumnInfo(name = "MusicTitle")
    private String title;
    @NonNull
    @ColumnInfo(name = "MusicType")
    private String musicType;
    @ColumnInfo(name = "IsFavorite")
    private boolean isFavorite = false;

    public YoutubeMusicElement(){
        musicID = "";
        DownloadedMusicUrl = "";
        title = "";
        musicType = "";
        isFavorite = false;
    }
    public YoutubeMusicElement(@NonNull String title, @NonNull String musicID){
        this.title = title;
        this.musicID = musicID;
        musicType = "";
    }
    public YoutubeMusicElement(@NonNull String title, @NonNull String musicID, String DownloadedMusicUrl){
        this.musicID= musicID;
        this.title = title;
        this.DownloadedMusicUrl = DownloadedMusicUrl;
        musicType = "";
    }
    public YoutubeMusicElement(@NonNull String title, @NonNull String musicID, String DownloadedMusicURrl, int islove){
        this.title = title;
        this.musicID = musicID;
        this.DownloadedMusicUrl = DownloadedMusicURrl;
        musicType = "";
        this.isFavorite = (islove != 0);
    }
    public YoutubeMusicElement(@NonNull String title, @NonNull String musicID, String DownloadedMusicURrl, @NonNull String musicType,int islove){
        this.title = title;
        this.musicID = musicID;
        this.DownloadedMusicUrl = DownloadedMusicURrl;
        this.isFavorite = (islove != 0);
        this.musicType = musicType;
    }

    public void setTitle(@NonNull String title){
        this.title = title;
    }
    @NonNull
    public String getTitle() {
        return title;
    }

    public void setDownloadedMusicUrl(String downloadedMusicUrl) {
        this.DownloadedMusicUrl = downloadedMusicUrl;
    }
    public String getDownloadedMusicUrl() {
        return DownloadedMusicUrl;
    }

    public void setMusicID(@NonNull String musicID){
        this.musicID = musicID;
    }
    @NonNull
    public String getMusicID() {
        return musicID;
    }

    public void setMusicType(@NonNull String type){
        this.musicType = type;
    }
    @NonNull
    public String getMusicType(){
        return musicType;
    }
    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }
    public boolean isFavorite(){
        return this.isFavorite;
    }
}
