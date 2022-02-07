package com.example.chillody.Model;

import androidx.lifecycle.ViewModel;

import java.nio.charset.StandardCharsets;

public class SoundCloudMusicElement {
    String title;
    String EncodedUrl;
    String DownloadedUrl;
    public SoundCloudMusicElement(String title, String Url){
        this.title = title;
        EncodedUrl = new String(Url.getBytes(StandardCharsets.UTF_8));
    }

    public void setDownloadedUrl(String downloadedUrl) {
        this.DownloadedUrl = downloadedUrl;
    }

    public String getDownloadedUrl() {
        return DownloadedUrl;
    }

    public String getEncodedUrl() {
        return EncodedUrl;
    }

    public String getTitle() {
        return title;
    }
}
