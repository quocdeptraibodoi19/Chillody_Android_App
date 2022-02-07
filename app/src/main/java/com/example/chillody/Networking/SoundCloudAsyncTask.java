package com.example.chillody.Networking;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.chillody.Model.SoundCloudMusicElement;
import com.example.chillody.Model.SoundCloudMusicModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Todo: search for the use of WeakReference
public class SoundCloudAsyncTask extends AsyncTask<String,Void,Void> {
    private WeakReference<SoundCloudMusicModel> musicModelWeakReference;
    public SoundCloudAsyncTask(SoundCloudMusicModel model){
        musicModelWeakReference = new WeakReference<>(model);
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
            for(int i=0; i< MusicList.length();i++){
                elementTitle = MusicList.getJSONObject(i).getString("title");
                elementUrl = MusicList.getJSONObject(i).getString("url");
                musicModelWeakReference.get().addElement(new SoundCloudMusicElement(elementTitle,elementUrl));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
