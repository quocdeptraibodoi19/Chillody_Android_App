package com.example.chillody.Networking;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogRecord;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Because our project depends on the API available on the Internet. Therefore, this model is
// for the backup based on SoundCloud framework.
// Our main project depends on the Youtube framework
public class SoundCloudExecutor extends AndroidViewModel {
    private ExecutorService executorService;
    private Application application;
    public SoundCloudExecutor(@NonNull Application application) {
        super(application);
        executorService = Executors.newFixedThreadPool(1);
        this.application = application;
    }
    public void StopProcess(){
        executorService.shutdownNow();
    }
    public void MusicProcess(String title, WeakReference<SoundCloudMusicModel> musicModelWeakReference){
        // use handler of OS android
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("QuocSoundcloud", "run: Inside the Executor");
                OkHttpClient client = new OkHttpClient();
                String EncodedTitle = new String(title.getBytes(StandardCharsets.UTF_8));
                Request request = new Request.Builder()
                        .url("https://soundcloud4.p.rapidapi.com/search?type=playlist&query="+EncodedTitle)
                        .get()
                        .addHeader("x-rapidapi-host", "soundcloud4.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    // bellow is the alternative of response.body().string()
                    JSONArray MusicList = new JSONArray(Objects.requireNonNull(response.body()).string());
                    String PlaylistURL;
                    for(int i=0; i<MusicList.length();i++){
                        PlaylistURL = MusicList.getJSONObject(i).getString("url");
                        musicModelWeakReference.get().AddPlaylist(PlaylistURL);
                    }
                    Log.d("QuocSoundcloud", "run: "+ String.valueOf(musicModelWeakReference.get().getLengthPlaylist()));
                    Log.d("QuocSoundcloud", "run: The curent playlist: "+ musicModelWeakReference.get().getCurrentPlaylist());
                    musicModelWeakReference.get().setCurPlaylist(musicModelWeakReference.get().getCurPlaylist()+1);
                    PlaylistURL = new String(musicModelWeakReference.get().getCurrentPlaylist().getBytes(StandardCharsets.UTF_8));
                     client = new OkHttpClient();
                     request = new Request.Builder()
                            .url("https://soundcloud4.p.rapidapi.com/playlist/info?playlist_url="+PlaylistURL)
                            .get()
                            .addHeader("x-rapidapi-host", "soundcloud4.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "2f7623ad77msh3137288b2a135acp188a6ajsndd873d37bf36")
                            .build();

                     response = client.newCall(request).execute();
                     MusicList = new JSONArray(Objects.requireNonNull(response.body()).string());
                    Log.d("QuocSoundcloud", "run: The Json body of current playlist"+ response.body().string());
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
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    SingletonExoPlayer SingletonexoPlayer = SingletonExoPlayer.getInstance(application);
                                    ExoPlayer exoPlayer = SingletonexoPlayer.getExoPlayer();
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
                            });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
