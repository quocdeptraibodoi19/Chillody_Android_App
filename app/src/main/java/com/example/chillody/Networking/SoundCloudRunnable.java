package com.example.chillody.Networking;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;

import com.example.chillody.Model.SingletonExoPlayer;
import com.example.chillody.Model.SoundCloudMusicElement;
import com.example.chillody.Model.SoundCloudMusicModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Todo: search for the use of WeakReference
public class SoundCloudRunnable extends AsyncTask<String,Void,Void>{
    private WeakReference<SoundCloudMusicModel> musicModelWeakReference;
    private final WeakReference<Application> context;
    public SoundCloudRunnable(SoundCloudMusicModel model, Application context){
        musicModelWeakReference = new WeakReference<>(model);
        this.context = new WeakReference<>(context);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Void doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();
        String EncodedTitle = new String(strings[0].getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .url("https://soundcloud4.p.rapidapi.com/search?type=track&query="+EncodedTitle)
                .get()
                .addHeader("x-rapidapi-host", "soundcloud4.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                .build();

        try {
            Response response = client.newCall(request).execute();
            // bellow is the alternative of response.body().string()
            JSONArray MusicList = new JSONArray(Objects.requireNonNull(response.body()).string());
            String elementUrl;
            String elementTitle;
            // Need to update the last update index to later on feed the download url to the exoplayer bellow on the postExecute
            musicModelWeakReference.get().setLastUpdateIndex(musicModelWeakReference.get().getLength());
            for(int i=0; i< MusicList.length();i++){
                elementTitle = MusicList.getJSONObject(i).getString("title");
                elementUrl = MusicList.getJSONObject(i).getString("url");
                musicModelWeakReference.get().addElement(new SoundCloudMusicElement(elementTitle,elementUrl));
                // This is to download the song responding to the URL via the API to download songs from the SoundCloud
                client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, "url="+musicModelWeakReference.get().getSoundcloudElement(musicModelWeakReference.get().getLength()-1).getEncodedUrl());
                 request = new Request.Builder()
                        .url("https://soundcloud-songs-downloader.p.rapidapi.com/")
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("x-rapidapi-host", "soundcloud-songs-downloader.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                        .build();
                response = client.newCall(request).execute();
                JSONObject data = new JSONObject(Objects.requireNonNull(response.body()).string());
                String url = data.getJSONObject("response").getJSONArray("links").getJSONObject(0).getString("url");
                musicModelWeakReference.get().getSoundcloudElement(i).setDownloadedUrl(url);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        SingletonExoPlayer singletonExoPlayer = SingletonExoPlayer.getInstance(context.get());
        ExoPlayer exoPlayer = singletonExoPlayer.getExoPlayer();
        for(int i=musicModelWeakReference.get().getLastUpdateIndex();i<musicModelWeakReference.get().getLength();i++){
            MediaItem item = MediaItem.fromUri(musicModelWeakReference.get().getSoundcloudElement(i).getDownloadedUrl());
            exoPlayer.addMediaItem(item);
        }
        if(!exoPlayer.isPlaying())
        {
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }
}
