package com.example.chillody.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favorite_Music_Table")
public class FavoriteYoutubeElement {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "MusicID")
    private  String musicID;

    @ColumnInfo(name = "MusicUrl")
    private String DownloadedMusicUrl;

    @NonNull
    @ColumnInfo(name = "MusicTitle")
    private String title;

    @ColumnInfo(name = "MusicType")
    private String type;
    public FavoriteYoutubeElement(){
        title = "";
        musicID = "";
        DownloadedMusicUrl = "";
        type = "";
    }

    public FavoriteYoutubeElement(@NonNull String musicID, String downloadedMusicUrl, @NonNull String title, String type) {
        this.musicID = musicID;
        DownloadedMusicUrl = downloadedMusicUrl;
        this.title = title;
        this.type = type;
     }

    @NonNull
    public String getMusicID() {
        return musicID;
    }

    public void setMusicID(@NonNull String musicID){
        this.musicID = musicID;
    }
    public String getDownloadedMusicUrl() {
        return DownloadedMusicUrl;
    }

    public void setDownloadedMusicUrl(String downloadedMusicUrl) {
        DownloadedMusicUrl = downloadedMusicUrl;
    }
    @NonNull
    public String getTitle() {
        return title;
    }
    public void setTitle(@NonNull String title){
        this.title = title;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
    public YoutubeMusicElement getYoutubeMusicElement(){
        return new YoutubeMusicElement(getTitle(),getMusicID(),getDownloadedMusicUrl(),1);
    }
}
