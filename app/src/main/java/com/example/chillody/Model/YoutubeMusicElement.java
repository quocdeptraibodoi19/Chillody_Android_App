package com.example.chillody.Model;

public class YoutubeMusicElement {
    private final String musicID;
    private String DownloadedMusicUrl;
    private final String title;
    private boolean isFavorite;
    public YoutubeMusicElement(String title,String musicID){
        this.title = title;
        this.musicID = musicID;
    }
    public YoutubeMusicElement(String title,String musicID, String DownloadedMusicUrl){
        this.musicID= musicID;
        this.title = title;
        this.DownloadedMusicUrl = DownloadedMusicUrl;
    }

    public String getTitle() {
        return title;
    }
    public String getDownloadedMusicUrl() {
        return DownloadedMusicUrl;
    }
    public String getMusicID() {
        return musicID;
    }
    public void setDownloadedMusicUrl(String downloadedMusicUrl) {
        this.DownloadedMusicUrl = downloadedMusicUrl;
    }
    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }
    public boolean isFavorite(){
        return this.isFavorite;
    }
}
