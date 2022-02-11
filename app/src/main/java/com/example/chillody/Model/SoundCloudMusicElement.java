package com.example.chillody.Model;

import androidx.lifecycle.ViewModel;

import java.nio.charset.StandardCharsets;
// Because our project depends on the API available on the Internet. Therefore, this model is
// for the backup based on SoundCloud framework.
// Our main project depends on the Youtube framework
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
